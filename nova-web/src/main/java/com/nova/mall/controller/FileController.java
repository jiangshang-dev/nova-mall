package com.nova.mall.controller;

import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.oss.FileStorageService;
import com.nova.mall.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传")
@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileController {
    private final FileStorageService fileStorageService;

    @Operation(summary = "上传文件")
    @Debounce
    @OperLog(title = "文件上传", businessType = BusinessType.INSERT)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        return R.ok(fileStorageService.upload(file));
    }
}
