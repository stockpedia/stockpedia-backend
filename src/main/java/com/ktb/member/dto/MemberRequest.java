package com.ktb.member.dto;


import com.ktb.file.dto.UploadFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

public class MemberRequest {

        @Getter
        public static class SignUpRequest {

            @NotBlank
            @Email
            private String email;

            @NotBlank
            private String password;

//            @NotBlank
//            private String passwordCheck;

            @NotBlank
            @Size(max = 10)
            private String nickname;

            @Valid
            private UploadFile uploadFile;
        }

    @Getter
    public static class LoginRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }

    @Getter
    public static class UpdateProfileRequest {

        @Size(min = 1, max = 10)
        private String nickname;

        private UploadFile uploadFile;

        private boolean removeImage;
    }

    @Getter
    public static class UpdatePwRequest {

        @NotBlank
        private String password;

        @NotBlank
        private String passwordCheck;

        @AssertTrue
        public boolean isPasswordMatching() {
            return password != null && password.equals(passwordCheck);
        }
    }
}
