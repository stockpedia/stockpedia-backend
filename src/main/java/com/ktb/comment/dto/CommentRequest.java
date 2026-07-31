package com.ktb.comment.dto;

import lombok.Getter;

public class CommentRequest {

    @Getter
    public static class CreateCommentRequest {

        private String content;
    }

    @Getter
    public static class UpdateCommentRequest {

        private String content;
    }
}
