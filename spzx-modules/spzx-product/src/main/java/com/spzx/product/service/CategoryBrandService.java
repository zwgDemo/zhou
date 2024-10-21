package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.CategoryBrand;

import java.util.List;

public interface CategoryBrandService extends IService<CategoryBrand> {

    //查询分类品牌列表
    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    CategoryBrand selectCategoryBrandById(Long id);

    //根据分类id获取对应品牌数据
    List<Brand> selectBrandListByCategoryId(Long categoryId);

    //添加
    int insertCategoryBrand(CategoryBrand categoryBrand);

    /**
     * 修改分类品牌
     */
    int updateCategoryBrand(CategoryBrand categoryBrand);
}
