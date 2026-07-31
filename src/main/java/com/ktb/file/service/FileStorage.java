package com.ktb.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    // String storeFile(MultipartFile file);

    String storeFile(MultipartFile file, String dir);

}
