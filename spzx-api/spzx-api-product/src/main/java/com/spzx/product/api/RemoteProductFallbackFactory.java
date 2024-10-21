package com.spzx.product.api;

import com.spzx.common.core.domain.R;
import com.spzx.product.domain.ProductSku;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoteProductFallbackFactory implements FallbackFactory<RemoteProductService>
{

    @Override
    public RemoteProductService create(Throwable throwable) {
//        return new RemoteProductService() {
//            @Override
//            public R<List<ProductSku>> getTopSale(String source) {
//                return R.fail("查询畅销商品出错....");
//            }
//        };
        throw new RuntimeException(throwable.getMessage());
    }
}
