package com.amoalla.redditube.commons.grpc;

import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.api.service.ExplorerServices;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GrpcClientConfigurationTest {

    @Test
    void testExplorerServiceClientPresentInContext() {
        new ApplicationContextRunner()
                .withPropertyValues(
                        "redditube.services.explorer-users.host=localhost",
                        "redditube.services.explorer-users.grpc-port=1",
                        "redditube.services.explorer-subreddits.host=localhost",
                        "redditube.services.explorer-subreddits.grpc-port=2")
                .withConfiguration(AutoConfigurations.of(GrpcClientConfiguration.class))
                .withBean(ModelMapper.class, ModelMapper::new)
                .run(context -> {
                    assertThat(context).hasSingleBean(ExplorerServices.class);
                    ExplorerServices explorerServices = context.getBean(ExplorerServices.class);
                    ExplorerService users = explorerServices.users();
                    String hostAndPort = extractHostAndPortOfService(users);
                    assertEquals("localhost:1", hostAndPort);
                    ExplorerService subreddits = explorerServices.subreddits();
                    hostAndPort = extractHostAndPortOfService(subreddits);
                    assertEquals("localhost:2", hostAndPort);
                });
    }

    private String extractHostAndPortOfService(ExplorerService explorerService) {
        var channel = ReflectionTestUtils.getField(explorerService, "channel");
        var delegate = ReflectionTestUtils.getField(channel, "delegate");
        return (String) ReflectionTestUtils.getField(delegate, "target");
    }
}