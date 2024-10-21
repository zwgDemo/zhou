package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.domain.vo.CategoryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

//@FeignClient指定远程调用接口所在服务，设置降级机制
//第一种
//@FeignClient(contextId = "remoteCategoryService",
//        value = ServiceNameConstants.PRODUCT_SERVICE,fallback = RemoteCategoryServiceImpl.class)

//第二种
@FeignClient(contextId = "remoteCategoryService",
        value = ServiceNameConstants.PRODUCT_SERVICE,
        fallbackFactory = RemoteCategoryFallbackFactory.class)
public interface RemoteCategoryService {

    //远程调用接口
    @GetMapping(value = "/category/getOneCategory")
    public R<List<CategoryVo>> getOneCategory(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    //查询所有分类
    @GetMapping(value = "/category/tree")
    public R<List<CategoryVo> > tree(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
