package com.ktb.global.utils.response;

public record ApiResponse<T>(boolean success, String code, String message, T data) {

//    private final boolean success;
//    private final String code;
//    private final String message;
//    private final T data;
//
//    public ApiResponse(boolean success, String code, String message, T data) {
//        this.success = success;
//        this.code = code;
//        this.message = message;
//        this.data = data;
//    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, null, message, data);
    }

    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}