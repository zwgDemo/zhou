package com.spzx.channel.service.impl;

import com.spzx.channel.service.IBrandService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteBrandService;
import com.spzx.product.domain.Brand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IBrandServiceImpl implements IBrandService {
    @Resource
    private RemoteBrandService remoteBrandService;

    @Override
    public List<Brand> getBrandAll() {
        R<List<Brand>> brandListResult = remoteBrandService.getBrandAllList(SecurityConstants.INNER);
        if (R.FAIL == brandListResult.getCode()) {
            throw new ServiceException(brandListResult.getMsg());
        }
        return brandListResult.getData();
    }
}
