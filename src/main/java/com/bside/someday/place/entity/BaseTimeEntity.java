package com.bside.someday.place.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // Entity 클래스들이 BaseTimeEntity를 상속할 경우 createdDate, modifiedDate도 칼럼으로 인식
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate // 생성될 때 시간 자동저장
    @Column(insertable = true, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate //수정될때 시간 자동저장
    @Column(insertable = false, updatable = true)
    private LocalDateTime modifiedDate;
}
