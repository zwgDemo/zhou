package com.spzx.channel.service;

import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.SkuQuery;

public interface IListService {
    TableDataInfo skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery);
}
