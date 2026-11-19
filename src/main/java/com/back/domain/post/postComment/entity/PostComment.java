package com.back.domain.post.postComment.entity;

import com.back.domain.member.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.global.globalExceptionHandler.AccessDeniedException;
import com.back.global.jpa.entity.BaseEntity;
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
    private Post post;
    @ManyToOne(fetch = LAZY) // 작성자
    private Member author;
    private String content;

    public PostComment(Post post, Member author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
    }

    // 댓글 작성자 본인이 아니면 삭제할 수 없다 (id 로 비교: author 는 지연 로딩 프록시일 수 있다)
    public void checkActorCanDelete(Member actor) {
        if (author.getId() != actor.getId()) {
            throw new AccessDeniedException();
        }
    }

    // 댓글 작성자 본인이 아니면 수정할 수 없다
    public void checkActorCanModify(Member actor) {
        if (author.getId() != actor.getId()) {
            throw new AccessDeniedException();
        }
    }

    public void modify(String content) {
        this.content = content;
    }
}