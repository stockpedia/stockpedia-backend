package com.ktb.file.controller;

import com.ktb.file.dto.FileResponse;
import com.ktb.file.service.FileStorage;
import com.ktb.global.utils.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileStorage fileStorage;

    @PostMapping("/profiles/images")
    public ResponseEntity<ApiResponse<FileResponse.UploadResponse>> store(
            @RequestParam("file") MultipartFile file
    ) {

        String storeFileUrl = fileStorage.storeFile(file, "profiles");

        FileResponse.UploadResponse response =
                new FileResponse.UploadResponse(file.getOriginalFilename(), storeFileUrl, "null");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("이미지가 업로드되었습니다.", response));
    }


    @PostMapping("/posts/images")
    public ResponseEntity<ApiResponse<List<FileResponse.UploadResponse>>> store(
            @RequestParam("file") List<MultipartFile> files
    ) {

        List<FileResponse.UploadResponse> result = new ArrayList<>();

        for (MultipartFile file : files) {
            String storeFileUrl = fileStorage.storeFile(file, "posts");

            FileResponse.UploadResponse response =
                    new FileResponse.UploadResponse(file.getOriginalFilename(), storeFileUrl, "null");
            result.add(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("이미지가 업로드되었습니다.", result));
    }
}
