package com.ktb.global.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Profile("prod")
@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(@Value("${aws.region}") String resion) {
        return S3Client.builder()
                .region(Region.of(resion))
                .build();
    }
}