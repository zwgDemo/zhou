package com.spzx.product.mapper;

import com.spzx.product.domain.Brand;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface BrandMapper {

    //分页查询品牌列表
    List<Brand> selectBrandList(Brand brand);

    //根据id查询品牌
    Brand getBrand(Long id);

    //添加品牌
    int addBrand(Brand brand);

    //修改品牌
    int updateBrand(Brand brand);

    //删除
    int deleteBrandByIds(@Param("ids") Long[] ids);
}
