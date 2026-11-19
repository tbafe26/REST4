package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    // [실험] 엔티티(Post)를 그대로 반환하면 Post -> comments -> post -> comments ... 로 JSON 직렬화가 끝나지 않는다
    @GetMapping("/api/v1/posts")
    @ResponseBody
    public List<Post> getItems() {
        List<Post> items = postService.findAll();

        return items;
    }
}