package com.back.domain.post.post.entity;

import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    private String subject;
    private String body;

    // 글을 저장/삭제하면 댓글도 함께 저장/삭제되고, 목록에서 빠진 댓글은 DB에서도 지운다
    @OneToMany(mappedBy = "post", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private final List<PostComment> comments = new ArrayList<>();

    public Post(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public void modify(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public PostComment addComment(String body) {
        PostComment postComment = new PostComment(this, body);
        comments.add(postComment);

        return postComment;
    }

    public Optional<PostComment> findCommentById(int id) {
        return comments
                .stream()
                .filter(comment -> comment.getId() == id)
                .findFirst();
    }

    public boolean deleteComment(PostComment postComment) {
        if (postComment == null) return false;

        return comments.remove(postComment);
    }
}