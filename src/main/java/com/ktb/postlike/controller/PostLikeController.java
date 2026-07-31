package com.ktb.postlike.controller;

import com.ktb.global.utils.response.ApiResponse;
import com.ktb.postlike.dto.PostLikeResponse;
import com.ktb.postlike.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;


    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> createPostLike(
            @PathVariable Long postId, @SessionAttribute("loginMember") Long currentMemberId
    ) {
        PostLikeResponse response = postLikeService.createPostLike(postId, currentMemberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(postId + "번 게시글 좋아요에 성공했습니다.", response));
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> deletePostLike(
            @PathVariable Long postId, @SessionAttribute("loginMember") Long currentMemberId
    ) {
        PostLikeResponse response = postLikeService.deletePostLike(postId, currentMemberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(postId + "번 게시글 좋아요 취소에 성공했습니다.", response));
    }

}
