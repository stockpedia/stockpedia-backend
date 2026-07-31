package com.ktb.global.utils.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity extends CreatedEntity{

    /**
     * @LastModifiedDate
     * 수정일을 기록하기 위해 LocalDateTime 타입의 필드에 @LastModifiedDate 를 적용한다
     * 이렇게 적용하면, 엔티티가 수정됨을 감지하고 그 시점을 updatedAt 필드에 기록한다
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
