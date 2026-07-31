    package com.ktb.file.service;

import com.ktb.global.utils.exception.BusinessException;
import com.ktb.global.utils.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 레이어드 아키텍처(Controller/Service/Repository)에 들어가지 않는 유틸이라 생각
 *
 * @Service보다 @Component가 의미상 더 맞다고 판단
 * 프로필 전용으로 책임을 좁힘
 */
@Profile({"local", "test"})
@Component
public class LocalFileStorage implements FileStorage {

    /**
     * 프로필만 저장하는 클래스이니 속성으로 profiles를 가지고 있음
     * 외부에서 매개변수로 받는 구조라면 enum도 고려
     */
    // private static final String BASE_PATH = "uploads";

    @Value("${file.upload-path}")
    private String basePath;

    private Path uploadPath;

    /**
     * 업로드 디렉토리를 서버 시작 시 한 번 초기화하는 init
     * <p>
     * [고민]
     * 업로드 요청마다 디렉터리 존재 여부를 확인하기보다
     * 애플리케이션 시작 시 한 번 초기화하는 편이 책임이 명확하다고 판단
     * base 경로("uploads/")는 yml + @Value로 뺄 수도 있지만
     * 지금은 작은 기능이라 문자열로 박아둠
     * <p>
     *
     * [흐름]
     * 상대경로 조합 → 절대경로 변환 + normalize(., .. 등 경로를 정리하여 표준화)
     * 디렉토리 생성 시도, 실패하면 에러
     */
    @PostConstruct
    public void init() {
        /**
         * 업로드 루트 디렉터리 경로 생성
         *
         * BASE_PATH = "uploads/"
         *
         * 현재 프로젝트 위치
         * ~/Documents/KTB
         *
         * Paths.get(BASE_PATH)
         * → uploads
         *
         * toAbsolutePath()
         * → /Users/{사용자명}/Documents/KTB/uploads
         *
         * normalize()
         * → ".", ".." 등을 제거하여 경로를 표준화
         *
         * 최종 결과:
         * /Users/{사용자명}/Documents/KTB/uploads
         *
         */
        this.uploadPath = Paths.get(basePath).toAbsolutePath().normalize();

//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }

        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 파일을 디스크에 저장하고 접근 경로를 반환하는 storeFile
     *
     * [고민]
     * S3나 post에서 파일 처리 확장을 위해 인터페이스에서 선언후 구현
     * 확장자 추출은 메서드로 안 해도 되는데 단일 책임을 위해 분리
     * UUID까지 분리해도 되지만 흐름을 잃어버릴거 같아 남겨둠
     * 경로를 반환할지 서버에 저장될 파일명만 반환할지 고민중
     *
     * [흐름]
     * 파일이 널인지 빈문자열인지 확인 → 비즈니스 예외 처리
     * 파일 저장 시작
     * 원본 파일명에서 확장자 추출
     * UUID + 확장자로 저장 파일명 생성
     * 저장 경로 조합 후 디스크에 복사
     */
    @Override
    public String storeFile(MultipartFile file, String dir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_REQUIRED);
        }

        try {
            Path targetDir = uploadPath.resolve(dir).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            // 원본 파일명 가져옴
            String originalFilename = file.getOriginalFilename();

            // 원본 파일명에서 확장자만 꺼냄
            String ext = extractExt(originalFilename);

            // UUID 이용해 서버에 저장할 파일명을 새로 만듬
            String storedFilename = UUID.randomUUID() + "." + ext;

            // 최종 파일 경로
            Path targetFile = targetDir.resolve(storedFilename).normalize();

            // 실제 파일 저장
            // Files.copy: NIO 표준, REPLACE_EXISTING으로 덮어쓰기를 명시적으로 선언 가능
            // transferTo: Spring MultipartFile 편의 메서드, 짧지만 옵션 지정 불가
            // transferTo안에서 this.getInputStream() 호출
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            // file.transferTo(filePath);

            return "/" + basePath + "/" + dir + "/" + storedFilename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 원본 파일명에서 확장자만 잘라내는 extractExt
     *
     * [고민]
     * 파일명에 점이 없으면 잘못된 입력이라 보고 예외 처리
     *
     * [흐름]
     * null 또는 점 없는 파일명이면 예외
     * 마지막 점 위치를 찾아 그 뒤를 반환
     */
    private String extractExt(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(ErrorCode.INVALID_FILE_NAME);
        }

        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1); // 결과 -> png

        // riginalFilename.substring(originalFilename.lastIndexOf(".")); // 결과 -> .png
    }
}
