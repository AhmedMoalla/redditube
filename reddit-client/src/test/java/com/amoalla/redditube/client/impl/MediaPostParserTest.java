package com.amoalla.redditube.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.amoalla.redditube.client.impl.MediaPostParser.CHILDREN_KEY;
import static com.amoalla.redditube.client.impl.MediaPostParser.DATA_KEY;

public class MediaPostParserTest {

    private final MediaPostParser parser = new MediaPostParser();

    private JsonNode json;

    @BeforeEach
    public void setUp() {

        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode children = factory.arrayNode();

        JsonNode data = factory.objectNode()
                .put(CHILDREN_KEY, children);

        factory.objectNode()
                .put(DATA_KEY, data);
    }

    @Test
    public void itShouldParseListingObjects() {
        
    }
}