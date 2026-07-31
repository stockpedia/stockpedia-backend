package com.ktb.postlike.domain;

import com.ktb.global.utils.entity.CreatedEntity;
import com.ktb.member.domain.Member;
import com.ktb.post.domain.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @IdClass(PostLikeId.class) 복합 PK(여러 컬럼을 묶어 식별자로 쓰는 경우)를 표현하는 JPA 표준 방식 중 하나
 * 엔티티 자체엔 @Id를 여러 개 두고, 별도 ID 보조 클래스를 만들어 IdClass에 지정
 * <p>
 * PostLikeId 측 요구사항
 * Serializable 구현
 * 기본 생성자 (public 또는 protected)
 * @EqualsAndHashCode equals() / hashCode() 오버라이드 (영속성 컨텍스트가 PK로 객체 동일성 비교)
 * 엔티티의 @Id 필드와 "동일한 이름"의 필드 보유 (postId, memberId)
 * <p>
 * 대안: @EmbeddedId
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// @IdClass(PostLikeId.class)
@Table(name = "post_like")
public class PostLike extends CreatedEntity {

    @EmbeddedId
    private PostLikeId id = new PostLikeId();

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    public PostLike(Post post, Member member) {
        this.post = post;
        this.member = member;
    }
}

/**
 * @EmbeddedId private PostLikeId id;
 * @Id 두 개
 * 복합 PK의 구성요소
 * IdClass에 지정한 PostLikeId의 같은 이름의 필드와 1:1로 매핑
 * (post_id, member_id) 조합이 유니크
 */
