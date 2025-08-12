package com.S1_K4.ForkMe_BE.modules.on_project.board.controller;

import com.S1_K4.ForkMe_BE.global.common.s3.S3Service;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.*;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.board.service.BoardInProjectService;
import com.S1_K4.ForkMe_BE.modules.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.controller
 * @fileName : BoardInProjectController
 * @date : 2025-08-08
 * @description : 워크스페이스 내 게시판 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/on-project/{projectPk}/boards")
public class BoardInProjectController {

    private final BoardInProjectService boardInProjectService;
    private final S3Service s3Service;


    // 게시글 생성 (첨부파일 포함 가능)

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InBoardDetailResponse> createBoard(
            //@AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectPk,
            @RequestPart("request") InBoardCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {


        //Long userPk = userDetails.getUserPk();
        Long userPk = request.getUserPk();

        System.out.println("=== InBoardCreateRequest 확인 ===");
        System.out.println("title: " + request.getTitle());
        System.out.println("projectPk: " + request.getProjectPk());
        System.out.println("userPk: " + userPk);
        System.out.println("content: " + request.getContent());
        System.out.println("imageUrls: " + request.getImageUrls());
        System.out.println("fileUrls: " + request.getFileUrls());

        // S3에 이미지 업로드
        List<String> uploadedImageUrls = (images != null && !images.isEmpty())
                ? s3Service.uploadFileIn(images, "images").stream()
                .map(FileInfoResponse::getFileUrl)
                .collect(Collectors.toList())
                : List.of();

        //S3에 파일업로드
        List<FileInfoResponse> uploadedFileInfos = (files != null && !files.isEmpty())
                ? s3Service.uploadFileIn(files, "downloads/" + projectPk)
                : List.of();

        // 기존 + 새로 업로드한 이미지 URL 합치기
        List<String> allImageUrls = new ArrayList<>();
        if (request.getImageUrls() != null) allImageUrls.addAll(request.getImageUrls());
        allImageUrls.addAll(uploadedImageUrls);

        // 기존 + 새로 업로드한 파일 URL 합치기
        List<FileInfoResponse> allFileInfos = new ArrayList<>();
        if (request.getFileInfos() != null) allFileInfos.addAll(request.getFileInfos());
        allFileInfos.addAll(uploadedFileInfos);

        // DTO에 세팅
        request.setImageUrls(allImageUrls);
        request.setFileInfos(allFileInfos);

        BoardInProject savedBoard = boardInProjectService.createBoard(projectPk, userPk, request);

        InBoardDetailResponse response = InBoardDetailResponse.from(savedBoard, allImageUrls, allFileInfos);
        return ResponseEntity.ok(response);
    }


    //전체 조회
    @GetMapping
    public ResponseEntity<List<InBoardSimpleResponse>> getBoardsInProject(@PathVariable Long projectPk) {
        List<InBoardSimpleResponse> responses = boardInProjectService.getAllBoardsInProject(projectPk);
        return ResponseEntity.ok(responses);
    }

    //상세 조회
    // 게시글 상세 조회

    @GetMapping("/{boardInProjectPk}")
    public ResponseEntity<InBoardDetailResponse> getBoardDetail( @PathVariable Long boardInProjectPk) {
        InBoardDetailResponse response = boardInProjectService.getBoardDetail(boardInProjectPk);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{boardInProjectPk}/edit")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long projectPk,
            @PathVariable Long boardInProjectPk,
           // @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("request") InBoardUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        //Long loggedInUserPk = userDetails.getUserPk();  // 로그인한 유저의 PK
        Long loggedInUserPk = request.getUserPk();
        // 2. 게시글 작성자 userPk 가져오기 (서비스에서)
        Long authorUserPk = boardInProjectService.getAuthorUserPk(boardInProjectPk);

        // 3. 권한 체크
        if (!loggedInUserPk.equals(authorUserPk)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }


        boardInProjectService.updateBoard(projectPk, boardInProjectPk, request, images, files, loggedInUserPk);
        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }

    @DeleteMapping("/{boardInProjectPk}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long projectPk, @PathVariable Long boardInProjectPk) {
        boardInProjectService.deleteBoard(projectPk, boardInProjectPk);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/{boardInProjectPk}/files")
    public ResponseEntity<?> deleteFile(
            @PathVariable Long projectPk,
            @PathVariable Long boardInProjectPk,
            @RequestBody Map<String, String> requestBody
    ) {

        String key = requestBody.get("key");
        if (key == null || key.isBlank()) {
            return ResponseEntity.badRequest().body("key 값이 필요합니다.");
        }

        try {
            // S3에서 파일 삭제
            s3Service.deleteFile(key);

            // (선택) 게시글에서 해당 파일 정보를 제거 (DB 또는 객체에서)
            boardInProjectService.removeFileFromBoard(boardInProjectPk, key);

            return ResponseEntity.ok("파일이 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 삭제 중 오류 발생");
        }
    }

    @GetMapping("/files/download")
    public ResponseEntity<String> downloadFile(@RequestParam String key, @RequestParam String originalFilename) {
        String presignedUrl = s3Service.generatePresignedDownloadUrl(key, 15, originalFilename);
        return ResponseEntity.ok(presignedUrl);
    }

}