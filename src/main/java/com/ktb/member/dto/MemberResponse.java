package com.ktb.member.dto;

import com.ktb.file.dto.UploadFile;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberResponse {

    @Getter
    @AllArgsConstructor
    public static class SignUpResponse {

        private Long id;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginResponse {

        private Long id;

        private String nickname;

        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class ProfileResponse {

        private Long id;

        private String email;

        private String nickname;

        private String profileImageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateProfileResponse {

        private String nickname;

        private String profileImageUrl;
    }

}
