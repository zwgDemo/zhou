package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.convert.StringToBaseEnumConvertFactory;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Resource
    private StringToBaseEnumConvertFactory stringToBaseEnumConvertFactory;
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(stringToBaseEnumConvertFactory);
    }
}
