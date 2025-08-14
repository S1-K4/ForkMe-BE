package com.S1_K4.ForkMe_BE.modules.on_project.board.service;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.global.common.s3.S3Service;
import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.*;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.board.repository.BoardFileRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.board.repository.BoardImageRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.board.repository.BoardInProjectRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.CommentInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.repository.CommentInProjectRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectProfileRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3File;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3Image;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.service
 * @fileName : BoardInProjectServiceImpl
 * @date : 2025-08-08
 * @description : 워크스페이스 내 게시판 serviceImpl 클래스
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardInProjectServiceImpl implements BoardInProjectService {

    private static final int DEFAULT_IMAGE_SLOT_COUNT = 5;

    private final BoardInProjectRepository boardInProjectRepository;
    private final S3Service s3Service;
    private final BoardImageRepository boardImageRepository;
    private final BoardFileRepository boardFileRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CommentInProjectRepository commentInProjectRepository;

    // 게시판 생성
    @Transactional
    public BoardInProject createBoard(Long projectPk, Long userPk, InBoardCreateRequest request) {
        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다. userPk=" + userPk));

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다. projectPk=" + projectPk));

        // ❗ projectProfile이 null인지 먼저 확인
        ProjectProfile projectProfile = project.getProjectProfile();


        String pureContent = removeImageMarkdown(request.getContent());

        BoardInProject board = BoardInProject.create(request.getTitle(), pureContent, project, user);
        BoardInProject savedBoard = boardInProjectRepository.save(board);
        System.out.println("projectProfile : "+projectProfile);

        // 이미지 URL 저장 (BoardInProject 관련 이미지)
        if (request.getImageUrls() != null) {
            for (String url : request.getImageUrls()) {
                S3Image image = projectProfile != null
                        ? S3Image.create(url, projectProfile, board)
                        : S3Image.create(url, null, board);
                boardImageRepository.save(image);
            }
        } else {
            log.info("이미지 URL 리스트가 비어있음");
        }

        // 파일 URL 저장
        if (request.getFileInfos() != null) {
            for (FileInfoResponse fileInfo : request.getFileInfos()) {
                S3File boardFile = S3File.create(fileInfo.getFileUrl(), fileInfo.getOriginalFileName(), savedBoard);
                boardFileRepository.save(boardFile);
            }
        }

        return savedBoard;
    }


    private String removeImageMarkdown(String markdown) {
        if (markdown == null) return null;
        return markdown.replaceAll("!\\[[^\\]]*\\]\\([^\\)]*\\)", "");
    }


    public List<InBoardSimpleResponse> getAllBoardsInProject(Long projectPk) {
        List<BoardInProject> boards = boardInProjectRepository.findByProject_ProjectPkAndDeletedYNOrderByCreatedAtDesc(projectPk, Yn.N);

        return boards.stream()
                .map(InBoardSimpleResponse::from)
                .collect(Collectors.toList());
    }


    // 게시글 상세보기
    @Transactional(readOnly = true)
    public InBoardDetailResponse getBoardDetail(Long boardInProjectPk) {
        BoardInProject board = boardInProjectRepository.findById(boardInProjectPk)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID=" + boardInProjectPk));

        List<S3Image> boardImages = boardImageRepository.findByBoardInProject(board);
        List<String> imageUrls = boardImages.stream()
                .map(S3Image::getUrl)
                .collect(Collectors.toList());

        // 첨부 파일 가져오기
        List<S3File> boardFiles = boardFileRepository.findByBoardInProject(board);
        List<FileInfoResponse> fileInfos = boardFiles.stream()
                .map(file -> new FileInfoResponse(file.getUrl(), file.getOriginalFileName()))
                .collect(Collectors.toList());

        return InBoardDetailResponse.builder()
                .boardInProjectPk(board.getBoardInProjectPk())
                .projectPk(board.getProject().getProjectPk())
                .userPk(board.getUser().getUserPk())
                .userNickname(board.getUser().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .imageUrls(imageUrls)
                .fileInfos(fileInfos)
                .build();
    }


    //게시글 수정
    @Transactional
    public BoardInProject updateBoard(Long projectPk, Long boardInProjectPk, InBoardUpdateRequest request,
                                      List<MultipartFile> newImages, List<MultipartFile> newFiles,Long userPk) {

        BoardInProject board = boardInProjectRepository.findById(boardInProjectPk)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new RuntimeException("프로젝트가 존재하지 않습니다."));

        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        ProjectProfile projectProfile = project.getProjectProfile();
        if (projectProfile == null) {
            throw new IllegalStateException("해당 프로젝트에 연결된 ProjectProfile이 없습니다. projectPk=" + projectPk);
        }

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setProject(project);
        board.setUser(user);
        boardInProjectRepository.save(board);



        List<String> removedImageUrls = request.getRemovedImageUrls();
        if (removedImageUrls != null && !removedImageUrls.isEmpty()) {
            // 1. S3에서 실제 파일 삭제
            for (String url : removedImageUrls) {
                String key = extractS3Key(url);
                s3Service.deleteFile(key);  // 실제 삭제 메서드 호출
            }

            // 2. DB에서 이미지 메타데이터 삭제
            boardImageRepository.deleteByUrls(removedImageUrls);  // 커스텀 쿼리 필요
        }
        System.out.println("삭제된 이미지 URL 리스트 (하드 삭제): " + removedImageUrls);

        List<String> removedFileUrls = request.getRemovedFileUrls(); // 삭제할 파일 URL 리스트
        if (removedFileUrls != null && !removedFileUrls.isEmpty()) {
            for (String url : removedFileUrls) {
                String key = extractS3Key(url); // S3 key 추출
                s3Service.deleteFile(key);    // 실제 S3 파일 삭제
            }

            boardFileRepository.deleteByUrls(removedFileUrls); // DB에서 삭제
        }
        System.out.println("삭제된 파일 URL 리스트 (하드 삭제): " + removedFileUrls);
        // 3) 새 이미지 업로드 (S3Image 엔티티 저장)
        List<FileInfoResponse> uploadedImages = List.of();
        if (newImages != null && !newImages.isEmpty()) {
            uploadedImages = s3Service.uploadFileIn(newImages, "images");
        }

        List<String> uploadedImageUrls = uploadedImages.stream()
                .map(FileInfoResponse::getFileUrl)
                .collect(Collectors.toList());

        List<S3Image> newS3Images = uploadedImages.stream()
                .map(fileInfo -> S3Image.create(fileInfo.getFileUrl(), projectProfile, board))
                .collect(Collectors.toList());
        boardImageRepository.saveAll(newS3Images);

        // 새 파일 업로드
        List<FileInfoResponse> uploadedFiles = List.of();
        if (newFiles != null && !newFiles.isEmpty()) {
            uploadedFiles = s3Service.uploadFileIn(newFiles, "downloads/" + projectPk);
        }

        // 업로드된 파일 URL만 추출
        List<String> uploadedFileUrls = uploadedFiles.stream()
                .map(FileInfoResponse::getFileUrl)
                .collect(Collectors.toList());

        // 새로 업로드된 S3File 엔티티 저장 (파일)
        List<S3File> newS3Files = new ArrayList<>();
        for (FileInfoResponse fileInfo : uploadedFiles) {
            S3File s3File = S3File.builder()
                    .url(fileInfo.getFileUrl())
                    .originalFileName(fileInfo.getOriginalFileName())  // 추가
                    .boardInProject(board)
                    .build();
            newS3Files.add(s3File);
        }

        boardFileRepository.saveAll(newS3Files);

        return board;
    }

// 삭제
    @Transactional
    public void deleteBoard(Long projectPk, Long boardInProjectPk) {
        BoardInProject board = boardInProjectRepository.findById(boardInProjectPk)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (!board.getProject().getProjectPk().equals(projectPk)) {
            throw new RuntimeException("해당 프로젝트에 속한 게시글이 아닙니다.");
        }

        // 이미지 하드 삭제
        List<S3Image> images = boardImageRepository.findByBoardInProject(board);
        boardImageRepository.deleteAll(images);

        // 파일 하드 삭제
        List<S3File> files = boardFileRepository.findByBoardInProject(board);
        boardFileRepository.deleteAll(files);

        //게시글 댓글 하드 삭제
        List<CommentInProject> commentInProjectList = commentInProjectRepository.findByBoardInProject(board);
        commentInProjectRepository.deleteAll(commentInProjectList);

        // 게시글 소프트 삭제
        board.markDeleted();
        boardInProjectRepository.save(board);
    }

    private String extractS3Key(String url) {
        // 예: https://bucket.s3.amazonaws.com/images/foo.jpg → images/foo.jpg
        int index = url.indexOf(".amazonaws.com/");
        if (index != -1) {
            return url.substring(index + ".amazonaws.com/".length());
        }
        return url;
    }

    @Transactional
    public void removeFileFromBoard(Long boardInProjectPk, String key) {
        BoardInProject board = boardInProjectRepository.findById(boardInProjectPk)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

        List<S3File> files = board.getFiles();
        if (files != null) {
            files.removeIf(file -> file.getUrl() != null && file.getUrl().contains(key));
        }
        // 보통 cascade + orphanRemoval이면 save 안 해도 됩니다.
    }

    public Long getAuthorUserPk(Long boardInProjectPk) {
        BoardInProject board = boardInProjectRepository.findById(boardInProjectPk)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        return board.getUser().getUserPk();
    }

}