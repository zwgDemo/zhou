package com.spzx.channel.service.impl;

import com.spzx.channel.service.CategoryService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.domain.vo.CategoryVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryImpl implements CategoryService {

    @Resource
    private RemoteCategoryService remoteCategoryService;

    @Override
    public List<CategoryVo> tree() {

        R<List<CategoryVo>> treeResult = remoteCategoryService.tree(SecurityConstants.INNER);
        List<CategoryVo> categoryVoList = treeResult.getData();

        return categoryVoList;
    }
}
