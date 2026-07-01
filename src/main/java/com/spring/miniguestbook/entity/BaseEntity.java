package com.spring.miniguestbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
//필드만 자식 엔터티에 넘겨준다
@MappedSuperclass
//저장/수정 시점을 감지해 아래 날짜 필드를 자동으로 채워줌
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    //CreatedDate : 처음 저장될 때 시각이 자동으로 입력됨
    //Column(updatable = false) : 더 이상 업데이트 되지 않음
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    //수정될때마다 시각이 자동으로 갱신
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
