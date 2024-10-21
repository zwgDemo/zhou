package com.spzx.channel.controller;

import com.spzx.channel.service.IListService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.SkuQuery;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "商品接口")
@RestController
@RequestMapping("/list")
public class ListController extends BaseController {

    @Resource
    private IListService iListService;

    //商品列表的方法
    @GetMapping("/skuList/{pageNum}/{pageSize}")
    public TableDataInfo skuList(@PathVariable Integer pageNum,
                                 @PathVariable Integer pageSize,
                                 SkuQuery skuQuery) {
        TableDataInfo tableDataInfo = iListService.skuList(pageNum,pageSize,skuQuery);
        return tableDataInfo;
    }
}
