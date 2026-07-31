package com.ktb.postImage.domain;

import com.ktb.global.utils.entity.BaseEntity;
import com.ktb.global.utils.entity.SoftDeleteEntity;
import com.ktb.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "post_image")
public class PostImage extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "stored_path", nullable = false)
    private String storedPath;

    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    public PostImage(Post post, String originalName, String storedPath, String s3Key) {
        this.post = post;
        this.originalName = originalName;
        this.storedPath = storedPath;
        this.s3Key = s3Key;
    }

}
