package com.amoalla.redditube.gateway.dto.dex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PasswordVerification {
    private final boolean verified;
    private final boolean notFound;
}
