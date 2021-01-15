package com.amoalla.redditube.mediaposts.storage.impl;

import com.amoalla.redditube.mediaposts.storage.StorageService;
import com.amoalla.redditube.mediaposts.storage.exception.StorageException;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class MinioStorageServiceImpl implements StorageService {

    private final MinioClient minioClient;

    public MinioStorageServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String uploadMediaToStorage(String mediaUrl, String bucketName) throws StorageException {
        log.debug("Uploading {} to bucket: {}", mediaUrl, bucketName);
        URL url;
        try {
            url = new URL(mediaUrl);

            String fileName = StringUtils.getFilename(mediaUrl);
            if (fileName != null && fileName.contains("?")) {
                fileName = fileName.split("\\?")[0];
            }
            String contentType = Files.probeContentType(Paths.get(fileName));
            try (InputStream stream = url.openStream()) {
                Map<String, String> userMetadata = new HashMap<>();
                ObjectWriteResponse response = minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(stream, -1, 10485760)
                                .contentType(contentType)
                                .userMetadata(userMetadata)
                                .build()
                );
                return response.object();
            }
        } catch (Exception e) {
            throw new StorageException("An error happened while trying to store MediaPost", e);
        }
    }

    @Override
    public String createBucketIfNotExists(String bucketName) throws StorageException {
        try {
            bucketName = checkAndSanitizeBucketName(bucketName);
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return bucketName;
        } catch (Exception e) {
            throw new StorageException("An error happened while trying to create bucket: " + bucketName, e);
        }
    }

    // http://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html
    String checkAndSanitizeBucketName(String bucketName) {

        if (!StringUtils.hasText(bucketName)) {
            throw new IllegalArgumentException("bucketName can't be null or empty");
        }

        String sanitizedBucketName = bucketName;
        sanitizedBucketName = sanitizedBucketName.trim();

        // Between 3 and 63 characters
        if (sanitizedBucketName.length() < 3) {
            sanitizedBucketName = sanitizedBucketName + "." + sanitizedBucketName;
        }
        if (sanitizedBucketName.length() > 63) {
            sanitizedBucketName = sanitizedBucketName.substring(0, 63);
        }

        // Only lowercase, numbers, dots(.) and hyphens(-)
        sanitizedBucketName = sanitizedBucketName.toLowerCase(Locale.ROOT)
                .replace("_", "-")
                // Replace everything prohibited with dots
                .replaceAll("[^a-z0-9.\\-]", ".");

        // Begin and end with a letter or number
        char firstChar = sanitizedBucketName.charAt(0);
        if (!Character.isAlphabetic(firstChar) && !Character.isDigit(firstChar)) {
            sanitizedBucketName = sanitizedBucketName.replaceFirst(String.valueOf(firstChar), "z");
        }
        char lastChar = sanitizedBucketName.charAt(sanitizedBucketName.length() - 1);
        if (!Character.isAlphabetic(lastChar) && !Character.isDigit(lastChar)) {
            sanitizedBucketName = sanitizedBucketName.substring(0, sanitizedBucketName.length() - 1) + "z";
        }

        // Can't start with xn--
        String forbiddenPrefix = "xn--";
        if (sanitizedBucketName.startsWith(forbiddenPrefix)) {
            sanitizedBucketName = sanitizedBucketName.substring(forbiddenPrefix.length());
        }

        return sanitizedBucketName;
    }

    @Override
    public String getTemporaryLinkByObjectId(String objectId, String bucketName) throws StorageException {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(objectId)
                    .method(Method.GET)
                    .build());
        } catch (Exception e) {
            throw new StorageException("An error happened while generating link for objectId: " + objectId, e);
        }
    }
}
