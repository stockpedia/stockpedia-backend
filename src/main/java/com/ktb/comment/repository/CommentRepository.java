package com.ktb.comment.repository;

import com.ktb.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Comment findByIdAndDeletedAtIsNull(Long commentId);
}
