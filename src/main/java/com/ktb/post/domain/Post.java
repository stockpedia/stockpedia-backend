package com.ktb.post.domain;

import com.ktb.global.utils.entity.SoftDeleteEntity;
import com.ktb.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class Post extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @Column(name ="title", nullable = false, length = 26)
    private String title;

    /**
     * columnDefinition
     * DDL 생성 시 컬럼 정의를 직접 지정하는 옵션
     * 표준 JPA 옵션으로 표현하기 어려운 DB 전용 타입
     *
     * DB 벤더(MySQL, PostgreSQL 등)에 종속적
     * DB를 변경하면 DDL이 깨질 수 있어 이식성이 떨어짐
     * 가능하면 JPA 표준 옵션(length, nullable 등)을 우선 사용하고,
     * 꼭 필요한 경우에만 columnDefinition을 사용
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount;

    public Post(Member member, String title, String content,
                Long viewCount, Long likeCount, Long commentCount) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public boolean isOwner(Long memberId) {
        return this.member != null && member.getId().equals(memberId);
    }

}

/**
 * @Column(length = 26)
 * DDL의 길이 제약, CREATE TABLE 시 VARCHAR(26)으로 생성
 *
 * @Size(max = 26)
 * Bean Validation(jakarta.validation) 어노테이션, 자바 레벨에서 길이 검증
 * 옵션: min, max (둘 다 조합 가능)
 * 예) @Size(min = 1, max = 26)  1~26자
 * @Size(min = 1)            최소 1자
 * @Size(max = 26)           최대 26자 (빈 문자열도 통과)
 *
 * 보통은 DTO에 붙임, 컨트롤러의 @Valid로 요청을 검증하고,
 * 비즈니스 로직에 닿기 전에 차단 (DB까지 안 감)
 *
 * 엔티티 필드에 붙여도 동작은 함 (Hibernate가 라이프사이클 시점에 검증)
 * 단, 일반적으론 DTO와 엔티티의 책임을 나누는 쪽이 깔끔
 * DTO  : 들어오는 요청 검증 (@Size 등)
 * 엔티티 : DB 스키마 정합성 (@Column length, nullable 등)
 *
 * https://www.reddit.com/r/SpringBoot/comments/1qtsdvd/spring_boot_jpa_validation_annotations_on/?utm_source=chatgpt.com
 * 살펴보고 나중에 정리할 것
 *
 * 같은 26이라도 역할이 다르다.
 * @Column(length = 26) : "DB가 26자까지 받는다"
 * @Size(max = 26)      : "요청이 26자까지여야 한다"
 */