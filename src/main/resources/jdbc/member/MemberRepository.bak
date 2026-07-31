package com.ktb.member.repository;

import com.ktb.member.domain.Member;

import java.util.List;

public interface MemberRepository {

    Member save(Member member);

    Member findById(Long id);

    Member findByEmail(String email);

    List<Member> findAll();

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    void updatePasswordById(Long id, String password);

    void deleteById(Long id);
}
