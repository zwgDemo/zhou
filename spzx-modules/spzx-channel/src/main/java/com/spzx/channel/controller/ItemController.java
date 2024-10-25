package com.spzx.channel.controller;

import com.spzx.channel.domain.ItemVo;
import com.spzx.channel.service.IItemService;
import com.spzx.channel.service.IListService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.SkuQuery;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "商品接口")
@RestController
@RequestMapping("/item")
public class ItemController extends BaseController {

    @Resource
    private IItemService iItemService;

    //商品列表的方法
    @GetMapping("{skuId}")
    public AjaxResult item(@PathVariable Long skuId)  {
        ItemVo map = iItemService.item(skuId);// 返回多个对象
        return AjaxResult.success(map);
    }
}
