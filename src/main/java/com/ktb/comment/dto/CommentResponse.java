package com.ktb.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ktb.post.dto.PostResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Getter
    @AllArgsConstructor
    public static class CreateCommentResponse {

        private Long id;

        private Long postId;

        private String content;

        private LocalDateTime createdAt;

        private Long commentCount;

        private Boolean isMine;

        private AuthorResponse author;
    }

    @Getter
    @AllArgsConstructor
    public static class AuthorResponse {

        private Long memberId;

        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    public static class AuthorCursorResponse {

        private Long memberId;

        private String nickname;

        private String profileImageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateCommentResponse {
        private Long id;

        private String content;

        // private LocalDateTime updateAt;
    }

    @Getter
    @AllArgsConstructor
    public static class CommentResult {

        private Long commentId;

        private String content;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        private Boolean isMine;

        private AuthorCursorResponse author;
    }

    @Getter
    @AllArgsConstructor
    public static class CommentPageResponse {

        private List<CommentResponse.CommentResult> posts;

        private Long nextCursor;

        private boolean hasNext;
    }
}
