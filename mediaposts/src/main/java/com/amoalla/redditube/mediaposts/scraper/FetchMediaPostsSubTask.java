package com.amoalla.redditube.mediaposts.scraper;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.client.impl.MediaPostRequester;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
public class FetchMediaPostsSubTask implements Callable<FetchMediaPostsSubTask.FetchMediaPostsResult> {

    private static int subtaskIdCounter = 1;
    private static final int LIMIT = MediaPostRequester.MAX_LIMIT;
    private final ExplorerService explorerService;
    private final String subscribableHandle;
    private final String after;
    @Getter
    private final int taskId = subtaskIdCounter++;

    public FetchMediaPostsSubTask(ExplorerService explorerService, Subscribable subscribable) {
        this.explorerService = explorerService;
        this.subscribableHandle = subscribable.getHandle();
        this.after = Objects.requireNonNullElse(subscribable.getLastFetchedPostId(), "");
    }

    public FetchMediaPostsSubTask(FetchMediaPostsSubTask subTask, Subscribable subscribable) {
        this.explorerService = subTask.explorerService;
        this.subscribableHandle = subTask.subscribableHandle;
        this.after = subscribable.getLastFetchedPostId();
    }

    @Override
    public FetchMediaPostsResult call() {

        log.debug("[Subtask:{}] Starting FetchMediaPostsSubTask with subscribable: {} and after: {}", taskId, subscribableHandle, after);
        List<MediaPostDto> mediaPosts = explorerService.getPosts(subscribableHandle, after, "", LIMIT)
                .collectList().block();
        if (mediaPosts != null && !mediaPosts.isEmpty()) {
            boolean hasMore = mediaPosts.size() == 100;
            String nextAfter = mediaPosts.get(mediaPosts.size() - 1).getId();
            return new FetchMediaPostsResult(hasMore, nextAfter, mediaPosts, this);
        } else if (mediaPosts != null) {
            return new FetchMediaPostsResult(false, "", new ArrayList<>(), this);
        }

        throw new IllegalStateException("Fetched MediaPost collection was null");
    }

    @RequiredArgsConstructor
    public static class FetchMediaPostsResult {
        private final boolean hasMore;
        @Getter
        private final String nextAfter;
        @Getter
        private final List<MediaPostDto> mediaPosts;
        @Getter
        private final FetchMediaPostsSubTask subTask;

        public boolean hasMore() {
            return hasMore;
        }
    }
}
