package com.S1_K4.ForkMe_BE.modules.on_project.board.controller;

import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.InBoardDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.controller
 * @fileName : InBoardController
 * @date : 2025-08-05
 * @description : 워크스페이스 내 게시판 컨트롤러입니다.
 */

//@RestController
//@RequestMapping("/api/on-project/inboard")
//public class InBoardController {
//
//    public ResponseEntity<InBoardDetailResponse> createdBoard(@AuthenticationPrincipal UserDetailSimpl userDetails,
//                                                              @RequestPart("projectPk")Long projectPk,
//                                                              @RequestPart("title") String title,
//                                                              @RequestPart("content") String content,
//                                                              @RequestPart(value= "images", required = false) List<MultipartFile> images,
//                                                              @RequestPart(value = "files", required = false) List<MultipartFile> files){
//
//
//    }
//}