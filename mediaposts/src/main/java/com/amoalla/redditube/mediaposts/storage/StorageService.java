package com.amoalla.redditube.mediaposts.storage;

import com.amoalla.redditube.mediaposts.storage.exception.StorageException;

public interface StorageService {
    String uploadMediaToStorage(String mediaUrl, String bucketName) throws StorageException;
    String getTemporaryLinkByObjectId(String objectId, String bucketName) throws StorageException;
    String createBucketIfNotExists(String bucket) throws StorageException;
}
