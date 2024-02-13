package com.telerikacademy.web.forumsystem.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String saveImage(MultipartFile file);
}
