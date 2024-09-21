package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.minio.MinioProperties;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Resource
    private MinioProperties properties;

    @Resource
    private MinioClient minioClient;

    //此处有疑问，为啥这里properties为null！！！！！！
    public String sName;


    public String upload(MultipartFile file) throws Exception{
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(properties.getBucketName()).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(properties.getBucketName()).config(createPolicy(properties.getBucketName())).build());
        }
        String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        minioClient.putObject(PutObjectArgs.builder().bucket(properties.getBucketName())
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType()).object(filename).build());
        return  String.join("/",properties.getEndpoint(),properties.getBucketName(),filename);
    }



    public String createPolicy(String bucketName){
        return """
                    {
                                "Version": "2012-10-17",
                                "Statement": [
                                    {
                                        "Action": "s3:GetObject",
                                        "Effect": "Allow",
                                        "Principal": "*",
                                        "Resource": "arn:aws:s3:::%s/*"
                                    }
                                ]
                    }
                """.formatted(bucketName);
    }

}
