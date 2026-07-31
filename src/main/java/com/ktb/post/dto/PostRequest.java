package com.ktb.post.dto;

import com.ktb.file.dto.UploadFile;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class PostRequest {

    @Getter
    @Setter // @ModelAttribute
    public static class CreatePostRequest {

        private String title;

        private String content;

        @Valid
        private List<UploadFile> uploadFiles;
    }

    @Getter
    public static class UpdatePostRequest {

        private String title;

        private String content;

        @Valid
        private List<UploadFile> uploadFiles;
    }
}
