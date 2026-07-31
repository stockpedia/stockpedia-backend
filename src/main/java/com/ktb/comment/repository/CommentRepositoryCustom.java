package com.ktb.comment.repository;

import com.ktb.comment.domain.Comment;
import com.ktb.comment.dto.CommentResponse;

import java.util.List;

public interface CommentRepositoryCustom {

    CommentResponse.CreateCommentResponse findCommentById(Long commentId, Long memberId);

    List<CommentResponse.CommentResult> findCommentResultByCursor(
            Long postId,
            Long loginUserId,
            Long cursor,
            int limit
    );
}
