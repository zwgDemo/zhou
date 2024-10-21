package com.spzx.product.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class ExcelCategoryListener<CategoryExcelVo> extends AnalysisEventListener<CategoryExcelVo> {

    //使用分类service
    //构造传递
    private CategoryService categoryService;
    public ExcelCategoryListener(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List<CategoryExcelVo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    @Override
    public void invoke(CategoryExcelVo o, AnalysisContext analysisContext) {
        cachedDataList.add(o);
        if(cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    //匹配添加数据库
    private void saveData() {
        //List<CategoryExcelVo> -- List<Category>
        List<Category> list = cachedDataList.stream().map(categoryExcelVo -> {
            Category category = new Category();
            BeanUtils.copyProperties(categoryExcelVo, category);
            return category;
        }).collect(Collectors.toList());

        categoryService.saveBatch(list);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }
}
