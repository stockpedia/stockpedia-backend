package com.ktb.postImage.repository;

import com.ktb.postImage.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    void deleteByPostId(Long postId);

    List<PostImage> findByPostId(Long postId);
}
