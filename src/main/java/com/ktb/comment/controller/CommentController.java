package com.ktb.comment.controller;

import com.ktb.comment.dto.CommentRequest;
import com.ktb.comment.dto.CommentResponse;
import com.ktb.comment.service.CommentService;
import com.ktb.global.utils.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse.CreateCommentResponse>> createComment(
            @PathVariable Long postId, @Valid @RequestBody CommentRequest.CreateCommentRequest cc,
            @SessionAttribute("loginMember") Long currentMemberId) {

        CommentResponse.CreateCommentResponse response =
                commentService.createComment(postId, currentMemberId, cc.getContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("댓글이 등록되었습니다.", response));
    }


    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse.CommentPageResponse>> getComments(
            @PathVariable Long postId,
            @SessionAttribute("loginMember") Long memberId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Integer limit
            ) {

        CommentResponse.CommentPageResponse response =
                commentService.getComments(postId, memberId, cursor, limit);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("댓글 목록 조회에 성공했습니다.", response));
    }


    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse.UpdateCommentResponse>> updateComment(
            @PathVariable Long postId, @PathVariable Long commentId, @Valid @RequestBody CommentRequest.UpdateCommentRequest cu,
            @SessionAttribute("loginMember") Long currentMemberId) {

        CommentResponse.UpdateCommentResponse response =
                commentService.updateComment(postId, currentMemberId, commentId, cu.getContent());

        return ResponseEntity.
                status(HttpStatus.OK)
                .body(ApiResponse.success(commentId + "번 댓글이 수정 되었습니다.", response));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteController(
            @PathVariable Long postId, @PathVariable Long commentId,
            @SessionAttribute("loginMember") Long currentMemberId) {

        commentService.deleteComment(postId, currentMemberId, commentId);

        return ResponseEntity.noContent().build();
    }
}
