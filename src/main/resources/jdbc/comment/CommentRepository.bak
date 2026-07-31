package com.ktb.comment.repository;

import com.ktb.comment.domain.Comment;
import com.ktb.comment.dto.CommentResponse;

public interface CommentRepository {

    CommentResponse.CreateCommentResponse save(Comment comment);

    Comment findById(Long commentId);

    CommentResponse.CreateCommentResponse findResponseForCreatedComment(Long commentId);

    void updateById(Long commentId, String content);

    void deleteById(Long commentId);
}
