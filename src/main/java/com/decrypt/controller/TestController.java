package com.decrypt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")

public class TestController {
    @PostMapping("/Upload")
    public String upload (@RequestParam("keys") MultipartFile file1, @RequestParam("list") MultipartFile file2, @RequestParam("output") String token) {
        
        try {
            // 检查文件是否为空
            if (file1.isEmpty()) {
                return "秘钥文档不能为空";
            }if (file2.isEmpty()) {
                return "资源结构文档不能为空";
            }

            // 这里可以添加文件保存逻辑
            file1.transferTo(new File("/"+token+"/keys.xml"));
            file2.transferTo(new File("/"+token+"/list.xml"));

            return "上传成功";
        } catch (Exception e) {
            log.error("上传失败: {}", e.getMessage());
            return "上传失败";
        }
    }
}