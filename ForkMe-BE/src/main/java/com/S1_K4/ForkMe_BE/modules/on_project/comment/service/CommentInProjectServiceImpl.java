package com.S1_K4.ForkMe_BE.modules.on_project.comment.service;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.board.repository.BoardInProjectRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentUpdateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.CommentInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.repository.CommentInProjectRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.comment.service
 * @fileName : CommentInProjectServiceImpl
 * @date : 2025-08-10
 * @description :
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentInProjectServiceImpl implements CommentInProjectService {
    private final CommentInProjectRepository commentRepository;
    private final BoardInProjectRepository boardRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;


    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long userPk, Long boardInProjectPk) {
        // 1. 게시판 조회
        BoardInProject board = boardRepository.findById(boardInProjectPk)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 2. 사용자 조회
        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 3. 해당 유저 프로젝트 멤버인지 조회
        if(!projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(board.getProject().getProjectPk(), userPk)){
            throw new RuntimeException("해당 유저는 프로젝트 멤버가 아닙니다.");
        }

        // 3. 댓글 생성
        CommentInProject comment = CommentInProject.builder()
                .comment(request.getComment())
                .boardInProject(board)
                .user(user)
                .build();

        // 4. 저장
        CommentInProject saved = commentRepository.save(comment);

        return CommentResponse.from(saved);
    }

    public List<CommentResponse> getCommentsByBoardId(Long boardPk, Long userPk) {
        // 1. 게시판 조회
        BoardInProject board = boardRepository.findById(boardPk)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 2. 사용자 조회
        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 3. 해당 유저 프로젝트 멤버인지 조회
        if(!projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(board.getProject().getProjectPk(), userPk)){
            throw new RuntimeException("해당 유저는 프로젝트 멤버가 아닙니다.");
        }
        return commentRepository.findByBoardInProject_BoardInProjectPkAndDeletedYNOrderByCreatedAtAsc(boardPk, Yn.N)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse updateComment(CommentUpdateRequest request, Long userPk, Long commentInProjectPk){

        // 1. 댓글 조회
        CommentInProject comment = commentRepository.findById(commentInProjectPk)
                .orElseThrow(()->new RuntimeException("해당 댓글이 존재하지 않습니다."));
        // 2. 게시판 조회
        BoardInProject board = boardRepository.findById(comment.getBoardInProject().getBoardInProjectPk())
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 3. 사용자 조회
        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 4. 해당 유저 프로젝트 멤버인지 조회
        if(!projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(board.getProject().getProjectPk(), userPk)){
            throw new RuntimeException("해당 유저는 프로젝트 멤버가 아닙니다.");
        }

        // 5. 작성자 확인
        if(!comment.getUser().getUserPk().equals(userPk)){
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        comment.updateContent(request.getComment()); // 엔티티 내부 메서드로 업데이트 권장
        return CommentResponse.from(comment);
    }


    @Transactional
    public void deleteComment(Long commentPk, Long userPk) {

        // 0814 softdelete 로 변경
        CommentInProject comment = commentRepository.findById(commentPk)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        // 게시판 조회
        BoardInProject board = boardRepository.findById(comment.getBoardInProject().getBoardInProjectPk())
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 작성자 확인
        if(!comment.getUser().getUserPk().equals(userPk)){
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        // 해당 유저 프로젝트 멤버인지 조회
        if(!projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(board.getProject().getProjectPk(), userPk)){
            throw new RuntimeException("해당 유저는 프로젝트 멤버가 아닙니다.");
        }

        comment.markDeleted(); // deletedYN = Y로 변경
    }

    @Transactional
    public Long getAuthorUserPk(Long commentInProjectPk){
        CommentInProject comment = commentRepository.findById(commentInProjectPk)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        return comment.getUser().getUserPk();

    }
}