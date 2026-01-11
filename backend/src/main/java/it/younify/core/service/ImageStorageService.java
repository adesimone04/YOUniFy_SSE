package it.younify.core.service;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import it.younify.core.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadImage(MultipartFile file, String filename, String subFolder){

        try{

            byte[] webpBytes = sanitizeAndCompressImage(file);
            InputStream is = new ByteArrayInputStream(webpBytes);

            String originalFilename = file.getOriginalFilename();
            String newFilename = subFolder + "/" + filename + ".webp";

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFilename)
                    .stream(is, webpBytes.length, -1)
                    .contentType("image/webp")
                    .build());

            return newFilename;
        }
        catch(Exception e){

            throw new FileStorageException(e.getMessage());
        }
    }


    public void deleteImage(String objectName) {

        if (objectName == null || objectName.isBlank()) {

            throw new FileStorageException("File name is null or empty");
        }

        try {

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        }
        catch(Exception e){

            throw new FileStorageException(e.getMessage());
        }
    }


    public String getPresignedUrl(String fileName){

        if(fileName == null || fileName.isBlank()){

           throw new FileStorageException("File name is null or empty");
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
        }
        catch(Exception e){

            throw new FileStorageException(e.getMessage());
        }
    }


    private byte[] sanitizeAndCompressImage(MultipartFile originalFile) throws IOException {

        if(originalFile == null || originalFile.isEmpty()){

            throw new FileStorageException("File name is null or empty");
        }

        return ImmutableImage.loader()
                .fromStream(originalFile.getInputStream())
                .bytes(WebpWriter.DEFAULT);
    }
}
