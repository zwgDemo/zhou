package com.spzx.product.service;

import com.spzx.product.domain.Brand;

import java.util.List;

public interface BrandService {

    //分页查询品牌列表
    List<Brand> selectBrandList(Brand brand);

    //根据id查询品牌
    Brand getBrand(Long id);

    //添加品牌
    int addBrand(Brand brand);

    //修改品牌
    int update(Brand brand);

    //删除
    int deleteBrandByIds(Long[] ids);

    List<Brand> selectBrandAll();
}
