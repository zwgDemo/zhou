package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.ProductSpec;

import java.util.List;

public interface ProductSpecService extends IService<ProductSpec> {
    //分页查询商品规格，数据里面包含商品规格所属分类名称
    List<ProductSpec> selectProductSpecList(ProductSpec productSpec);

    //获取商品规格详细信息
    ProductSpec selectProductSpecById(Long id);

    List<ProductSpec> selectProductSpecListByCategoryId(Long categoryId);
}
