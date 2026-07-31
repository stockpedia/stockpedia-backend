package com.ktb.postlike.repository;

import com.ktb.postlike.domain.PostLike;
import com.ktb.postlike.domain.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    PostLike findPostLikeById(PostLikeId postLikeId);

    boolean existsById(PostLikeId postLikeId);
}
