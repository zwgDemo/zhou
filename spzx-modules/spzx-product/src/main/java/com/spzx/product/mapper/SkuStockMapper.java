package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.SkuStock;
import org.apache.ibatis.annotations.Param;

public interface SkuStockMapper extends BaseMapper<SkuStock> {
    //select * from  sku_stock st where st.sku_id=? and st.available_num>?
    SkuStock check(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    //锁定库存
    int lock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    //解锁的方法
    int unlock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    //扣减库存
    int minus(Long skuId, Integer skuNum);
}
