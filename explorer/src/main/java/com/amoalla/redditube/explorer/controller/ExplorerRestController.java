package com.amoalla.redditube.explorer.controller;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.commons.util.CaseInsensitiveEnumEditor;
import com.amoalla.redditube.explorer.controller.param.RequestParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class ExplorerRestController {

    private final ExplorerService explorerService;

    public ExplorerRestController(ExplorerService explorerService) {
        this.explorerService = explorerService;
    }


    @GetMapping("/{usernameOrSubreddit}")
    public Flux<MediaPostDto> getPosts(
            @PathVariable String usernameOrSubreddit,
            RequestParams params) {

        log.info("Received GET /{} with params: {}", usernameOrSubreddit, params);
        return explorerService.getPosts(usernameOrSubreddit, params.getAfter(), params.getBefore(), params.getLimit());
    }

    @GetMapping("/{usernameOrSubreddit}/{sort}")
    public Flux<MediaPostDto> getPostsWithSorting(
            @PathVariable String usernameOrSubreddit,
            @PathVariable Sort sort,
            RequestParams params) {

        log.info("Received GET /{} with sort: {} and params: {}", usernameOrSubreddit, sort, params);
        return explorerService.getPosts(usernameOrSubreddit, params.getAfter(), params.getBefore(), params.getLimit(), sort);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Sort.class, new CaseInsensitiveEnumEditor(Sort.class));
    }
}
