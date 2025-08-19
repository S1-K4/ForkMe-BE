package com.S1_K4.ForkMe_BE.modules.on_project.board.controller;

import com.S1_K4.ForkMe_BE.global.common.s3.S3Service;
import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.*;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.board.service.BoardInProjectService;
import com.S1_K4.ForkMe_BE.modules.project.service.ProjectService;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3Image;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    public ResponseEntity<ApiResponse<?>> createBoard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectPk,
            @RequestPart("request")InBoardCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        System.out.println("board controller 작동");

        Long userPk = userDetails.getUserPk();
        if (images != null && images.size() > 5) {
            ApiResponse<?> errorResponse = ApiResponse.error(400, "이미지는 최대 5장까지만 업로드할 수 있습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        BoardInProject savedBoard = boardInProjectService.createBoard(projectPk, userPk, request,images, files);
        List<String> imageUrls=  savedBoard.getImages().stream()
                .map(S3Image::getUrl)
                .collect(Collectors.toList());

        List<FileInfoResponse> fileInfos = savedBoard.getFiles().stream()
                .map(file -> new FileInfoResponse(file.getUrl(), file.getOriginalFileName()))
                .collect(Collectors.toList());

        InBoardDetailResponse response = InBoardDetailResponse.from(savedBoard, imageUrls, fileInfos);
        return ResponseEntity.ok(ApiResponse.success("프로젝트 번호 : "+ projectPk, "프로젝트 생성 완료"));
    }


    //전체 조회
    @GetMapping
    public ResponseEntity<Page<InBoardSimpleResponse>> getBoardsInProject(@PathVariable Long projectPk,
                                                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {

        Long userPk = userDetails.getUserPk();
        Pageable pageable = PageRequest.of(page, size);
        Page<InBoardSimpleResponse> responses = boardInProjectService.getAllBoardsInProject(projectPk, pageable, userPk);
        return ResponseEntity.ok(responses);
    }

    //상세 조회
    // 게시글 상세 조회

    @GetMapping("/{boardInProjectPk}")
    public ResponseEntity<InBoardDetailResponse> getBoardDetail( @PathVariable Long boardInProjectPk,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userPk = userDetails.getUserPk();
        InBoardDetailResponse response = boardInProjectService.getBoardDetail(boardInProjectPk,userPk);
        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @PutMapping(value = "/{boardInProjectPk}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBoard(
            @PathVariable Long projectPk,
            @PathVariable Long boardInProjectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("request") InBoardUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        Long loggedInUserPk = userDetails.getUserPk();  // 1. 로그인한 유저의 PK

        // 이미지 갯수 제한 (현재 : 5개)
        if (images != null && images.size() > 5) {
            ApiResponse<?> errorResponse = ApiResponse.error(400, "이미지는 최대 5장까지만 업로드할 수 있습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 2. 게시글 작성자 userPk 가져오기 (서비스에서)
        Long authorUserPk = boardInProjectService.getAuthorUserPk(boardInProjectPk);

        // 3. 권한 체크
        if (!loggedInUserPk.equals(authorUserPk)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }


        boardInProjectService.updateBoard(projectPk, boardInProjectPk, request, images, files, loggedInUserPk);
        return ResponseEntity.ok(ApiResponse.success("프로젝트 번호 : "+ projectPk, "프로젝트 수정 완료"));
    }

    @DeleteMapping("/{boardInProjectPk}")
    public ResponseEntity<ApiResponse<?>> deleteBoard(@PathVariable Long projectPk,
                                            @PathVariable Long boardInProjectPk,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 현재 로그인한 유저pk 가져오기
        Long loggedInUserPk = userDetails.getUserPk();

        boardInProjectService.deleteBoard(projectPk, boardInProjectPk, loggedInUserPk);
        return ResponseEntity.ok(ApiResponse.success("프로젝트 번호 : "+ projectPk, "프로젝트 삭제 완료"));
    }

    @DeleteMapping("/{boardInProjectPk}/files")
    public ResponseEntity<ApiResponse<?>> deleteFile(
            @PathVariable Long projectPk,
            @PathVariable Long boardInProjectPk,
            @RequestBody Map<String, String> requestBody
    ) {

        String key = requestBody.get("key");
        if (key == null || key.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "key 값이 필요합니다."));
        }

        try {
            // S3에서 파일 삭제
            s3Service.deleteFile(key);

            // (선택) 게시글에서 해당 파일 정보를 제거 (DB 또는 객체에서)
            boardInProjectService.removeFileFromBoard(boardInProjectPk, key);

            return ResponseEntity.ok(ApiResponse.success("프로젝트 번호 : "+ projectPk, "프로젝트 삭제 완료"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "파일 삭제 중 오류 발생"));
        }
    }

    @GetMapping("/files/download")
    public ResponseEntity<String> downloadFile(@RequestParam String key, @RequestParam String originalFilename) {
        String presignedUrl = s3Service.generatePresignedDownloadUrl(key, 15, originalFilename);
        return ResponseEntity.ok(presignedUrl);
    }

}