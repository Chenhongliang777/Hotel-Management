package com.homestay.module.common.controller;

import com.homestay.common.exception.BusinessException;
import com.homestay.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "文件上传")
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url-prefix}")
    private String urlPrefix;

    @Operation(summary = "上传单个文件")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        
        String url = saveFile(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("name", file.getOriginalFilename());
        return Result.success(result);
    }

    @Operation(summary = "上传多个文件")
    @PostMapping("/upload/batch")
    public Result<List<Map<String, String>>> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        List<Map<String, String>> results = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = saveFile(file);
                Map<String, String> item = new HashMap<>();
                item.put("url", url);
                item.put("name", file.getOriginalFilename());
                results.add(item);
            }
        }
        
        return Result.success(results);
    }

    @Operation(summary = "通过URL添加图片")
    @PostMapping("/add-url")
    public Result<Map<String, String>> addImageByUrl(@RequestParam("url") String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new BusinessException("图片URL不能为空");
        }
        
        // 验证URL格式
        if (!isValidImageUrl(imageUrl)) {
            throw new BusinessException("无效的图片URL格式");
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("url", imageUrl.trim());
        result.put("name", "网络图片");
        return Result.success(result);
    }

    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        String lowerUrl = url.toLowerCase().trim();
        return lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://");
    }

    private String saveFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            
            Path dirPath = Paths.get(uploadPath, dateDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            Path filePath = dirPath.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            return urlPrefix + "/" + dateDir + "/" + newFilename;
            
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
}
