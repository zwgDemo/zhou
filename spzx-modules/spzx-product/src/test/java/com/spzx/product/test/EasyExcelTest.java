package com.spzx.product.test;

import com.alibaba.excel.EasyExcel;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class EasyExcelTest {

    @Resource
    private CategoryService categoryService;

    @Resource
    private EasyExcelListener easyExcelListener;

    //读操作
    @Test
    public void readExcelData() {
        String fileName = "C:\\分类数据0311.xlsx" ;
        //创建监听器对象
        EasyExcelListener easyExcelListener = new EasyExcelListener();
        //调用方法
        EasyExcel.read(fileName)
                .head(CategoryExcelVo.class)
                .sheet()
                .registerReadListener(easyExcelListener)
                .doRead();

        List list = easyExcelListener.getDatas();
        //添加数据库，批量添加

        easyExcelListener.getDatas().forEach(s -> System.out.println(s) );
        //EasyExcel.read(fileName,CategoryExcelVo.class,easyExcelListener).sheet().doRead();
    }

    //写操作
    // 查询所有分类，把查询出来所有分类list集合，写入到excel表格里面
    @Test
    public void wirteExcelData() {
        // 查询所有分类
        List<Category> list = categoryService.list();

        //List<Category> -- List<CategoryExcelVo>
        //        List<CategoryExcelVo> listVo = new ArrayList<>();
        //        list.forEach(category -> {
        //            CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
        //            // 把category值 复制到 categoryExcelVo
        //            //categoryExcelVo.setName(category.getName());
        //            BeanUtils.copyProperties(category,categoryExcelVo);
        //            listVo.add(categoryExcelVo);
        //        });

        List<CategoryExcelVo> listVo = list.stream().map(category -> {
            CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
            BeanUtils.copyProperties(category, categoryExcelVo);
            return categoryExcelVo;
        }).collect(Collectors.toList());

        //list集合，写入到excel表格里面
        EasyExcel.write("C:\\分类数据0311.xlsx", CategoryExcelVo.class)
                .sheet("data")
                .doWrite(listVo);
    }
}
