package com.spzx.product.utils;

import com.spzx.product.domain.vo.CategoryVo;

import java.util.ArrayList;
import java.util.List;

public class CategoryUtils {

    //所有分类封装树形结构
    //使用递归
    public static List<CategoryVo> buildTree(List<CategoryVo> categoryVoList) {
        //递归
        //首先找到第一层数据，从第一层继续往下找，直到找不到为止
        //找下一层时候，条件： id 和 parent_id关系
        //1 创建list集合，用于封装最终数据
        List<CategoryVo> trees = new ArrayList<>();
        //2 从所有分类categoryVoList集合，找到第一层分类数据 parent_id=0
        for (CategoryVo categoryVo:categoryVoList) {
            //第一层数据 parent_id=0
            // Long long
            if(categoryVo.getParentId().longValue()==0) {
               // categoryVo 第一层数据
                //findChildren方法是递归查找的方法
                // 目前方法里面 categoryVo代表第一层分类数据
                //             categoryVoList 所有分类数据
                //根据第一层，到categoryVoList所有分类集合找到第二层，递归方式继续往下找
                trees.add(findChildren(categoryVo,categoryVoList));
            }
        }
        return trees;
    }


    // 目前方法里面 categoryVo代表第一层分类数据
    //             categoryVoList 所有分类数据
    //根据第一层，到categoryVoList所有分类集合找到第二层，递归方式继续往下找
    public static CategoryVo findChildren(CategoryVo categoryVo,
                                          List<CategoryVo> categoryVoList) {

        categoryVo.setChildren(new ArrayList<CategoryVo>());

        //遍历所有分类集合 categoryVoList
        for(CategoryVo it : categoryVoList) {
            //判断
            //一级分类id  和 所有分类parent_id相同，就是下面二级分类
            if(categoryVo.getId().longValue() == it.getParentId().longValue() ) {
                if (categoryVo.getChildren() == null) {
                    categoryVo.setChildren(new ArrayList<>());
                }
                //it就是一级下面二级分类
                categoryVo.getChildren().add(findChildren(it,categoryVoList));
            }
        }
        return categoryVo;
    }
}
