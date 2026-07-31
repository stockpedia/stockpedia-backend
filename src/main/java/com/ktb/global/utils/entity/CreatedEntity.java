package com.ktb.global.utils.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

/**
 * @EntityListeners
 * Auditing을 적용할 엔티티 클래스에 적용
 * 엔티티의 변화를 감지하여 엔티티와 매핑된 테이블의 데이터를 조작한다
 * 파라미터로 이벤트 리스너를 받는데, 여기에 AuditingEntityListener 클래스를 넣어준다
 * 이 클래스는 Spring Data JPA에서 제공하는 이벤트 리스너로 엔티티의 영속, 수정 이벤트를 감지하는 역할을 한다
 * @EntityListeners는 상속하는 최상위 부모에게만 한 번 선언
 *
 * @CreatedDate
 * 생성일을 기록하기 위해 LocalDateTime 타입의 필드에 @CreatedDate 를 적용한다
 * 생성일자는 수정되어서는 안되므로 @Column(updatable = false) 를 함께 적용한다
 * 이렇게 적용하면, 엔티티가 생성됨을 감지하고 그 시점을 createdAt 필드에 기록한다
 *
 * @MappedSuperclass
 * 공통 매핑 정보가 필요할 때 부모 클래스에 선언된 필드를 상속받는 클래스에서 그대로 사용할 때 사용한다
 * 이때, 부모 클래스에 대한 테이블은 별도로 생성되지 않는다
 *
 * @Getter
 * 상속받는 자식 엔티티가 createdAt / updatedAt 같은 공통 필드를 외부로 노출할 수 있도록 부모 단에서 미리 getter 생성
 * 필드가 private이라 자식 클래스에서도 직접 접근 불가, 반드시 getter를 통해 읽어야 함
 * 공통 시간 필드를 자식 엔티티마다 다시 선언하지 않고 부모의 getter를 통해 한 번에 제공
*/
