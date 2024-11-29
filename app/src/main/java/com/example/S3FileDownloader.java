package com.example;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class S3FileDownloader {

    private static String accessKey;
    private static String secretKey;
    private static String region;

    public static AmazonS3 s3Client() {
        return Optional.ofNullable(accessKey)
                .map(key -> {
                    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(key, secretKey);
                    return AmazonS3ClientBuilder.standard()
                            .withRegion(region)
                            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                            .build();
                })
                .orElseGet(() -> AmazonS3ClientBuilder.standard().build());
    }


    public static void downloadFileFromS3(String bucketName, String objectKey) {
        AmazonS3 s3Client;
        try {

            s3Client = s3Client();

            S3Object s3Object = s3Client.getObject(bucketName, objectKey);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            File outputFile = new File("/tmp/" + objectKey);
            if (outputFile.exists()) {
                System.out.println("File already exists - " + outputFile.getAbsolutePath());
                return;
            }

            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("File downloaded successfully to: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error writing file: " + e.getMessage());
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            System.err.println("Error downloading file from S3: " + e.getMessage());
        }
    }

}
