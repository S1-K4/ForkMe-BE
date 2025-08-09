package com.S1_K4.ForkMe_BE.global.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    public String createFileName(String fileName,String dirName) {
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
    public void deleteImageByUrl(String url) {
        String key = url.substring(url.indexOf("images/"));
        amazonS3.deleteObject(bucket, key);
    }
}