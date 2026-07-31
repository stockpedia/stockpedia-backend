package com.ktb.member.repository;

import com.ktb.member.domain.Member;
import com.ktb.member.dto.MemberResponse;

public interface MemberRepositoryCustom {

    MemberResponse.ProfileResponse findMemberProfileById(Long id);
}
