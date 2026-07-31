package com.ktb.member.repository;

import com.ktb.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Member findByEmail(String email);

    Member findMemberById(Long id);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}


