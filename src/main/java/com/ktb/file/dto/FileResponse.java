package com.ktb.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class FileResponse {

    @Getter
    @AllArgsConstructor
    public static class UploadResponse {

        private String originalName;

        private String storedPath;
        
        private String s3Key;
    }
}
