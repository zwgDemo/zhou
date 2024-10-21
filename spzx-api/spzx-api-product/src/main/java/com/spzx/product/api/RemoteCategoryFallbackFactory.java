package com.spzx.product.api;

import com.spzx.common.core.domain.R;
import com.spzx.product.domain.vo.CategoryVo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoteCategoryFallbackFactory implements FallbackFactory<RemoteCategoryService> {

    @Override
    public RemoteCategoryService create(Throwable throwable) {
//        return new RemoteCategoryService() {
//            @Override
//            public R<List<CategoryVo>> getOneCategory(String source) {
//                return R.fail("获取全部一级分类失败:" + throwable.getMessage());
//            }
//        };
        throw new RuntimeException(throwable.getMessage());
    }
}
