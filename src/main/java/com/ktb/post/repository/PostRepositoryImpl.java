package com.ktb.post.repository;

import com.ktb.member.domain.QMember;
import com.ktb.post.domain.QPost;
import com.ktb.post.dto.PostResponse;
import com.ktb.postlike.domain.QPostLike;
import com.ktb.profileImage.domain.QProfileImage;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ktb.member.domain.QMember.member;
import static com.ktb.post.domain.QPost.post;
import static com.ktb.profileImage.domain.QProfileImage.profileImage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public PostResponse.DetailPostResponse findPostDetailByPostId(Long postId, Long memberId) {
        PostResponse.DetailPostResponse response = jpaQueryFactory.select(
                        Projections.constructor(
                                PostResponse.DetailPostResponse.class,
                                QPost.post.id,
                                QPost.post.title,
                                QPost.post.content,
                                QPost.post.viewCount,
                                QPost.post.likeCount,
                                QPost.post.commentCount,
                                QPost.post.createdAt,
                                QPost.post.updatedAt,
                                QPost.post.member.id.eq(memberId),
                                QPostLike.postLike.member.id.isNotNull(),
                                Projections.constructor(
                                        PostResponse.AuthorResponse.class,
                                        QMember.member.id,
                                        QMember.member.nickname,
                                        QProfileImage.profileImage.storedPath
                                )
                        )
                )
                .from(QPost.post)
                .join(QPost.post.member, QMember.member)
                .leftJoin(QProfileImage.profileImage)
                .on(QProfileImage.profileImage.member.id.eq(QMember.member.id))
                .leftJoin(QPostLike.postLike)
                .on(QPostLike.postLike.post.id.eq(QPost.post.id)
                        .and(QPostLike.postLike.member.id.eq(memberId)))
                .where(
                        QPost.post.id.eq(postId),
                        QPost.post.deletedAt.isNull()
                )
                .fetchOne();
        return response;
    }

    @Override
    public List<PostResponse.PostSummaryResult> findPostSummaryResultByCursor(Long cursor, int limit) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                PostResponse.PostSummaryResult.class,
                                QPost.post.id,
                                QPost.post.title,
                                QPost.post.createdAt,
                                QPost.post.likeCount,
                                QPost.post.commentCount,
                                QPost.post.viewCount,
                                Projections.constructor(
                                        PostResponse.AuthorResponse.class,
                                        QMember.member.id,
                                        QMember.member.nickname,
                                        QProfileImage.profileImage.storedPath
                                )
                        )
                )
                .from(QPost.post)
                .join(QPost.post.member, QMember.member)
                .leftJoin(QProfileImage.profileImage)
                .on(QProfileImage.profileImage.member.id.eq(QMember.member.id))
                .where(cursor != null ? post.id.lt(cursor) : null, QPost.post.deletedAt.isNull())
                .orderBy(QPost.post.id.desc())
                .limit(limit)
                .fetch();
    }
}
