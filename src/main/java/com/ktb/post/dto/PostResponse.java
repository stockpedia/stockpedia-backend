package com.ktb.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ktb.file.dto.UploadFile;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    @Getter
    @AllArgsConstructor
    public static class CreatePostResponse {

        private Long id;

        private String title;

        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdatePostResponse {
        private Long id;

        private String title;

        private String content;

        private List<String> storedImageUrls;
    }

    @Getter
    public static class DetailPostResponse {

        private Long postId;

        private String title;

        private String content;

        private Long viewCount;

        private Long likeCount;

        private Long commentCount;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        private Boolean isMine;

        private Boolean isLiked;

        private AuthorResponse author;

        private List<String> imageUrls;

        public DetailPostResponse(Long postId, String title, String content, Long viewCount, Long likeCount, Long commentCount, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isMine, Boolean isLiked, AuthorResponse author) {
            this.postId = postId;
            this.title = title;
            this.content = content;
            this.viewCount = viewCount;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.isMine = isMine;
            this.isLiked = isLiked;
            this.author = author;
        }

        public void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        public void setViewCount(Long viewCount) {
            this.viewCount = viewCount;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class AuthorResponse {

        private Long memberId;

        private String nickname;

        private String profileImageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class PostSummaryResult {

        private Long postId;

        private String title;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        private Long likeCount;

        private Long commentCount;

        private Long viewCount;

        private AuthorResponse author;
    }

    @Getter
    @AllArgsConstructor
    public static class PostPageResponse {

        private List<PostResponse.PostSummaryResult> posts;

        private Long nextCursor;

        private boolean hasNext;
    }


}
