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

/**
 * The {@code ImageStorageService} class provides a high-level abstraction for interacting with the object storage (Minio).
 * It handles uploading, deleting, and generating URLs for images, and includes logic for image processing.
 *
 * @see io.minio.MinioClient
 */
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Uploads an image to the object storage.
     * <p>
     * This method first sanitizes and compresses the image into the WebP format. It then uploads
     * the processed image to the specified sub-folder in the Minio bucket.
     *
     * @param file The image file to upload.
     * @param filename The base name for the new file (without extension).
     * @param subFolder The sub-folder within the bucket where the image will be stored.
     * @return The full path of the uploaded image in the bucket.
     * @throws FileStorageException if an error occurs during the upload process.
     */
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


    /**
     * Deletes an image from the object storage.
     *
     * @param objectName The full path of the object to delete within the bucket.
     * @throws FileStorageException if the object name is invalid or an error occurs during deletion.
     */
    public void deleteImage(String objectName) {

        if (objectName == null || objectName.isBlank()) {

            throw new FileStorageException("File is null or empty","FILE_NOT_VALID");
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


    /**
     * Generates a temporary, pre-signed URL to access a private object in the storage.
     * This is the primary method for allowing clients to view images without making the bucket public.
     *
     * @param fileName The full path of the object within the bucket.
     * @return A pre-signed URL that is valid for a limited time (e.g., 2 hours).
     * @throws FileStorageException if the file name is invalid or an error occurs during URL generation.
     */
    public String getPresignedUrl(String fileName){

        if(fileName == null || fileName.isBlank()){

           throw new FileStorageException("File is null or empty", "FILE_NOT_VALID");
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


    /**
     * Sanitizes and compresses an image file into the WebP format.
     * This private utility method is used to standardize and optimize images before they are uploaded.
     *
     * @param originalFile The original image file.
     * @return A byte array representing the compressed WebP image.
     * @throws IOException if an error occurs while reading the image stream.
     * @throws FileStorageException if the input file is null or empty.
     */
    private byte[] sanitizeAndCompressImage(MultipartFile originalFile) throws IOException {

        if(originalFile == null || originalFile.isEmpty()){

            throw new FileStorageException("File is null or empty", "FILE_NOT_VALID");
        }

        return ImmutableImage.loader()
                .fromStream(originalFile.getInputStream())
                .bytes(WebpWriter.DEFAULT);
    }
}
