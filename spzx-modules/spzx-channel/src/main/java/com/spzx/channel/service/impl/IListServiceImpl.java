package com.spzx.channel.service.impl;

import com.spzx.channel.service.IListService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.domain.SkuQuery;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class IListServiceImpl implements IListService {

    @Resource
    private RemoteProductService remoteProductService;

    @Override
    public TableDataInfo skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery) {
        R<TableDataInfo> tableDataInfoResult = remoteProductService.skuList(pageNum, pageSize,
                                                           skuQuery, SecurityConstants.INNER);
        TableDataInfo data = tableDataInfoResult.getData();
        return data;
    }
}
