package com.amoalla.redditube.commons.grpc;

import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.api.service.ExplorerServices;
import com.amoalla.redditube.commons.grpc.impl.ExplorerServiceGrpcClientImpl;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    @ConditionalOnMissingBean(ExplorerService.class)
    ExplorerServices provideExplorerServiceClient(@Value("${redditube.services.explorer-users.host}") String usersHost,
                                                  @Value("${redditube.services.explorer-users.grpc-port}") int usersGrpcPort,
                                                  @Value("${redditube.services.explorer-subreddits.host}") String subredditsHost,
                                                  @Value("${redditube.services.explorer-subreddits.grpc-port}") int subredditsGrpcPort) {

        final var usersService = new ExplorerServiceGrpcClientImpl(usersHost, usersGrpcPort);
        final var subredditsService = new ExplorerServiceGrpcClientImpl(subredditsHost, subredditsGrpcPort);
        class DisposableExplorerServices implements DisposableBean, ExplorerServices {

            @Override
            public ExplorerService users() {
                return usersService;
            }

            @Override
            public ExplorerService subreddits() {
                return subredditsService;
            }

            @Override
            public void destroy() {
                usersService.destroy();
                subredditsService.destroy();
            }
        }
        return new DisposableExplorerServices();
    }
}
