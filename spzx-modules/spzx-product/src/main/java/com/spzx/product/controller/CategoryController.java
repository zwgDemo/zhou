package com.spzx.product.controller;

import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.domain.vo.CategoryVo;
import com.spzx.product.listener.ExcelCategoryListener;
import com.spzx.product.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Synchronized;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Tag(name = "商品分类接口管理")
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {

    @Resource
    private CategoryService categoryService;

    //查询所有一级分类
    @InnerAuth
    @GetMapping(value = "/getOneCategory")
    public R<List<CategoryVo> > getOneCategory() {
        List<CategoryVo>  list = categoryService.selectOneCategory();
        return R.ok(list);
    }

    //每次查询一层分类数据 懒加载
    //select id,parent_id,name from category  where parent_id=1
    @GetMapping("/list/{id}")
    public AjaxResult list(@PathVariable Long id) {
        List<Category> list = categoryService.listTree(id);
        return success(list);
    }

    // 上传  下载
    //导出  就是下载过程
    @PostMapping("/export")
    public void export(HttpServletResponse response) {
        categoryService.export(response);
    }

    //使用MultipartFile获取上传文件
    //特别注意：参数名称file编写有规则，和name值一致
    //<input type="file" name="file"/>
    //element-plus上传组件name值就是file
    //导入  就是上传过程
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file) {
        try {
            categoryService.importData(file);
            return AjaxResult.success("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.error("导入失败");
    }

    @PostMapping("/import1")
    public AjaxResult importData1(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream())
                    .head(CategoryExcelVo.class)
                    .sheet()
                    .registerReadListener(new ExcelCategoryListener(categoryService))
                    .doRead();

            return AjaxResult.success("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.error("导入失败");
    }

    //查询所有分类，封装树形结构
    //@InnerAuth
    @GetMapping(value = "/tree")
    public R<List<CategoryVo>> tree() {
        return R.ok(categoryService.tree());
    }
}
