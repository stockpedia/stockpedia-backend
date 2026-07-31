package com.ktb.post.controller;


import com.ktb.global.utils.response.ApiResponse;
import com.ktb.post.domain.Post;
import com.ktb.post.service.PostService;
import com.ktb.post.dto.PostRequest;
import com.ktb.post.dto.PostResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostResponse.CreatePostResponse>> createPost(
            @Valid @RequestBody PostRequest.CreatePostRequest pc,
            @SessionAttribute("loginMember") Long currentMemberId) {

        Post post = postService.createPost(currentMemberId, pc);

        PostResponse.CreatePostResponse response =
                new PostResponse.CreatePostResponse(post.getId(), post.getTitle(), post.getCreatedAt());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 등록되었습니다.", response));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostResponse.DetailPostResponse>> getPostDetail(
            @PathVariable Long postId, @SessionAttribute("loginMember") Long currentMemberId
    ) {
        PostResponse.DetailPostResponse response = postService.getPostDetail(postId, currentMemberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(postId + "번 게시글 조회에 성공했습니다.", response));
    }
    
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PostResponse.PostPageResponse>> getPosts(
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Integer limit
    ) {
        PostResponse.PostPageResponse response = postService.getPosts(cursor, limit);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("게시글 목록 조회에 성공했습니다.", response));
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostResponse.UpdatePostResponse>> updatePost(
            @PathVariable Long postId, @Valid @RequestBody PostRequest.UpdatePostRequest pu,
            @SessionAttribute("loginMember") Long currentMemberId
    ) {
        PostResponse.UpdatePostResponse response =
                postService.updatePost(postId, currentMemberId, pu);

        return ResponseEntity.
                status(HttpStatus.OK)
                .body(ApiResponse.success(postId + "번 게시글이 수정되었습니다.", response));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @SessionAttribute("loginMember") Long currentMemberId) {

        postService.deletePost(postId, currentMemberId);

        return ResponseEntity.noContent().build();
    }
}
