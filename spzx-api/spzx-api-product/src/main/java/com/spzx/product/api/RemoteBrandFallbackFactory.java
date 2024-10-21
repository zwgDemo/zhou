package com.spzx.product.api;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteBrandFallbackFactory implements FallbackFactory<RemoteBrandService> {
    @Override
    public RemoteBrandService create(Throwable throwable) {
        throw new RuntimeException(throwable.getMessage());
    }
}
