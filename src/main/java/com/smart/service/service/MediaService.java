package com.smart.service.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface MediaService {

    Map upload(MultipartFile file, String folder);
}
