package com.ktb.post.repository;

import com.ktb.post.dto.PostResponse;

import java.util.List;

public interface PostRepositoryCustom {

    PostResponse.DetailPostResponse findPostDetailByPostId(Long postId, Long MemberId);

    List<PostResponse.PostSummaryResult> findPostSummaryResultByCursor(Long cursor, int limit);
}
