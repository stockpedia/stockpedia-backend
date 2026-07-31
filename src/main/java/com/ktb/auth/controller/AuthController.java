package com.ktb.auth.controller;

import com.ktb.global.utils.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
public class AuthController {


    @GetMapping("/auth/status")
    public ResponseEntity<ApiResponse<Long>> checkSession(
            @SessionAttribute(value = "loginMember", required = false) Long memberId
    ) {
        if (memberId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("로그인 상태입니다.", memberId));
    }
}
