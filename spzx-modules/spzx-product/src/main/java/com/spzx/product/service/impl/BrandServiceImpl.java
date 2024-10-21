package com.spzx.product.service.impl;

import com.spzx.product.domain.Brand;
import com.spzx.product.mapper.BrandMapper;
import com.spzx.product.service.BrandService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    //分页查询品牌列表
    @Override
    public List<Brand> selectBrandList(Brand brand) {
        List<Brand> list = brandMapper.selectBrandList(brand);
        return list;
    }

    //根据id查询品牌
    @Override
    public Brand getBrand(Long id) {
        Brand brand = brandMapper.getBrand(id);
        return brand;
    }

    //添加品牌
    @Override
    public int addBrand(Brand brand) {
        int rows = brandMapper.addBrand(brand);
        return rows;
    }

    //修改品牌
    @Override
    public int update(Brand brand) {
        return brandMapper.updateBrand(brand);
    }

    //删除
    @Override
    public int deleteBrandByIds(Long[] ids) {
        return brandMapper.deleteBrandByIds(ids);
    }

    @Override
    public List<Brand> selectBrandAll() {
        return brandMapper.selectBrandList(null);
    }
}
