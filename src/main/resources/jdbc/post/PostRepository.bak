package com.ktb.post.repository;

import com.ktb.post.domain.Post;
import com.ktb.post.dto.PostResponse;

public interface PostRepository {

    Post save(Post post);

    PostResponse.DetailPostResponse findPostDetailByPostId(Long postId, Long currentMemberId);

    void updateById(Long postId, String title, String content);

    void deleteById(Long postId);

    Post findByPostId(Long postId);

    Long findAuthorIdByPostId(Long postId);

    void incrementViewCount(Long postId);

    void incrementLikeCount(Long postId);

    void decrementLikeCount(Long postId);

    void incrementCommentCount(Long postId);

    void decrementCommentCount(Long postId);

    Long findLikeCountByPostId(Long postId);
}
