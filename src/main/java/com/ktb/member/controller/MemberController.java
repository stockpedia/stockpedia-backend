package com.ktb.member.controller;

import com.ktb.global.utils.response.ApiResponse;
import com.ktb.member.domain.Member;
import com.ktb.member.service.MemberService;

import com.ktb.member.dto.MemberRequest;
import com.ktb.member.dto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController: 스프링 빈 등록, 프레젠테이션 계층(컨트롤러)임을 명시 @Controller + @ResponseBody 합쳐진 형태
 * @RequestBody: HTTP 요청 본문(JSON 등)을 자바 객체로 역직렬화
 * HttpMessageConverter(보통 Jackson, MappingJackson2HttpMessageConverter)가 변환 담당
 * @Controller와의 차이
 * @Controller: 반환값을 View 이름으로 해석 → 템플릿 엔진등이 HTML 렌더링 → SSR
 * @RestController: 반환값을 HTTP 응답 본문에 직접 직렬화(보통 JSON) → REST API에 적합
 * @RequiredArgsConstructor: final 필드만 모아서 생성자 자동 생성 → DI(생성자 주입) 방식
 * 생성자 주입을 사용하는 이유: 불변성 보장(final), 컴파일 타임에 감지
 */
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    /**
     * ResponseEntity: 응답으로 단순 객체를 반환하지 않고 상태코드, 헤더, 바디를 직접 제어하기 위해 사용
     * HTTP 응답을 담는 객체
     * <p>
     * ApiResponse: 클라이언트에 통일된 응답 형식을 전달하기 위한 공통 응답 클래스
     * 제네릭으로 타입을 받아 어떤 타입이든 응답으로 내려줄 수 있음
     * 맵으로 ResponseHandler를 구현해 사용할 수도 있음 (자세한 내용은 ApiResponse 클래스 참고)
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<MemberResponse.SignUpResponse>> createMember(
            /**
             * 회원가입을 처리하는 signup
             *
             * 컨트롤러의 요청 애노테이션 README.md에서 정리
             *
             * [고민]
             * 유저를 생성하는 행위이므로 POST 메서드
             * 자원은 컬렉션이므로 복수형 'users' 사용
             *
             * [흐름]
             * 서비스 레이어의 createMember 메서드에 DTO 넘겨 회원가입 처리
             * 결과에서 식별자 추출해 응답 DTO 생성
             * 상태코드 201, 바디에 성공 메시지와 응답 결과를 담아 반환
             */
            @Valid @RequestBody MemberRequest.SignUpRequest ms) {

        Member member = memberService.createMember(ms);
        MemberResponse.SignUpResponse response = new MemberResponse.SignUpResponse(member.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", response));
    }


    @PostMapping("/auth")
    public ResponseEntity<ApiResponse<MemberResponse.LoginResponse>> login(
            @Valid @RequestBody MemberRequest.LoginRequest ml, HttpServletRequest request) {
        /**
         * 로그인을 처리하는 login
         *
         * [고민]
         * 인증, 세션을 생성하는 행위이므로 POST
         * 자원은 인증인 auth
         * 세션 방식 인증이라 세션 생성을 위해 HttpServletRequest 객체를 함께 받음
         *
         * [흐름]
         * 요청 DTO에서 접근자로 이메일과 패스워드를 꺼내 서비스 레이어의 login 메서드로 내려보냄
         * 예외 처리를 통과한 객체로 세션을 생성하고, 식별자를 세션 값으로 등록
         * 상태코드 200, 바디에 성공 메시지와 응답 결과를 담아 반환
         */
        MemberResponse.LoginResponse loginMember = memberService.login(ml.getEmail(), ml.getPassword());

        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("로그인 성공", loginMember));
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<MemberResponse.ProfileResponse>> getMemberProfile(
            @PathVariable Long id) {
        /**
         * 멤버프로필을 조회하는 getMemberProfile
         *
         * [고민]
         * 경로로 식별자를 받아 멤버 프로필을 조회하므로 GET
         * URI는 자원 밑에 식별자 (/users/{id})
         *
         * [흐름]
         * 식별자로 멤버 정보를 조회하고, 결과를 상태코드 200, 바디에 성공 메시지와 응답 결과를 담아 반환
         */
        MemberResponse.ProfileResponse response = memberService.getMemberProfile(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("회원정보 조회에 성공했습니다.", response));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<ApiResponse<MemberResponse.UpdateProfileResponse>> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest.UpdateProfileRequest mu,
            @SessionAttribute("loginMember") Long currentMemberId
    ) {
        MemberResponse.UpdateProfileResponse response =
                memberService.changeProfile(id, mu, currentMemberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("회원정보 변경에 성공했습니다.", response));
    }


    @PutMapping("/users/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest.UpdatePwRequest mu,
            @SessionAttribute("loginMember") Long currentMemberId
    ) {
        /**
         * 비밀번호를 수정하는 updatePassword
         *
         * [고민]
         * 비밀번호를 새 값으로 교체하는 행위이므로 PUT
         * URI는 자원 밑에 식별자의 하위 자원 (users/{id}/password)
         * 응답에 줄 데이터가 없어 204 No Content
         *
         * [흐름]
         * 경로의 식별자, 요청 DTO, 세션에서 꺼낸 로그인 사용자 id를
         * 응답 본문 없이 204 반환
         */
        memberService.changePassword(id, mu, currentMemberId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long id,
            @SessionAttribute("loginMember") Long currentMemberId,
            HttpServletRequest request) {
        /**
         * 회원을 삭제하는 deleteMember
         *
         * [고민]
         * 자원을 삭제하는 행위이므로 DELETE
         * URI는 자원 밑에 식별자 (users/{id})
         * 응답에 줄 데이터가 없어 204 No Content
         *
         * [흐름]
         * 경로의 식별자, 세션에서 꺼낸 로그인 사용자 id를
         * HttpServletRequest에서 기존 세션 조회, 없으면 null 반환
         * 세션이 존재하면 invalidate로 무효화(쿠키 삭제도 추가적으로 가능)
         * 응답 본문 없이 204 반환
         */
        memberService.deleteMember(id, currentMemberId);
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/auth")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        /**
         * 로그아웃을 처리하는 logout
         *
         * [고민]
         * 인증 세션을 제거하는 행위이므로 DELETE
         * 자원은 인증인 auth
         *
         * [흐름]
         * getSession(false): 세션이 없을 때 새로 만들지 않기 위해 false 전달
         * true: 세션이 있으면 가져오고, 없으면 새로 만듬
         * false: 세션이 있으면 가져오고, 없으면 만들지 않음
         * HttpServletRequest에서 기존 세션 조회, 없으면 null 반환
         * 세션이 존재하면 invalidate로 무효화(쿠키 삭제도 추가적으로 가능)
         * 상태코드 200, 바디에 성공 메시지를 담아 반환
         */
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("로그아웃", null));
    }

    @GetMapping("/users/email/check")
    public ResponseEntity<ApiResponse<Void>> checkEmail(
            @RequestParam String email
    ) {
        memberService.checkEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("사용 가능한 이메일입니다.", null));
    }

    @GetMapping("/users/nickname/check")
    public ResponseEntity<ApiResponse<Void>> checkNickname(
            @RequestParam String nickname
    ) {
        memberService.checkNickname(nickname);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("사용 가능한 이메일입니다.", null));
    }
}
