package com.brunopiovan.test_aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.brunopiovan.test_aws.config.S3Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

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
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

            S3Object s3Object = s3Client.getObject(getObjectRequest);

            try (OutputStream outputStream = new FileOutputStream(downloadPath)) {
                s3Object.getObjectContent().transferTo(outputStream);
            }
            return "File downloaded successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Download failed: " + e.getMessage();
        }
    }
}