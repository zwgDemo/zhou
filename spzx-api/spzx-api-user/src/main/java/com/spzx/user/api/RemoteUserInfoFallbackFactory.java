package com.spzx.user.api;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteUserInfoFallbackFactory implements FallbackFactory<RemoteUserInfoService> {
    @Override
    public RemoteUserInfoService create(Throwable cause) {
        throw new RuntimeException();
    }
}
