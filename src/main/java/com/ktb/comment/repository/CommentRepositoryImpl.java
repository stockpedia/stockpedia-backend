package com.ktb.comment.repository;

import com.ktb.comment.domain.QComment;
import com.ktb.comment.dto.CommentResponse;
import com.ktb.member.domain.QMember;
import com.ktb.post.domain.QPost;
import com.ktb.profileImage.domain.QProfileImage;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CommentResponse.CreateCommentResponse findCommentById(Long commentId, Long memberId) {
        CommentResponse.CreateCommentResponse result = jpaQueryFactory.select(
                        Projections.constructor(
                                CommentResponse.CreateCommentResponse.class,
                                QComment.comment.id,
                                QComment.comment.post.id,
                                QComment.comment.content,
                                QComment.comment.createdAt,
                                QPost.post.commentCount,
                                QComment.comment.member.id.eq(memberId),
                                Projections.constructor(
                                        CommentResponse.AuthorResponse.class,
                                        QMember.member.id,
                                        QMember.member.nickname
                                )
                        ))
                .from(QComment.comment)
                .join(QComment.comment.post, QPost.post)
                .join(QComment.comment.member, QMember.member)
                .where(
                        QComment.comment.id.eq(commentId),
                        QComment.comment.deletedAt.isNull()
                )
                .fetchOne();
        return result;
    }

    @Override
    public List<CommentResponse.CommentResult> findCommentResultByCursor(Long postId, Long loginId, Long cursor, int limit) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        CommentResponse.CommentResult.class,
                        QComment.comment.id,
                        QComment.comment.content,
                        QComment.comment.createdAt,
                        QComment.comment.member.id.eq(loginId),
                        Projections.constructor(
                                CommentResponse.AuthorCursorResponse.class,
                                QMember.member.id,
                                QMember.member.nickname,
                                QProfileImage.profileImage.storedPath
                        )
                ))
                .from(QComment.comment)
                .join(QComment.comment.member, QMember.member)
                .leftJoin(QProfileImage.profileImage)
                .on(QProfileImage.profileImage.member.id.eq(QMember.member.id))
                .where(
                        QComment.comment.post.id.eq(postId),
                        QComment.comment.deletedAt.isNull(),
                        cursor == null ? null : QComment.comment.id.lt(cursor)
                )
                .orderBy(QComment.comment.id.desc())
                .limit(limit)
                .fetch();
    }
}
