package com.S1_K4.ForkMe_BE.global.common.s3;

import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.FileInfoResponse;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.s3
 * @fileName : S3Service
 * @date : 2025-08-04
 * @description : S3 service 파일입니다.
 */
@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

        public List<String> uploadFile(List<MultipartFile> multipartFiles, String dirName) {
            List<String> fileNameList = new ArrayList<>();

            multipartFiles.forEach(file -> {
                String fileName = createFileName(file.getOriginalFilename(), dirName);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());

                try (InputStream inputStream = file.getInputStream()) {
                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
                }

                String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
                fileNameList.add(fileUrl);
            });

            return fileNameList;
        }

        // 워크스페이스 내 파일 저장
    public List<FileInfoResponse> uploadFileIn(List<MultipartFile> multipartFiles, String dirName) {
        List<FileInfoResponse> fileInfos = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            String fileName = dirName + "/" + createFileName(file.getOriginalFilename(), dirName);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
            fileInfos.add(FileInfoResponse.builder()
                    .fileUrl(fileUrl)
                    .originalFileName(file.getOriginalFilename())
                    .build());
        }

        return fileInfos;
    }

//    // 파일명을 난수화하기 위해 UUID 를 활용하여 난수를 돌린다.
//    public String createFileName(String fileName){
//        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
//    }

    public String createFileName(String fileName, String dirName) {
        String extension = getFileExtension(fileName);
        String uuid = UUID.randomUUID().toString();

        return dirName + "/" + uuid + extension;
    }

    //ContentType 판별
    private String getFolderNameByContentType(String contentType) {
        if (contentType == null) return "etc";

        if (contentType.startsWith("image")) {
            return "images";
        } else if (contentType.equals("application")) {
            return "files";
        } else {
            return "etc";
        }
    }

    //  "."의 존재 유무만 판단
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf( "."));
        } catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
        }
    }


    public void deleteImage(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, "images/"+fileName));
//        System.out.println(bucket);
    }

    public void deleteFile(String key) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
    }


    public String generatePresignedDownloadUrl(String key, int expirationMinutes, String downloadFileName) {
        // 만료시간 설정 (현재시간 + expirationMinutes)
        Date expiration = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000L);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        try {
            // UTF-8로 URL 인코딩, +는 공백으로 변경
            String encodedFileName = URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

            // Content-Disposition 헤더에 RFC 5987 방식 적용 (fallback으로 ASCII 이름 포함)
            String contentDisposition = "attachment; filename=\"file.txt\"; filename*=UTF-8''" + encodedFileName;

            generatePresignedUrlRequest.addRequestParameter("response-content-disposition", contentDisposition);

        } catch (Exception e) {
            // 인코딩 실패 시, 기본 헤더 세팅
            generatePresignedUrlRequest.addRequestParameter("response-content-disposition",
                    "attachment; filename=\"" + downloadFileName + "\"");
        }

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }
    public void deleteImageByUrl(String url) {
        String key = url.substring(url.indexOf("images/"));
        amazonS3.deleteObject(bucket, key);
    }
}