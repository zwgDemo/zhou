package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.ProductSpec;
import org.apache.catalina.mbeans.BaseCatalinaMBean;

import java.util.List;

public interface ProductSpecMapper extends BaseMapper<ProductSpec> {

    //分页查询商品规格，数据里面包含商品规格所属分类名称
    List<ProductSpec> selectProductSpecList(ProductSpec productSpec);
}
