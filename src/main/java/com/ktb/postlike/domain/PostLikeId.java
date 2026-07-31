package com.ktb.postlike.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * PostLike 엔티티의 복합 PK 보조 클래스 (@IdClass용)
 *
 *  Serializable 구현
 *  @EqualsAndHashCode
 *  equals/hashCode 자동 생성. 영속성 컨텍스트가 PK로 객체를 식별하므로 필수
 *  @NoArgsConstructor
 *  JPA가 리플렉션으로 인스턴스를 만들 때 필요
 *  필드명(postId, memberId)이 PostLike의 @Id 필드와 1:1로 일치해야 함
 *  글자가 다르면 매핑 실패
 */
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class PostLikeId implements Serializable {

    private Long postId;

    private Long memberId;

    public PostLikeId(Long postId, Long memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }
}