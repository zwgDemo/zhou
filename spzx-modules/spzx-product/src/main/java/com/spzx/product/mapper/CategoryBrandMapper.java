package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.CategoryBrand;

import java.util.List;

public interface CategoryBrandMapper extends BaseMapper<CategoryBrand> {
    //查询分类品牌列表
    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    //根据分类id获取对应品牌数据
    List<Brand> selectBrandListByCategoryId(Long categoryId);
}
