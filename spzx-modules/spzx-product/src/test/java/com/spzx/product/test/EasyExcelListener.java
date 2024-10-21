package com.spzx.product.test;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


public class EasyExcelListener<T> extends AnalysisEventListener<T> {

    private List<T> datas = new ArrayList<>();
    /*
     * 1 EasyExcel加载方式：一行一行加载
     * 2 每加载一行数据，这个方法就会执行
     * 3 从Excel表格里面第二行开始加载
     */
    @Override
    public void invoke(T o, AnalysisContext analysisContext) {
        datas.add(o);
    }

    public List<T> getDatas() {
        return datas;
    }
    //最后执行的
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
