package it.younify.core.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import it.younify.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadImage(MultipartFile file, String filename , String subFolder) throws IOException {
        try{
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";

            String newFilename = subFolder + "/" + filename +extension;

            InputStream is = file.getInputStream();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFilename)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return newFilename;
        }catch (Exception e){
            throw new ResourceNotFoundException(e.getMessage());
        }

    }

    public String getPresignedUrl(String fileName) throws IOException {
        if(fileName==null || fileName.isEmpty()){
            return null;
        }

        try{
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(2, TimeUnit.HOURS)
                            .build()
            );
        }catch (Exception e){
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
