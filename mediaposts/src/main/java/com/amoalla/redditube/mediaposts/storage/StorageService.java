package com.amoalla.redditube.mediaposts.storage;

import com.amoalla.redditube.mediaposts.storage.exception.StorageException;

import java.io.InputStream;

public interface StorageService {
    String uploadMediaToStorage(InputStream mediaData, String fileName, String bucketName) throws StorageException;
    String getTemporaryLinkByObjectId(String objectId, String bucketName) throws StorageException;
    String createBucketIfNotExists(String bucket) throws StorageException;
}
