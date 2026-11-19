package com.back.domain.post.postComment.entity;

import com.back.domain.post.post.entity.Post;
import com.back.global.jpa.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class PostComment extends BaseEntity {
    @ManyToOne(fetch = LAZY) // 필요할 때까지 글 조회를 미룬다 (연관관계의 주인)
    @JsonIgnore // 응답 JSON 에서 제외해 글 <-> 댓글 순환 참조를 끊는다
    private Post post;
    private String body;

    public PostComment(Post post, String body) {
        this.post = post;
        this.body = body;
    }

    public void modify(String body) {
        this.body = body;
    }
}