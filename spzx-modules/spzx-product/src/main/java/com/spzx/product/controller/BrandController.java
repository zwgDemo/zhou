package com.spzx.product.controller;

import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Brand;
import com.spzx.product.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "品牌接口管理")
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {

    @Resource
    private BrandService brandService;

    @Log(title = "获取全部")
    @Operation(summary = "获取全部品牌")
    @GetMapping("getBrandAll")
    public AjaxResult getBrandAll() {
        return success(brandService.selectBrandAll());
    }

    // [1,2,3]
//    @Operation(summary = "删除")
//    @DeleteMapping("remove")
//    public AjaxResult remove(@RequestBody List<Long> ids) {
//    }

    // 1,2,3
    @RequiresPermissions("product:brand:delete")
    @Operation(summary = "删除")
    @DeleteMapping("remove/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(brandService.deleteBrandByIds(ids));
    }

    //修改品牌
    @Operation(summary = "修改品牌")
    @PutMapping("update")
    public AjaxResult update(@RequestBody Brand brand) {
        brand.setUpdateBy(SecurityUtils.getUsername());
        int rows = brandService.update(brand);
        return toAjax(rows);
    }

    //添加品牌
    @RequiresPermissions("product:brand:add")
    @Operation(summary = "添加品牌")
    @PostMapping("add")
    public AjaxResult add(@RequestBody @Validated Brand brand) {

        String name = brand.getName();
        if(!StringUtils.hasText(name)) {
            return AjaxResult.error();
        }

        //设置当前操作用户（登录用户）
        brand.setCreateBy(SecurityUtils.getUsername());
        //返回影响行数
        int rows = brandService.addBrand(brand);
        //return rows > 0 ? AjaxResult.success() : AjaxResult.error();
        return toAjax(rows);
    }

    //根据id查询品牌
    // getBrand/1
    @Operation(summary = "根据id查询品牌")
    @GetMapping("getBrand/{id}")
    public AjaxResult getBrand(@PathVariable Long id) {
        Brand brand = brandService.getBrand(id);
        return success(brand);
    }

    //分页查询品牌列表
    //若依框架，针对分页查询结果，封装对象TableDataInfo
    @Log(title = "品牌列表",businessType = BusinessType.OTHER)
    @Operation(summary = "查询品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(Brand brand) {
        //PageHelper   当前页2  每页2
        //1 查询所有数据  10
        //2 根据设置分页参数，返回对应数据
        //设置分页参数
        startPage();
        //调用service方法实现查询
        List<Brand> list = brandService.selectBrandList(brand);
        //返回数据封装
        TableDataInfo tableDataInfo = getDataTable(list);
        return tableDataInfo;
    }

    //远程调用使用的，查询所有品牌
    @Operation(summary = "获取全部品牌")
    @GetMapping("getBrandAllList")
    public R<List<Brand>> getBrandAllList() {
        return R.ok(brandService.selectBrandAll());
    }
}
