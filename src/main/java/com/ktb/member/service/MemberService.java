package com.ktb.member.service;

import com.ktb.global.utils.exception.BusinessException;
import com.ktb.global.utils.exception.ErrorCode;
import com.ktb.member.domain.Member;
import com.ktb.member.repository.MemberRepository;
import com.ktb.member.dto.MemberRequest;
import com.ktb.member.dto.MemberResponse;
import com.ktb.profileImage.domain.ProfileImage;
import com.ktb.profileImage.repository.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Service: 스프링 빈 등록, 비즈니스 로직 계층임을 명시(기능은 @Component와 동일, 의미 구분용)
 * @RequiredArgsConstructor: final 필드만 모아서 생성자 자동 생성 → DI(생성자 주입) 방식
 * 생성자 주입을 사용하는 이유: 불변성 보장(final), 컴파일 타임에 감지
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final ProfileImageRepository profileImageRepository;

    private final PasswordEncoder encoder;

    /**
     * @Transactional: 트랜잭션은 all or nothing
     * → 회원가입이 완벽히 끝나거나 아예 롤백되어야 하므로 추가
     */
    @Transactional
    public Member createMember(MemberRequest.SignUpRequest ms) {

        /**
         * 회원을 생성하는 createMember
         * HTTP 요청 데이터를 담은 MemberRequest.SignUpRequest를 매개변수로 받음
         *
         * [고민]
         * DTO를 서비스로 넘기는 게 맞나? 여러 방식으로 시도
         * 비밀번호 암호화 관련 설정은 SecurityConfig에서 자세하게 설명
         *
         * [흐름]
         * 1. 중복 검증: 이메일, 닉네임이 이미 존재하면 회원가입 거부
         *    비즈니스 로직 전에 미리 차단해서 불필요한 비용 절감
         * 2. 비밀번호 암호화: 평문 저장 금지, 검증 통과 직후 처리
         * 3. 멤버 객체 생성: id, createdAt, updatedAt은 null로 둠
         *    이유: id는 DB AUTO_INCREMENT, TIMESTAMP는 DB CURRENT_TIMESTAMP가 채움
         * 4. 회원 저장
         */
        if (memberRepository.existsByEmail(ms.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByNickname(ms.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

//        if (!ms.getPassword().equals(ms.getPasswordCheck())) {
//            throw new BusinessException(ErrorCode.MISMATCH_PASSWORD);
//        }

        String password = encoder.encode(ms.getPassword());

//        Member member = new Member(
//                null,
//                ms.getEmail(),
//                password, // 암호화 된 password 저장
//                ms.getNickname(),
//                null,
//                null);

        Member member = new Member(ms.getEmail(), password, ms.getNickname());
        Member savedMember = memberRepository.save(member);

        if (ms.getUploadFile() != null) {
            ProfileImage profileImage = new ProfileImage(
                    savedMember,
                    ms.getUploadFile().getOriginalName(),
                    ms.getUploadFile().getStoredPath(),
                    ms.getUploadFile().getS3Key());

            profileImageRepository.save(profileImage);
        }

        return savedMember;
    }

    /**
     * @Transactional(readOnly = true)
     * readOnly = true를 붙이는 이유?
     * 이 메서드는 데이터 수정을 하지 않고 조회 전용이라는 의도 표현
     * JPA의 변경 감지와 불필요한 flush를 줄여 성능 최적화에 도움을 줌
     */
    @Transactional(readOnly = true)
    public MemberResponse.LoginResponse login(String email, String password) {

        /**
         * [고민]
         * 회원가입 메서드와 다르게 DTO를 매개변수로 받지 않음
         * 여러 방식을 시도해보고 싶었음, 정답이 있는지는 잘 모르겠음
         *
         * [흐름]
         * 이메일로 사용자가 있는지 우선 조회, 조회한 사용자가 존재하지 않으면 예외 처리
         * 비밀번호는 암호화되어 있으므로 encoder.matches(평문, 암호화)로 비교
         * 같으면 true, 다르면 false → !로 뒤집어 "일치하지 않을 때" 예외 처리
         * 모든 검증을 통과하면 응답 DTO에 데이터를 담아 컨트롤러로 반환
         */
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        if (!encoder.matches(password, member.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        return new MemberResponse.LoginResponse(
                member.getId(), member.getNickname(), member.getEmail()
        );
    }

    /**
     * @Transactional(readOnly = true)
     * readOnly = true를 붙이는 이유?
     * 이 메서드는 데이터 수정을 하지 않고 조회 전용이라는 의도 표현
     * JPA의 변경 감지와 불필요한 flush를 줄여 성능 최적화에 도움을 줌
     */
    @Transactional(readOnly = true)
    public MemberResponse.ProfileResponse getMemberProfile(Long id) {

        /**
         * 멤버프로필을 조회하는 getMemberProfile
         *
         * [고민]
         * 언제 null이 발생하는지도 생각해봐야 할 부분
         *
         * [흐름]
         * 조회 대상 id기반으로 사용자가 존재하는지 우선 조회
         * 없으면 예외 처리
         * 있으면 응답 DTO에 담아 컨트롤러로 반환
         */
        MemberResponse.ProfileResponse member = memberRepository.findMemberProfileById(id);

        if (member == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return member;
    }

    // TODO: 파일처리가 되는 즉시 프로필 수정 필요
    @Transactional
    public MemberResponse.UpdateProfileResponse changeProfile(Long id, MemberRequest.UpdateProfileRequest mu, Long currentMemberId) {
        if (!id.equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.USER_UPDATE_ACCESS_FORBIDDEN);
        }

        Member member = memberRepository.findMemberById(id);

        if (member == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (mu.getNickname() == null && mu.getUploadFile() == null && !mu.isRemoveImage()) {
            throw new BusinessException(ErrorCode.USER_UPDATE_EMPTY);
        }

        if (mu.getNickname() != null && !mu.getNickname().equals(member.getNickname())) {
            if (memberRepository.existsByNickname(mu.getNickname())) {
                throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
            }
            member.changeNickname(mu.getNickname());
        }


        String profileImageUrl = null;

        if (mu.getUploadFile() != null) {
            ProfileImage profileImage = profileImageRepository.findByMemberId(member.getId());

            if (profileImage == null) {
                ProfileImage newImage = new ProfileImage(
                        member, mu.getUploadFile().getOriginalName(),
                        mu.getUploadFile().getStoredPath(),
                        mu.getUploadFile().getS3Key()
                );

                profileImageRepository.save(newImage);
                profileImageUrl = newImage.getStoredPath();
            } else {
                profileImage.update(mu.getUploadFile().getOriginalName(),
                        mu.getUploadFile().getStoredPath(),
                        mu.getUploadFile().getS3Key());

                profileImageUrl = profileImage.getStoredPath();
            }
        } else if (mu.isRemoveImage()) {
            profileImageRepository.deleteByMemberId(member.getId());
            profileImageUrl = null;
        }

        return new MemberResponse.UpdateProfileResponse(
                member.getNickname(),
                profileImageUrl
        );
    }

    /**
     * @Transactional: 트랜잭션은 all or nothing
     * → 회원 수정이 완벽히 끝나거나 아예 롤백되어야 하므로 추가
     * 데이터를 수정하니 readonly는 디폴트인 false
     */
    @Transactional
    public void changePassword(Long id, MemberRequest.UpdatePwRequest mu, Long currentMemberId) {

        /**
         * 비밀번호를 수정하는 updatePassword
         *
         * [고민]
         * existsById or findById
         * 응답할 데이터가 필요 없으면 existsById로 단순 조회만 해도 괜찮을 것 같음
         * 어느 상황에 어느 메서드가 더 적합한지는 아직 판단이 안 섬
         *
         * [흐름]
         * 수정 대상 id와 로그인한 본인 id가 일치하는지 확인
         * 요청 DTO의 패스워드와 패스워드 확인 값을 .equals()로 비교
         * 수정 대상 id 기반으로 사용자 존재 확인, 없으면 예외 처리
         * 같으면 true, 다르면 false → !로 뒤집어 "일치하지 않을 때" 예외 처리
         * 검증을 모두 통과하면 새 비밀번호를 암호화해 리포지토리로 내려보냄
         */

        if (!id.equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.USER_UPDATE_ACCESS_FORBIDDEN);
        }

        if (!mu.getPassword().equals(mu.getPasswordCheck())) {
            throw new BusinessException(ErrorCode.MISMATCH_PASSWORD);
        }

        Member member = memberRepository.findMemberById(id);

        if (member == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        String newPassword = encoder.encode(mu.getPassword());
        member.changePassword(newPassword);
    }

    /**
     * @Transactional: 트랜잭션은 all or nothing
     * → 회원 삭제가 완벽히 끝나거나 아예 롤백되어야 하므로 추가
     * 데이터를 수정하니 readonly는 디폴트인 false
     */
    @Transactional
    public void deleteMember(Long id, Long currentMemberId) {

        /**
         * 회원을 삭제하는 deleteMember
         *
         * [고민]
         * 소프트 삭제로 구현한다면 SQL을 DELETE가 아닌 UPDATE로 바꾸고
         * 삭제 시간이나 상태 필드를 추가해야 할 것 같은데 맞는지 모르겠음
         *
         * [흐름]
         * 삭제 대상 id와 로그인한 본인 id가 일치하는지 확인
         * 삭제 대상 id 기반으로 사용자 존재 확인, 없으면 예외 처리
         * 삭제 대상 id로 삭제 요청
         */

        if (!id.equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.USER_DELETE_ACCESS_FORBIDDEN);
        }

        Member member = memberRepository.findMemberById(id);

        if (member == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        profileImageRepository.deleteByMemberId(member.getId());

        memberRepository.deleteById(member.getId());
        // memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public void checkEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
