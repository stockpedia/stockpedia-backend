package com.ktb.member.repository;

import com.ktb.member.domain.QMember;
import com.ktb.member.dto.MemberResponse;
import com.ktb.profileImage.domain.QProfileImage;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MemberResponse.ProfileResponse findMemberProfileById(Long id) {
        MemberResponse.ProfileResponse result = jpaQueryFactory
                .select(Projections.constructor(
                        MemberResponse.ProfileResponse.class,
                        QMember.member.id,
                        QMember.member.email,
                        QMember.member.nickname,
                        QProfileImage.profileImage.storedPath
                ))
                .from(QMember.member)
                .leftJoin(QProfileImage.profileImage)
                .on(QProfileImage.profileImage.member.id.eq(QMember.member.id))
                .where(QMember.member.id.eq(id))
                .fetchOne();

        return result;
    }
}
