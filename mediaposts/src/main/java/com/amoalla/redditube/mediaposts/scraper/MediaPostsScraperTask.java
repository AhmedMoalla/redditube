package com.amoalla.redditube.mediaposts.scraper;

import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.api.service.ExplorerServices;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.repository.SubscribableRepository;
import com.amoalla.redditube.mediaposts.scraper.FetchMediaPostsSubTask.FetchMediaPostsResult;
import com.amoalla.redditube.mediaposts.scraper.configuration.ScraperSchedulerProperties;
import com.amoalla.redditube.mediaposts.scraper.event.NewMediaPostsAvailableEvent;
import groovy.lang.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Phaser;

import static com.amoalla.redditube.client.impl.MediaPostRequester.MAX_LIMIT;

@Slf4j
@Component
public class MediaPostsScraperTask implements Runnable, InitializingBean {

    private final ApplicationEventPublisher eventPublisher;
    private final ThreadPoolTaskScheduler scheduler;
    private final SubscribableRepository repository;
    private final ExplorerServices explorerServices;
    private final ScraperSchedulerProperties properties;

    private int nbPostsScraped = 0;

    public MediaPostsScraperTask(ApplicationEventPublisher eventPublisher, ThreadPoolTaskScheduler scheduler,
                                 SubscribableRepository repository, ExplorerServices explorerServices,
                                 ScraperSchedulerProperties properties) {

        this.eventPublisher = eventPublisher;
        this.scheduler = scheduler;
        this.repository = repository;
        this.explorerServices = explorerServices;
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        scheduler.execute(this);
    }

    @Override
    public void run() {

        Iterable<Subscribable> subscribables = repository.findAll();
        List<Tuple2<Subscribable, FetchMediaPostsSubTask>> subTasks = new ArrayList<>();
        log.info("Started scraping posts for {} subscribables", subscribables.spliterator().getExactSizeIfKnown());
        for (Subscribable subscribable : subscribables) {
            ExplorerService service;
            if (SubscribableType.USER.equals(subscribable.getType())) {
                service = explorerServices.users();
            } else {
                service = explorerServices.subreddits();
            }

            if (hasNewPosts(service, subscribable)) {
                var fetchPostsSubTask = new FetchMediaPostsSubTask(service, subscribable);
                subTasks.add(new Tuple2<>(subscribable, fetchPostsSubTask));
                continue;
            }

            log.info("Subscribable {} has no new posts. Nothing to fetch.", subscribable.getHandle());
        }

        submitTasksAndAwaitFinish(subTasks);
        log.info("Finished scraping {} posts for {} subscribables", nbPostsScraped, subscribables.spliterator().getExactSizeIfKnown());
        nbPostsScraped = 0;

        scheduleNextScrapingTask();
    }

    private boolean hasNewPosts(ExplorerService service, Subscribable subscribable) {
        int latestPostsCount = service.getPosts(subscribable.getHandle(), subscribable.getLastFetchedPostId(), "", MAX_LIMIT)
                .collectList().block()
                .size();
        int latestFetchedPostCount = Objects.requireNonNullElse(subscribable.getLastFetchedPostCount(), 0);
        return latestFetchedPostCount == MAX_LIMIT || latestPostsCount > latestFetchedPostCount;
    }

    private void submitTasksAndAwaitFinish(List<Tuple2<Subscribable, FetchMediaPostsSubTask>> subTasks) {
        if (subTasks != null && !subTasks.isEmpty()) {
            Phaser phaser = new Phaser();
            phaser.bulkRegister(subTasks.size());

            for (Tuple2<Subscribable, FetchMediaPostsSubTask> tuple : subTasks) {
                var resultFuture = scheduler.submitListenable(tuple.getSecond());
                addResultFutureCallback(resultFuture, tuple.getFirst(), phaser);
            }

            phaser.awaitAdvance(0);
        }
    }

    private void addResultFutureCallback(ListenableFuture<FetchMediaPostsResult> resultFuture, Subscribable subscribable, Phaser phaser) {

        resultFuture.addCallback(
                result -> {
                    if (result != null) {
                        if (result.getMediaPosts() != null && !result.getMediaPosts().isEmpty()) {
                            // Publish new media posts event
                            eventPublisher.publishEvent(new NewMediaPostsAvailableEvent(subscribable, result.getMediaPosts()));
                        }

                        if (result.hasMore()) {
                            publishResultsAndStartNextSubtask(result, subscribable, phaser);
                        }

                        if (!result.getMediaPosts().isEmpty()) {
                            subscribable.setLastFetchedPostCount(result.getMediaPosts().size());
                            repository.save(subscribable);
                        }
                        nbPostsScraped += result.getMediaPosts().size();
                        log.debug("[SubTask:{}] Finished fetching {} posts from subscribable {} and hasMore = {}",
                                result.getSubTask().getTaskId(), result.getMediaPosts().size(), subscribable.getHandle(),
                                result.hasMore());
                    }
                    phaser.arriveAndDeregister();
                },
                ex -> {
                    phaser.arriveAndDeregister(); // To prevent getting stuck if there's an error
                    log.error("Error happened while fetching posts for {} with after: '{}'",
                            subscribable.getHandle(), "", ex);
                });
    }

    private void publishResultsAndStartNextSubtask(FetchMediaPostsResult result, Subscribable subscribable, Phaser phaser) {
        // Save next after post id
        subscribable.setLastFetchedPostId(result.getNextAfter());
        subscribable.setLastFetchedPostCount(result.getMediaPosts().size());
        repository.save(subscribable);

        // Create next sub task
        var nextSubTask = new FetchMediaPostsSubTask(result.getSubTask(), subscribable);
        phaser.register();
        var nextResultFuture = scheduler.submitListenable(nextSubTask);
        addResultFutureCallback(nextResultFuture, subscribable, phaser);
    }

    private void scheduleNextScrapingTask() {
        Instant nextExecutionTime = Instant.now().plus(properties.getRestartPeriod());
        log.info("Starting next scraping at: {}", nextExecutionTime);
        scheduler.schedule(this, nextExecutionTime);
    }
}
