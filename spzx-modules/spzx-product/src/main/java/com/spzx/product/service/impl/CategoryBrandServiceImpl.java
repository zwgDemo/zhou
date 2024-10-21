package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.mapper.CategoryBrandMapper;
import com.spzx.product.service.CategoryBrandService;
import com.spzx.product.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand>
        implements CategoryBrandService {

    //    @Resource
//    private CategoryMapper categoryMapper;

    @Resource
    private CategoryService categoryService;

    //修改
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateCategoryBrand(CategoryBrand categoryBrand) {
        CategoryBrand originalCategoryBrand = this.getById(categoryBrand.getId());
        if(categoryBrand.getCategoryId().longValue() != originalCategoryBrand.getCategoryId().longValue()
                || categoryBrand.getBrandId().longValue() != originalCategoryBrand.getBrandId().longValue()) {
            long count = baseMapper.selectCount(new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getCategoryId, categoryBrand.getCategoryId()).eq(CategoryBrand::getBrandId, categoryBrand.getBrandId()));
            if(count > 0) {
                throw new ServiceException("该分类已加该品牌");
            }
        }
        return baseMapper.updateById(categoryBrand);
    }

    //添加
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertCategoryBrand(CategoryBrand categoryBrand) {
        long count = baseMapper.selectCount(new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getCategoryId, categoryBrand.getCategoryId()).eq(CategoryBrand::getBrandId, categoryBrand.getBrandId()));
        if(count > 0) {
            throw new ServiceException("该分类已加该品牌");
        }
        return baseMapper.insert(categoryBrand);
    }

    //查询分类品牌列表
    @Override
    public List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand) {
        List<CategoryBrand> list = baseMapper.selectCategoryBrandList(categoryBrand);
        return list;
    }

    //分类品牌详情
    @Override
    public CategoryBrand selectCategoryBrandById(Long id) {
        //根据id查询
       //CategoryBrand categoryBrand = this.getById(id);/
        CategoryBrand categoryBrand = baseMapper.selectById(id);
        //根据当前第三层分类id，获取第二层和第一层分类id
        Long categoryId = categoryBrand.getCategoryId();
        List<Long> categoryIdList = categoryService.getAllCategoryById(categoryId);
        categoryBrand.setCategoryIdList(categoryIdList);

        return categoryBrand;
    }

    //根据分类id获取对应品牌数据
    @Override
    public List<Brand> selectBrandListByCategoryId(Long categoryId) {
        return baseMapper.selectBrandListByCategoryId(categoryId);
    }


}
