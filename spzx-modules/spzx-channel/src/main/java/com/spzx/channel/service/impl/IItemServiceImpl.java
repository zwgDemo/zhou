package com.spzx.channel.service.impl;

import com.alibaba.fastjson.JSON;
import com.spzx.channel.domain.ItemVo;
import com.spzx.channel.service.IItemService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service
public class IItemServiceImpl implements IItemService {

    @Autowired
    RemoteProductService remoteProductService;

    @Override
    public ItemVo item(Long skuId) {

        // sku
        R<ProductSku> productSkuR = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);// 内部接口header参数
        ProductSku sku = productSkuR.getData();
        // product=spu
        Long productId = sku.getProductId();
        Product product = remoteProductService.getProduct(productId, SecurityConstants.INNER).getData();
        // 库存
        R<SkuStockVo> skuStock = remoteProductService.getSkuStock(skuId, SecurityConstants.INNER);
        SkuStockVo skuStockVo = skuStock.getData();
        // 价格
        SkuPrice skuPrice = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER).getData();
        // 规格
        Map<String, Long> skuSpecValueMap = remoteProductService.getSkuSpecValue(skuId, SecurityConstants.INNER).getData();
        // 轮播图
        String sliderUrls = product.getSliderUrls();
        // 海报
        ProductDetails productDetails = remoteProductService.getProductDetails(productId, SecurityConstants.INNER).getData();

        ItemVo itemVo = new ItemVo();
        itemVo.setProductSku(sku); // sku
        itemVo.setSkuStockVo(skuStockVo); // 库存
        itemVo.setSkuPrice(skuPrice);// price
        itemVo.setProduct(product);// spu
        itemVo.setSkuSpecValueMap(skuSpecValueMap);// 规格:skuId的map
        itemVo.setSliderUrlList(Arrays.asList(sliderUrls.split(",")));// 轮播图
        itemVo.setSpecValueList(JSON.parseArray(product.getSpecValue()));// 规格
        itemVo.setDetailsimagesUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));// 海报
        return itemVo;
    }
}
