package com.spzx.product.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.domain.vo.CategoryVo;
import com.spzx.product.listener.ExcelCategoryListener;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.CategoryService;
import com.spzx.product.utils.CategoryUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

//    @Resource
//    private CategoryMapper categoryMapper;

    //select id,parent_id,name from category  where parent_id=1
    @Override
    public List<Category> listTree(Long id) {
        //调用mapper
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId,id);
        List<Category> list = baseMapper.selectList(wrapper);
//        List<Category> list = this.list(wrapper);
        //判断每个分类下面是否有下一层分类，如果有 hasChildren==true，否则hasChildren==false
        //遍历查询list集合，得到每个分类，查询每个分类是否有下一层分类，如果有hasChildren==true
        list.stream().forEach(category->{
            //查询每个分类是否有下一层分类
            //select id,parent_id,name from category  where parent_id=1
            LambdaQueryWrapper<Category> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(Category::getParentId, category.getId());
            Long count = baseMapper.selectCount(wrapper1);
            if(count >0) {
                category.setHasChildren(true);
            }else {
                category.setHasChildren(false);
            }
        });
        return list;
    }

    //根据当前第三层分类id，获取第二层和第一层分类id
    @Override
    public List<Long> getAllCategoryById(Long categoryId) {
        List<Long> list = new ArrayList<>();
        // 3 2 1  ==  1 2 3
        List<Category> categoryList = this.getParentCategory(categoryId, new ArrayList<Category>());
        int size = categoryList.size();
        for (int i = size; i > 0; i--) {
            Category category = categoryList.get(i - 1);
            Long id = category.getId();
            list.add(id);
        }
        return list;
    }

    //导出  就是下载过程
    @Override
    public void export(HttpServletResponse response) {
        try {
        //1 设置下载响应头信息 Content-disposition
        // 设置响应结果类型  mime类型
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");

        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("分类数据", "UTF-8");

        //下载过程
        //如果实现下载，首先基于response对象，设置响应头信息
        // 响应头Content-disposition，文件以下载方式打开
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        //2 调用方法查询数据库获取所有分类list集合
        List<Category> categoryList = baseMapper.selectList(null);

        // List<Category> -- List<CategoryExcelVo>
        List<CategoryExcelVo> listVo = categoryList.stream().map(category -> {
            CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
            BeanUtils.copyProperties(category, categoryExcelVo);
            return categoryExcelVo;
        }).collect(Collectors.toList());

        //3 调用EasyExcel方法实现写操作，把数据库表数据写到Excel表格里面
        EasyExcel.write(response.getOutputStream(),CategoryExcelVo.class)
                .sheet("data")
                .doWrite(listVo);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //导入  就是上传过程
    @Override
    public void importData(MultipartFile file) {
        try {
            //读取excel里面数据
            List<CategoryExcelVo> categoryExcelVoList =
                    EasyExcel.read(file.getInputStream())
                            .head(CategoryExcelVo.class)
                            .sheet()
                            .doReadSync();

            //读取到list集合，把list集合添加数据表
            //List<CategoryExcelVo> -- List<Category>
            if(!CollectionUtils.isEmpty(categoryExcelVoList)) {
                List<Category> categoryList = new ArrayList<>(categoryExcelVoList.size());
                for(CategoryExcelVo categoryExcelVo : categoryExcelVoList) {
                    Category category = new Category();
                    BeanUtils.copyProperties(categoryExcelVo, category, Category.class);
                    categoryList.add(category);
                }
                this.saveBatch(categoryList);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //查询所有一级分类
    @Override
    public List<CategoryVo> selectOneCategory() {
        //SELECT * FROM category WHERE parent_id=0
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId,0);
        List<Category> list = baseMapper.selectList(wrapper);
        //List<Category> -- List<CategoryVo>
        List<CategoryVo> categoryVoList = list.stream().map(category -> {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category, categoryVo);
            return categoryVo;
        }).collect(Collectors.toList());

        return categoryVoList;
    }

    //查询所有分类，树形结构
    @Override
    public List<CategoryVo> tree() {
        //1 查询所有分类
        List<Category> categoryList = baseMapper.selectList(null);

        //  List<Category> -- List<CategoryVo>
        List<CategoryVo> categoryVoList = categoryList.stream().map(category -> {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category, categoryVo);
            return categoryVo;
        }).collect(Collectors.toList());

        //所有分类封装树形结构
        return CategoryUtils.buildTree(categoryVoList);
    }

    //带监听器读写法
    public void importData1(MultipartFile file) {
        try {
           EasyExcel.read(file.getInputStream())
                   .head(CategoryExcelVo.class)
                   .sheet()
                   .registerReadListener(new ExcelCategoryListener(this))
                   .doRead();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //  1 2 3
    //当前 3
    private List<Category> getParentCategory(Long categoryId, ArrayList<Category> categoryList) {
        //递归
        while(categoryId > 0) {
            //获取当前分类id对应分类数据
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getId,categoryId);
            // select id,parentid from ...
            wrapper.select(Category::getId, Category::getParentId);

            Category category = baseMapper.selectOne(wrapper);

            categoryList.add(category);

            return getParentCategory(category.getParentId(),categoryList);
        }
        return categoryList;
    }

}
