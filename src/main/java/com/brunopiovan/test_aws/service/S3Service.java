package com.brunopiovan.test_aws.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.brunopiovan.test_aws.config.S3Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file, String key) {
        try {
            File tempFile = File.createTempFile("upload", file.getOriginalFilename());
            file.transferTo(tempFile);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, tempFile);

            s3Client.putObject(putObjectRequest);

            tempFile.delete();

            return "File uploaded successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload failed: " + e.getMessage();
        }
    }

    public String downloadFile(String key, String downloadPath) {
        try {
            File downloadDir = new File(downloadPath).getParentFile();
            if (downloadDir != null && !downloadDir.exists()) {
                downloadDir.mkdirs();
            }

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            S3Object s3Object = s3Client.getObject(getObjectRequest);

            long contentLength = s3Object.getObjectMetadata().getContentLength();
            System.out.println("Tamanho do arquivo S3: " + contentLength);

            // Transfere o conteúdo do S3 para um arquivo local
            try (OutputStream outputStream = new FileOutputStream(downloadPath)) {
                long transferredBytes = s3Object.getObjectContent().transferTo(outputStream);
                logger.info("Bytes transferidos: " + transferredBytes);
            }

            // Retorna uma mensagem de sucesso
            return "File downloaded successfully to: " + downloadPath;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Download failed: " + e.getMessage(), e);
            return "Download failed: " + e.getMessage();
        }
    }

    public String deleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
            logger.info("File with key '{}' deleted successfully from bucket '{}'", key, bucketName);
            return "File deleted successfully!";
        } catch (Exception e) {
            logger.error("Failed to delete file with key '{}': {}", key, e.getMessage(), e);
            throw new RuntimeException("Delete failed: " + e.getMessage(), e);
        }
    }

    public List<String> listVideos() {
        try {
            // Verifique se o bucket existe
            if (!s3Client.doesBucketExistV2(bucketName)) {
                throw new RuntimeException("Bucket não encontrado: " + bucketName);
            }

            // Listando todos os objetos no bucket
            List<S3ObjectSummary> objectSummaries = s3Client.listObjectsV2(bucketName).getObjectSummaries();

            // Filtrando arquivos de vídeo (por exemplo, com a extensão .mp4)
            List<String> videoUrls = objectSummaries.stream()
                    .filter(summary -> summary.getKey().endsWith(".mp4")) // Filtra arquivos .mp4
                    .map(summary -> s3Client.getUrl(bucketName, summary.getKey()).toString())
                    .collect(Collectors.toList());

            if (videoUrls.isEmpty()) {
                throw new RuntimeException("Nenhum vídeo encontrado no bucket.");
            }

            return videoUrls;
        } catch (Exception e) {
            e.printStackTrace();  // Aqui você pode logar ou imprimir o erro completo
            throw new RuntimeException("Erro ao listar vídeos no S3: " + e.getMessage(), e);
        }
    }


}