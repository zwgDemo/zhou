package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.service.CategoryBrandService;
import com.spzx.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.catalina.mbeans.BaseCatalinaMBean;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 分类品牌Controller
 */
@Tag(name = "分类品牌接口管理")
@RestController
@RequestMapping("/categoryBrand")
public class CategoryBrandController extends BaseController {

    @Resource
    private CategoryBrandService categoryBrandService;
    /**
     * 查询分类品牌列表
     */
    @Operation(summary = "查询分页分类品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(CategoryBrand categoryBrand) {
        //设置分页参数
        startPage();
        //调用service方法查询所有，传递条件
        List<CategoryBrand> list = categoryBrandService.selectCategoryBrandList(categoryBrand);
        //返回结果
        TableDataInfo dataTable = getDataTable(list);
        return dataTable;
    }

    /**
     * 获取分类品牌详细信息
     */
    @Operation(summary = "获取分类品牌详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(categoryBrandService.selectCategoryBrandById(id));
    }

    //根据分类id获取对应品牌数据
    @Operation(summary = "根据分类id获取品牌列表")
    @GetMapping("brandList/{categoryId}")
    public AjaxResult selectBrandListByCategoryId(@PathVariable Long categoryId) {
        return success(categoryBrandService.selectBrandListByCategoryId(categoryId));
    }

    /**
     * 新增分类品牌
     */
    @Operation(summary = "新增分类品牌")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated CategoryBrand categoryBrand) {
        categoryBrand.setCreateBy(SecurityUtils.getUsername());
        return toAjax(categoryBrandService.insertCategoryBrand(categoryBrand));
    }

    /**
     * 修改分类品牌
     */
    @Operation(summary = "修改分类品牌")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated CategoryBrand categoryBrand) {
        categoryBrand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(categoryBrandService.updateCategoryBrand(categoryBrand));
    }

    /**
     * 删除分类品牌
     */
    @Operation(summary = "删除分类品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(categoryBrandService.removeBatchByIds(Arrays.asList(ids)));
    }
}
