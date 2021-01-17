package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.listener.MediaPostStorageListener;
import com.amoalla.redditube.mediaposts.listener.NewSingleMediaPostAvailableEvent;
import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import com.amoalla.redditube.mediaposts.scraper.event.MediaPostsScraperTaskFinished;
import com.amoalla.redditube.mediaposts.scraper.event.NewMediaPostsAvailableEvent;
import com.amoalla.redditube.mediaposts.storage.StorageService;
import com.amoalla.redditube.mediaposts.storage.cache.MediaHashCache;
import com.amoalla.redditube.mediaposts.storage.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class CompositeMediaPostStorageListener extends AbstractMediaPostStorageListener {

    private final ApplicationEventPublisher eventPublisher;
    private final List<MediaPostStorageListener> listeners;
    private AtomicInteger nbTasks = new AtomicInteger(0);
    private AtomicInteger totalNbTasks = new AtomicInteger(0);

    public CompositeMediaPostStorageListener(MediaPostRepository repository,
                                             StorageService storageService,
                                             ThreadPoolTaskScheduler scheduler,
                                             MediaHashCache mediaHashCache,
                                             ApplicationEventPublisher eventPublisher,
                                             List<MediaPostStorageListener> listeners) {
        super(repository, storageService, scheduler, mediaHashCache);
        this.eventPublisher = eventPublisher;
        this.listeners = listeners;
    }

    @Override
    @Async
    @EventListener
    public void onNewMediaPostsAvailableEvent(NewMediaPostsAvailableEvent event) throws StorageException {
        Subscribable subscribable = event.getSubscribable();
        String bucketName = storageService.createBucketIfNotExists(getBucketNameForSubscribable(subscribable));
        log.debug("Received event with {} mediaposts for subscribable {}", event.getMediaPosts().size(), subscribable.getHandle());

        int counter = 0;
        for (MediaPostDto mediaPostDto : event.getMediaPosts()) {
            MediaPostStorageListener matchingListener = listeners.stream()
                    .filter(listener -> listener.matches(mediaPostDto))
                    .findFirst()
                    .orElse(this);

            counter += matchingListener.getNumberOfUploadTasks(mediaPostDto);
        }
        incrementNbTasks(counter);

        for (MediaPostDto mediaPostDto : event.getMediaPosts()) {
            MediaPostStorageListener matchingListener = listeners.stream()
                    .filter(listener -> listener.matches(mediaPostDto))
                    .findFirst()
                    .orElse(this);

            matchingListener.onNewMediaPostAvailable(new NewSingleMediaPostAvailableEvent(subscribable, mediaPostDto, bucketName, this::decrementNbTasks));
        }
    }

    @Override
    public void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event) {
        uploadAndSaveMediaPost(event.getMediaPost(), event.getSubscribable(), event.getBucketName(), this::decrementNbTasks);
    }

    @Override
    public boolean matches(MediaPostDto mediaPostDto) {
        return true;
    }

    protected void incrementNbTasks(int nb) {
        int currentNbTasks = this.nbTasks.addAndGet(nb);
        totalNbTasks.addAndGet(nb);
        log.debug("Increment number of current tasks by {}. Current tasks: {}", nb, currentNbTasks);
    }

    protected void decrementNbTasks() {
        int currentNbTasks = this.nbTasks.decrementAndGet();
        log.debug("Decrement number of current tasks. Current tasks: {}", currentNbTasks);
        if (currentNbTasks == 0) {
            eventPublisher.publishEvent(new MediaPostsScraperTaskFinished(totalNbTasks.get()));
            nbTasks = new AtomicInteger(0);
            totalNbTasks = new AtomicInteger(0);
        }
    }
}
