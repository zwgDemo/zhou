package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.service.ProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "商品规格接口管理")
@RestController
@RequestMapping("/productSpec")
public class ProductSpecController extends BaseController {

    @Resource
    private ProductSpecService productSpecService;

    /**
     * 删除商品规格
     */
    @Operation(summary = "删除商品规格")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(productSpecService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 修改商品规格
     */
    @Operation(summary = "修改商品规格")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ProductSpec productSpec) {
        productSpec.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(productSpecService.updateById(productSpec));
    }

    /**
     * 新增商品规格
     */
    @Operation(summary = "新增商品规格")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ProductSpec productSpec) {
        productSpec.setCreateBy(SecurityUtils.getUsername());
        return toAjax(productSpecService.save(productSpec));
    }

    /**
     * 获取商品规格详细信息
     */
    @Operation(summary = "获取商品规格详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productSpecService.selectProductSpecById(id));
    }


    //分页查询商品规格，数据里面包含商品规格所属分类名称
    @Operation(summary = "查询商品规格列表")
    @GetMapping("/list")
    public TableDataInfo list(ProductSpec productSpec) {
        //设置分页参数
        startPage();
        //查询数据
        List<ProductSpec> list = productSpecService.selectProductSpecList(productSpec);
        TableDataInfo dataTable = getDataTable(list);
        return dataTable;
    }

    @Operation(summary = "根据分类id获取商品规格列表")
    @GetMapping("/productSpecList/{categoryId}")
    public AjaxResult selectProductSpecListByCategoryId(@PathVariable Long categoryId) {
        return success(productSpecService.selectProductSpecListByCategoryId(categoryId));
    }
}
