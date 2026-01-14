package it.younify.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import it.younify.core.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code MinioConfig} class provides the configuration for connecting to a Minio object storage server.
 * It defines the {@link MinioClient} bean that will be used throughout the application for object storage operations.
 */
@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Creates and configures the {@link MinioClient} bean.
     * <p>
     * This method initializes the client with the connection details from the application properties.
     * It also performs a crucial setup step: it checks if the target bucket exists, and if not,
     * it creates it. This ensures that the application's storage bucket is always available
     * on startup.
     *
     * @return A configured {@link MinioClient} instance.
     * @throws ResourceNotFoundException if an error occurs while connecting to Minio or creating the bucket.
     */
    @Bean
    public MinioClient minioClient() {

        MinioClient client = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();

        try {

            boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {

                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                System.out.println("Bucket " + bucketName + " created successfully");
            }

        }
        catch (Exception e){

            throw new ResourceNotFoundException(e.getMessage());
        }

        return client;
    }
}