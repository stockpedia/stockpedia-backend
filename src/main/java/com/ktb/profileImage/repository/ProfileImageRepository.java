package com.ktb.profileImage.repository;

import com.ktb.profileImage.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    void deleteByMemberId(Long memberId);

    ProfileImage findByMemberId(Long memberId);
}
