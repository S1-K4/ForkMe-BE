package com.S1_K4.ForkMe_BE.global.common.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.s3
 * @fileName : S3Controller
 * @date : 2025-08-05
 * @description : S3 테스트 Controller입니다.
 */
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/file")
//public class S3ControllerTest {
//
//    private final S3Service s3Service;
//
//    //테스트용 컨트롤러(insert)
//    @PostMapping
//    public ResponseEntity<List<String>> uploadFile(@RequestParam("files") List<MultipartFile> multipartFiles){
//        return ResponseEntity.ok(s3Service.uploadFile(multipartFiles));
//    }
//
//    //이미지 delete
//    @DeleteMapping
//    public ResponseEntity<String> deleteImage(@RequestParam String fileName){
//        s3Service.deleteImage(fileName);
//        return ResponseEntity.ok(fileName);
//    }
//}