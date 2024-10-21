package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.mapper.ProductUnitMapper;
import com.spzx.product.service.ProductUnitService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit>
        implements ProductUnitService {

}
