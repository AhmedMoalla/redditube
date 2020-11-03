package com.amoalla.redditube.explore.controller;

import com.amoalla.redditube.client.model.MediaPost;
import com.amoalla.redditube.client.RedditClient;
import com.amoalla.redditube.client.model.Sort;
import com.amoalla.redditube.explore.controller.param.RequestParams;
import com.amoalla.redditube.explore.util.CaseInsensitiveEnumEditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class ExploreRestController {

    private final RedditClient redditClient;

    public ExploreRestController(RedditClient redditClient) {
        this.redditClient = redditClient;
    }

    @GetMapping("/{usernameOrSubreddit}")
    public Flux<MediaPost> getPosts(
            @PathVariable String usernameOrSubreddit,
            RequestParams params,
            Authentication authentication) {

        log.info("Received GET /{} with params: {}", usernameOrSubreddit, params);
        if (!StringUtils.isEmpty(params.getAfter())) {
            return redditClient.getPostsAfter(usernameOrSubreddit, params.getAfter(), params.getLimit());
        } else if (!StringUtils.isEmpty(params.getBefore())) {
            return redditClient.getPostsBefore(usernameOrSubreddit, params.getBefore(), params.getLimit());
        }
        return redditClient.getPosts(usernameOrSubreddit, params.getLimit());
    }

    @GetMapping("/{usernameOrSubreddit}/{sort}")
    public Flux<MediaPost> getPostsWithSorting(
            @PathVariable String usernameOrSubreddit,
            @PathVariable Sort sort,
            RequestParams params) {

        log.info("Received GET /{} with sort: {} and params: {}", usernameOrSubreddit, sort, params);
        if (!StringUtils.isEmpty(params.getAfter())) {
            return redditClient.getPostsAfter(usernameOrSubreddit, params.getAfter(), sort, params.getLimit());
        } else if (!StringUtils.isEmpty(params.getBefore())) {
            return redditClient.getPostsBefore(usernameOrSubreddit, params.getBefore(), sort, params.getLimit());
        }
        return redditClient.getPosts(usernameOrSubreddit, sort, params.getLimit());
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Sort.class, new CaseInsensitiveEnumEditor(Sort.class));
    }
}
