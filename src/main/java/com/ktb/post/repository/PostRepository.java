package com.ktb.post.repository;

import com.ktb.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Post findByIdAndDeletedAtIsNull(Long id);

    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + :delta where p.id = :id")
    void incrementViewCount(@Param("id") Long id, @Param("delta") Long delta);

    /**
     * 도메인 메소드를 사용해서 객체 상태를 바꿔 JPA의 dirty checking으로 UPDATE가 나가면
     * 동시에 여러 사용자가 같은 값을 바꿀때 Lost Update가 발생할 수 있음
     * 객체 단위가 아닌, DB의 도움이 필요
     * 카운터는 JPQL, QueryDSL로 해결 할 수 있고 나아가 Redis도 사용할 수 있음
     */
    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount + 1 where p.id = :id")
    void incrementLikeCount(Long id);

    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount - 1 where p.id = :id")
    void decrementLikeCount(Long id);

    @Query("select p.likeCount from Post p where p.id = :id")
    Long findLikeCountById(Long id);

    @Modifying
    @Query("update Post p set p.commentCount = p.commentCount + 1 where p.id = :id")
    void incrementCommentCount(Long id);

    @Modifying
    @Query("update Post p set p.commentCount = p.commentCount - 1 where p.id = :id")
    void decrementCommentCount(Long id);
}
