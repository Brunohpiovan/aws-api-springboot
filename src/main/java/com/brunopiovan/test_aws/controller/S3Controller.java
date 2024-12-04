package com.brunopiovan.test_aws.controller;

import com.brunopiovan.test_aws.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // Endpoint para realizar o upload do arquivo
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("key") String key) {
        try {
            String response = s3Service.uploadFile(file, key);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestParam("key") String key,
                                               @RequestParam("downloadPath") String downloadPath) {
        try {
            String response = s3Service.downloadFile(key, downloadPath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Download failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("key") String key) {
        try {
            String response = s3Service.deleteFile(key);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Delete failed: " + e.getMessage());
        }
    }

    // Endpoint para listar vídeos
    @GetMapping("/videos")
    public ResponseEntity<List<String>> getVideos() {
        try {
            List<String> videoUrls = s3Service.listVideos();
            return ResponseEntity.ok(videoUrls);
        } catch (Exception e) {
            // Logando a exceção para o backend
            logger.error("Erro ao recuperar vídeos: ", e);
            // Retornando um erro 500 com a mensagem detalhada
            return ResponseEntity.status(500).body(null);
        }
    }


}
