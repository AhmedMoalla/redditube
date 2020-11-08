package com.amoalla.redditube.gateway;

import com.amoalla.redditube.gateway.configuration.properties.DexProperties;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import io.grpc.netty.shaded.io.netty.handler.codec.protobuf.ProtobufDecoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Base64;

@SpringBootApplication
@EnableConfigurationProperties(DexProperties.class)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    CommandLineRunner commandLineRunner() {
        return args -> {
            String sub = "CiRkMGI5ZjBiYi0wMGVhLTRlMmItODg3Ny1mMWYyMTk2NzZiN2ESBWxvY2Fs";
            byte[] data = Base64.getDecoder().decode(sub);

        };
    }
}
