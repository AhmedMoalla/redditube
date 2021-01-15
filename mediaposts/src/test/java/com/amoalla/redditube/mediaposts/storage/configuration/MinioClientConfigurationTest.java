package com.amoalla.redditube.mediaposts.storage.configuration;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MinioClientConfigurationTest {

    @Test
    void testProvidesMinioClient() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MinioClientConfiguration.class))
                .withPropertyValues(
                        "redditube.minio.endpoint=ENDPOINT",
                        "redditube.minio.accessKey=ACCESS_KEY",
                        "redditube.minio.secretKey=SECRET_KEY"
                )
                .run(context -> assertThat(context).hasSingleBean(MinioClient.class));
    }
}