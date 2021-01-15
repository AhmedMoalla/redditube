package com.amoalla.redditube.mediaposts.storage.impl;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MinioStorageServiceImpl.class)
class MinioStorageServiceImplTest {

    private static final String OBJECT_ID = "OBJECT_ID";
    private static final String TEST_URL = "TEST_URL";

    private static MockWebServer mockWebServer;

    @MockBean
    private MinioClient minioClient;

    @Autowired
    private MinioStorageServiceImpl minioStorageService;

    @BeforeAll
    static void setUpMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() throws Exception {
        var mockResponse = new ObjectWriteResponse(null, null, null, OBJECT_ID, null, null);
        when(minioClient.putObject(Mockito.any()))
                .thenReturn(mockResponse);

        when(minioClient.bucketExists(Mockito.any()))
                .thenReturn(false);
        doNothing().when(minioClient).makeBucket(Mockito.any());

        when(minioClient.getPresignedObjectUrl(Mockito.any()))
                .thenReturn(TEST_URL);
    }

    @Test
    void testUploadMediaToStorage() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody(new Buffer()));
        String mediaUrl = String.format("http://localhost:%d/file.mp4?arg1=val1", mockWebServer.getPort());
        String bucketName = minioStorageService.checkAndSanitizeBucketName("bucketName");
        String objectId = minioStorageService.uploadMediaToStorage(mediaUrl, bucketName);
        assertEquals(OBJECT_ID, objectId);
        verify(minioClient).putObject(Mockito.any());
    }

    @Test
    void testCreateBucketIfNotExists() throws Exception {
        String bucketName = "bucketName";
        String createdBucketName = minioStorageService.createBucketIfNotExists(bucketName);
        verify(minioClient).bucketExists(Mockito.any());
        verify(minioClient).makeBucket(Mockito.any());
        assertEquals(minioStorageService.checkAndSanitizeBucketName(bucketName), createdBucketName);
    }

    @Test
    void testCheckAndSanitizeBucketName() {
        assertThrows(IllegalArgumentException.class, () -> minioStorageService.checkAndSanitizeBucketName(""));

        String bucketName = "a";
        bucketName = minioStorageService.checkAndSanitizeBucketName(bucketName);
        assertEquals("a.a", bucketName);

        bucketName = "a".repeat(100);
        bucketName = minioStorageService.checkAndSanitizeBucketName(bucketName);
        assertEquals(63, bucketName.length());

        bucketName = "AbCd*Ef_Gh$I";
        bucketName = minioStorageService.checkAndSanitizeBucketName(bucketName);
        assertEquals("abcd.ef-gh.i", bucketName);

        bucketName = ".eaea-";
        bucketName = minioStorageService.checkAndSanitizeBucketName(bucketName);
        assertEquals("zeaeaz", bucketName);

        bucketName = "xn--bucketname";
        bucketName = minioStorageService.checkAndSanitizeBucketName(bucketName);
        assertEquals("bucketname", bucketName);
    }

    @Test
    void testGetTemporaryLinkByObjectId() throws Exception {
        String url = minioStorageService.getTemporaryLinkByObjectId(OBJECT_ID, "bucketname");
        assertEquals(TEST_URL, url);
        verify(minioClient).getPresignedObjectUrl(Mockito.any());
    }
}