package com.ktb.member.domain;

import com.ktb.global.utils.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
// @AllArgsConstructor
@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    /**
     * @Id PK(식별자)임을 표시
     * 한 번 정해지면 변경되어선 안 됨
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * ID 생성 전략을 DB에 위임 (MySQL의 AUTO_INCREMENT 사용)
     * 영속 시점에 쓰기 지연 저장소를 거치지 않고 바로 DB로 INSERT가 나감
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column 자바 필드와 DB 컬럼의 매핑을 세부 설정하는 어노테이션
     * <p>
     * name
     * 매핑되는 DB 컬럼명을 지정
     * 생략하면 Hibernate의 기본 네이밍 전략이 자동 매핑 (email → email, createdAt → created_at)
     * 같은 이름이어도 명시하면 "어느 컬럼에 붙는지" 코드에서 바로 읽혀서 가독성에 이점
     * <p>
     * nullable = false
     * NOT NULL 제약 추가
     */
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * length
     * 문자열 컬럼의 길이 (VARCHAR(n)에서 n)
     * 기본값은 255
     */
    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;

    }

    public void changePassword(String password) {
        this.password = password;
    }

}


/**
 * @NoArgsConstructor JdbcTemplate 단계에서 BeanPropertyRowMapper / DataClassRowMapper 같은
 * 리플렉션 기반 매퍼를 시도하면서 필요했음
 * 결과적으로는 람다 RowMapper(new Member(...))를 쓰게 돼서
 * 실제 매핑은 @AllArgsConstructor 쪽이 담당하게 됐음
 * <p>
 * → JPA 전환 이후에도 기본 생성자 자체는 여전히 필요함
 * (JPA 스펙상 Hibernate가 리플렉션으로 엔티티를 인스턴스화하기 위함)
 * https://velog.io/@pdy000726/Hibernate%EC%97%90%EC%84%9C-%EA%B0%9D%EC%B2%B4-%ED%95%A0%EB%8B%B9-%EC%8B%9C-%EA%B8%B0%EB%B3%B8-%EC%83%9D%EC%84%B1%EC%9E%90-%ED%95%84%EC%9A%94%ED%95%9C-%EC%9D%B4%EC%9C%A0
 * @AllArgsConstructor JdbcMemberRepository의 람다 RowMapper에서 new Member(...)로 매핑하기 위해 열어둠
 * MemberService.createMember에서도 new Member(null, ..., null, null) 형태로 호출 중
 */


/**
 * @Table(name = "member", uniqueConstraints = {...})
 * member는 일부 DB에서 예약어와 충돌할 여지도 있고,
 * "이 엔티티가 어느 테이블에 매핑되는지" 가 명시적으로 박아둠
 * <p>
 * uniqueConstraints: 유니크 제약을 어디에 둘지 두 가지 선택지
 * → 컬럼 레벨: @Column(unique = true)
 * Hibernate가 제약명을 해시로 자동 생성
 * 에러 로그에서 어떤 제약이 터졌는지 추적이 어려움
 * → 테이블 레벨: @UniqueConstraint(name = "...", columnNames = ...)
 * 제약명을 직접 지정 → 로그/디버깅 시 의미가 드러남
 * 복합 유니크(여러 컬럼 묶음)으로도 표현 가능
 * 예) @UniqueConstraint(columnNames = {"company_id", "date"})
 * name을 빼기 때문에 컬럼 레벨에서 선언한 것처럼 제약명이 해시로 자동 생성
 */


/**
 * @Entity 이 클래스를 JPA가 관리하는 엔티티로 등록
 * 이 어노테이션이 있어야 Hibernate가 클래스 ↔ 테이블 매핑 대상으로 인식하고
 * EntityManager / PersistenceContext의 관리 대상이 됨
 * @Table로 명시하지 않으면 Hibernate가 자동으로 클래스명(Member) → 테이블명(member)으로,
 * 테이블로 매핑(CamelCase → snake_case 자동 변환)
 */


/**
 * @Getter 도메인 객체는 필드를 직접 노출하지 않고 메서드로 읽도록 캡슐화
 * @Setter는 의도적으로 두지 않음
 * → 상태 변경은 "의미가 드러나는 도메인 메서드"로만 일어나야 함, 지금은 서비스 레이어에서 상태 변경 중
 */