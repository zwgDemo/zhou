package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.vo.CategoryVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> listTree(Long id);

    //根据当前第三层分类id，获取第二层和第一层分类id
    List<Long> getAllCategoryById(Long categoryId);

    //导出  就是下载过程
    void export(HttpServletResponse response);

    //导入  就是上传过程
    void importData(MultipartFile file);

    //查询所有一级分类
    List<CategoryVo> selectOneCategory();

    //查询所有分类，树形结构
    List<CategoryVo> tree();
}
