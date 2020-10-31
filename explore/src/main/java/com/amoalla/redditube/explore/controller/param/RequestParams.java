package com.amoalla.redditube.explore.controller.param;

import lombok.Data;

import static com.amoalla.redditube.client.RedditClient.DEFAULT_LIMIT;

@Data
public class RequestParams {
    private int limit = DEFAULT_LIMIT;
    private String after = "";
    private String before = "";
    private String count = "";
}
