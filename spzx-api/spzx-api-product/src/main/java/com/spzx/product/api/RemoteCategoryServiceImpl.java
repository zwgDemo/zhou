package com.spzx.product.api;

import com.spzx.common.core.domain.R;
import com.spzx.product.domain.vo.CategoryVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemoteCategoryServiceImpl implements RemoteCategoryService{
    @Override
    public R<List<CategoryVo>> getOneCategory(String source) {
        //降级方法
        return R.fail("系统繁忙..");
    }

    @Override
    public R<List<CategoryVo>> tree(String source) {
        return null;
    }

}
