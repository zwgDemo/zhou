package com.spzx.channel.service.impl;

import com.spzx.channel.service.IIndexService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.domain.ProductSku;
import com.spzx.product.domain.vo.CategoryVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IIndexServiceImpl implements IIndexService {

    @Resource
    private RemoteCategoryService remoteCategoryService;

    @Resource
    private RemoteProductService remoteProductService;

    //首页数据
    @Override
    public Map<String, Object> getIndexData() {

        //获取所有一级分类
        R<List<CategoryVo>> oneCategoryResult = remoteCategoryService.getOneCategory(SecurityConstants.INNER);
        List<CategoryVo> categoryVoList = oneCategoryResult.getData();

        //获取畅销商品
        R<List<ProductSku>> topSaleResult = remoteProductService.getTopSale(SecurityConstants.INNER);
        List<ProductSku> productSkuList = topSaleResult.getData();

        //封装map
        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", categoryVoList);
        map.put("productSkuList", productSkuList);

        return map;
    }
}
