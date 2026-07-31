package com.ktb.profileImage.domain;
import com.ktb.global.utils.entity.BaseEntity;
import com.ktb.global.utils.entity.SoftDeleteEntity;
import com.ktb.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "profile_image")
public class ProfileImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false, updatable = false, unique = true)
    private Member member;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "stored_path", nullable = false)
    private String storedPath;

    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    public ProfileImage(Member member, String originalName, String storedPath, String s3Key) {
        this.member = member;
        this.originalName = originalName;
        this.storedPath = storedPath;
        this.s3Key = s3Key;
    }

    public void update(String originalName, String storedPath, String s3Key) {
        this.originalName = originalName;
        this.storedPath = storedPath;
        this.s3Key = s3Key;
    }
}
