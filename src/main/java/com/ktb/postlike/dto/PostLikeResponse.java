package com.ktb.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeResponse {

    private Long postId;

    private Long memberId;

    private Long likeCount;

    private Boolean isLike;
}
