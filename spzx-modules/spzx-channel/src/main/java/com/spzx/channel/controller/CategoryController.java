package com.spzx.channel.controller;

import com.spzx.channel.service.CategoryService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.product.domain.vo.CategoryVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {

    @Resource
    private CategoryService categoryService;

    @GetMapping(value = "/tree")
    public AjaxResult tree() {
        List<CategoryVo> list = categoryService.tree();
        return success(list);
    }
}
