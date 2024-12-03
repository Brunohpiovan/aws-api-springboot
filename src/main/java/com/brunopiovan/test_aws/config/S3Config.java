package com.brunopiovan.test_aws.config;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class S3Config {


    @Bean
    public AmazonS3Client s3Client() {
        return (AmazonS3Client) AmazonS3Client.builder()
                .withRegion(Regions.US_EAST_2)
                .build();
    }
}
