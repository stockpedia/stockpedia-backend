package com.ktb.file.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UploadFile {

    @NotBlank
    private String originalName;

    @NotBlank
    private String storedPath;

    @NotBlank
    private String s3Key;
}
