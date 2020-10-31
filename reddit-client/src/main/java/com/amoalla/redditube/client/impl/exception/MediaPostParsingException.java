package com.amoalla.redditube.client.impl.exception;

import com.fasterxml.jackson.databind.JsonNode;

public class MediaPostParsingException extends RuntimeException {
    public MediaPostParsingException(JsonNode json, Exception e) {
        super("Error while parsing media post: \n" + json.toPrettyString(), e);
    }
}
