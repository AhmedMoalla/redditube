package com.amoalla.redditube.explorer.service;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.client.RedditClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Service
public class ExplorerServiceImpl implements ExplorerService {

    private final RedditClient redditClient;

    public ExplorerServiceImpl(RedditClient redditClient) {
        this.redditClient = redditClient;
    }

    public Flux<MediaPostDto> getPosts(String usernameOrSubreddit, String after, String before, int limit) {
        if (StringUtils.hasText(after)) {
            return redditClient.getPostsAfter(usernameOrSubreddit, after, limit);
        } else if (StringUtils.hasText(before)) {
            return redditClient.getPostsBefore(usernameOrSubreddit, before, limit);
        }
        return redditClient.getPosts(usernameOrSubreddit, limit);
    }

    public Flux<MediaPostDto> getPosts(String usernameOrSubreddit, String after, String before, int limit, Sort sort) {
        if (StringUtils.hasText(after)) {
            return redditClient.getPostsAfter(usernameOrSubreddit, after, sort, limit);
        } else if (StringUtils.hasText(before)) {
            return redditClient.getPostsBefore(usernameOrSubreddit, before, sort, limit);
        }
        return redditClient.getPosts(usernameOrSubreddit, sort, limit);
    }
}
