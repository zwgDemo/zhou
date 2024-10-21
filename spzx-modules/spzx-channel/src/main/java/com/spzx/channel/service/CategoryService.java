package com.spzx.channel.service;

import com.spzx.product.domain.vo.CategoryVo;

import java.util.List;

public interface CategoryService {
    List<CategoryVo> tree();
}
