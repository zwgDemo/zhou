package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.constant.HttpStatus;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.service.ProductUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag(name = "商品单位接口管理")
@RestController
@RequestMapping("/productUnit")
public class ProductUnitController extends BaseController {

    @Resource
    private ProductUnitService productUnitService;

    @Operation(summary = "获取全部单位")
    @GetMapping("getUnitAll")
    public AjaxResult selectProductUnitAll() {
        return success(productUnitService.list());
    }

    //删除  1,2,3
    @Operation(summary = "删除商品单位")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        //批量删除
        List<Long> list = Arrays.asList(ids);
        productUnitService.removeBatchByIds(list);
        return toAjax(1);
    }

    //修改
    @Operation(summary = "修改")
    @PutMapping("/update")
    public AjaxResult update(@RequestBody ProductUnit productUnit) {
        boolean update = productUnitService.updateById(productUnit);
        int rows = 0;
        if(update) {
            rows = 1;
        }
        return toAjax(rows);
    }

    //添加
    @Operation(summary = "添加")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ProductUnit productUnit) {
        boolean save = productUnitService.save(productUnit);
        int rows = 0;
        if(save) {
            rows = 1;
        }
        return toAjax(rows);
    }

    //根据id查询
    @Operation(summary = "根据id查询")
    @GetMapping("/get/{id}")
    public AjaxResult get(@PathVariable Long id) {
        ProductUnit productUnit = productUnitService.getById(id);
        return success(productUnit);
    }

    //分页条件查询接口
    //需要三个参数
    // 当前页 、 每页显示记录数、条件值 单位名称
//    @Operation(summary = "分页条件查询接口")
//    @GetMapping("/list/{pageNum}/{pageSize}")
//    public TableDataInfo list(ProductUnit productUnit) {
//
//    }

    @Operation(summary = "分页条件查询接口")
    @GetMapping("/list")
    public TableDataInfo list(
            @Parameter(name = "pageNum", description = "当前页码", required = true)
            @RequestParam(value = "pageNum",defaultValue = "0",required = true) Integer pageNum,

            @Parameter(name = "pageSize", description = "每页记录数", required = true)
            @RequestParam(value = "pageSize",defaultValue = "10",required = true) Integer pageSize,

            @Parameter(name = "productUnit", description = "条件", required = false)
            ProductUnit productUnit) {

        //1 创建page对象，传递当前页 、 每页显示记录数
        Page<ProductUnit> pageParam = new Page<>(pageNum,pageSize);

        //2 封装查询条件
        //LambdaQueryWrapper
        LambdaQueryWrapper<ProductUnit> wrapper = new LambdaQueryWrapper<>();
        //条件非空判断
        if(StringUtils.hasText(productUnit.getName())) {
            wrapper.eq(ProductUnit::getName,productUnit.getName());
        }

        //3 调用service的方法实现条件分页查询
        //E page(E page, Wrapper<T> queryWrapper)
        IPage<ProductUnit> pageModel = productUnitService.page(pageParam, wrapper);

        //TableDataInfo
//        TableDataInfo rspData = new TableDataInfo();
//        rspData.setCode(HttpStatus.SUCCESS);
//        rspData.setRows(pageModel.getRecords());
//        rspData.setMsg("查询成功");
//        rspData.setTotal(pageModel.getTotal());

        return getDataTable(pageModel);
    }

}
