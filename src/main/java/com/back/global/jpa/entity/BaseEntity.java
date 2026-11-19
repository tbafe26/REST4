package com.back.global.jpa.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@MappedSuperclass // 테이블은 만들지 않고, 필드만 자식 엔티티에 물려준다
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @CreatedDate // 처음 저장될 때 시각이 자동으로 채워진다
    private LocalDateTime createdDate;

    @LastModifiedDate // 수정될 때마다 시각이 자동으로 갱신된다
    private LocalDateTime modifyDate;

    // 같은 id 면 같은 엔티티로 본다
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}