package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.mapper.ProductSpecMapper;
import com.spzx.product.service.CategoryService;
import com.spzx.product.service.ProductSpecService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements ProductSpecService {

    @Resource
    private CategoryService categoryService;
    //分页查询商品规格，数据里面包含商品规格所属分类名称
    @Override
    public List<ProductSpec> selectProductSpecList(ProductSpec productSpec) {
        List<ProductSpec> list = baseMapper.selectProductSpecList(productSpec);
        return list;
    }

    //获取商品规格详细信息
    @Override
    public ProductSpec selectProductSpecById(Long id) {
        //根据商品规格id查询信息
        ProductSpec productSpec = baseMapper.selectById(id);

        //商品规格所属所有层分类的id查询出来
        Long categoryId = productSpec.getCategoryId();//第三层分类id
        //根据第三层分类id，找到第二层 找到第一层
        List<Long> allCategoryById = categoryService.getAllCategoryById(categoryId);
        productSpec.setCategoryIdList(allCategoryById);

        return productSpec;
    }

    @Override
    public List<ProductSpec> selectProductSpecListByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ProductSpec> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSpec::getCategoryId,categoryId);
        List<ProductSpec> list = baseMapper.selectList(wrapper);
        return list;
    }
}
