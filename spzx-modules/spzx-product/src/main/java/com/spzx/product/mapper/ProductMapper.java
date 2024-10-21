package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.Product;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {

    //查询商品列表
    List<Product> selectProductList(Product product);
}
