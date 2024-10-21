package com.spzx.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.redis.cache.GuiguCache;
import com.spzx.product.domain.*;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.ProductService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private SkuStockMapper skuStockMapper;
    @Resource
    private ProductDetailsMapper productDetailsMapper;

    //查询商品列表
    @Override
    public List<Product> selectProductList(Product product) {
        List<Product> list = baseMapper.selectProductList(product);
        return list;
    }

    //添加商品
    @Transactional
    @Override
    public int insertProduct(Product product) {
        //0 操作多张表，添加到事务里面
        //1 添加商品基本信息到product表
        baseMapper.insert(product);

        //2 获取前端传递过来商品所有sku列表list集合 productSkuList
        List<ProductSku> productSkuList = product.getProductSkuList();

        //3 把sku列表list集合遍历，得到每个sku对象，把每个sku添加product_sku
        for (int i = 0; i < productSkuList.size(); i++) {
            ProductSku productSku = productSkuList.get(i);
            //设置一些sku其他值
            productSku.setSkuCode(product.getId() + "_" + i);
            productSku.setProductId(product.getId());
            String skuName = product.getName() + " " + productSku.getSkuSpec();
            productSku.setSkuName(skuName);
            //把每个sku添加product_sku表
            productSkuMapper.insert(productSku);

            //4 把每个sku和库存量，添加sku_stock
            SkuStock skuStock = new SkuStock();
            skuStock.setSkuId(productSku.getId());
            skuStock.setTotalNum(productSku.getStockNum());
            skuStock.setLockNum(0);
            skuStock.setAvailableNum(productSku.getStockNum());
            skuStock.setSaleNum(0);
            skuStockMapper.insert(skuStock);
        }

        //5 添加商品product_details
        // 商品图片 image_urls
        // detailsImageUrlList集合 转换字符串
        ProductDetails productDetails = new ProductDetails();

        List<String> detailsImageUrlList = product.getDetailsImageUrlList();
        // [01.jpg,02.jpg]  ==  01.jpg,02.jpg
        productDetails.setProductId(product.getId());

        String detailsImageUrl = String.join(",", detailsImageUrlList);
        productDetails.setImageUrls(detailsImageUrl);

        productDetailsMapper.insert(productDetails);

        return product.getId().intValue();
    }

    //获取商品详细信息
    @Override
    public Product selectProductById(Long id) {
        //1 根据商品id获取商品基本信息，查询product表
        Product product = baseMapper.selectById(id);

        //2 根据商品id获取商品所有sku列表
        LambdaQueryWrapper<ProductSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSku::getProductId,id);
        List<ProductSku> productSkuList = productSkuMapper.selectList(wrapper);

        //2.1 因为每个sku有对应库存量
        //2.2 查询每个sku库存量封装到每个ProductSku对象里面
        //第一种 productSkuList遍历，得到每个sku，根据每个skuid查询库存表得到库存量，设置对象
//        productSkuList.forEach(productSku -> {
//            Long skuId = productSku.getId();
//            //根据skuId查询库存
//            LambdaQueryWrapper<SkuStock> wrapper1 = new LambdaQueryWrapper<>();
//            wrapper1.eq(SkuStock::getSkuId,skuId);
//            SkuStock skuStock = skuStockMapper.selectOne(wrapper1);
//            Integer totalNum = skuStock.getTotalNum();
//
//            productSku.setStockNum(totalNum);
//        });

        //第二种 stream流数据处理
        //1 从productSkuList获取所有id
        //List<ProductSku>
        List<Long> skuIdList =
                productSkuList.stream().map(ProductSku::getId).collect(Collectors.toList());

        //2 拿着所有skuId集合查询库存表，对应库存信息
        LambdaQueryWrapper<SkuStock> wrapperSkuStock = new LambdaQueryWrapper<>();
        wrapperSkuStock.in(SkuStock::getSkuId,skuIdList);
        List<SkuStock> skuStockList = skuStockMapper.selectList(wrapperSkuStock);

        //3 第二步查询sku所有库存信息list ，转换map集合
        // key:skuId  value:库存量
        Map<Long, Integer> map =
                skuStockList.stream()
                        .collect(Collectors.toMap(SkuStock::getSkuId, SkuStock::getTotalNum));

        //4 把productSkuList遍历，根据skuId到第三步map里面找到对应库存，设置到productSku
        productSkuList.forEach(productSku -> {
            //获取skuId
            Long productSkuId = productSku.getId();
            //根据skuid查询map，获取对应库存量
            Integer num = map.get(productSkuId);
            productSku.setStockNum(num);
        });

        //3 把查询商品所有sku列表封装product里面
        product.setProductSkuList(productSkuList);

        //4 根据商品id获取商品详情数据  图片列表
        LambdaQueryWrapper<ProductDetails> wrapperDetails = new LambdaQueryWrapper<>();
        wrapperDetails.eq(ProductDetails::getProductId,id);
        ProductDetails productDetails = productDetailsMapper.selectOne(wrapperDetails);

        String imageUrls = productDetails.getImageUrls();
        // 01.jpg,02.jpg
        //字符串 imageUrls 转换数组
        String[] split = imageUrls.split(",");
        //把数组转换list集合
        List<String> list = Arrays.asList(split);
        //5 把查询商品图片列表封装product里面
        product.setDetailsImageUrlList(list);

        //6 返回封装完成product对象
        return product;
    }

    //修改
    @Transactional
    @Override
    public int updateProduct(Product product) {
        //修改商品信息
        baseMapper.updateById(product);

        List<ProductSku> productSkuList = product.getProductSkuList();

        productSkuList.forEach(productSku -> {
            //修改商品SKU信息
            productSkuMapper.updateById(productSku);

            //修改商品库存
            SkuStock skuStock =
                    skuStockMapper.selectOne(new LambdaQueryWrapper<SkuStock>()
                            .eq(SkuStock::getSkuId, productSku.getId()));
            skuStock.setTotalNum(productSku.getStockNum());

            int availableNum = skuStock.getTotalNum() - skuStock.getLockNum();

            skuStock.setAvailableNum(availableNum);
            skuStockMapper.updateById(skuStock);
        });

        //修改商品详细信息
        ProductDetails productDetails =
                productDetailsMapper.selectOne(new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, product.getId()));
        productDetails.setImageUrls(String.join(",", product.getDetailsImageUrlList()));
        productDetailsMapper.updateById(productDetails);
        return 1;
    }

    //删除
    @Transactional
    @Override
    public int deleteProductByIds(Long[] ids) {
        //1 根据商品id删除product
        baseMapper.deleteBatchIds(Arrays.asList(ids));

        //2 删除skuId，删除sku里面库存信息
        //获取商品所有skuId
        LambdaQueryWrapper<ProductSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductSku::getProductId,ids);
        List<ProductSku> productSkuList = productSkuMapper.selectList(wrapper);

        //productSkuList  获取skuId值
        List<Long> skuIdList =
                productSkuList.stream().map(ProductSku::getId).collect(Collectors.toList());

        //删除sku对应库存 删除条件 skuId
        LambdaQueryWrapper<SkuStock> wrapperSkuStock = new LambdaQueryWrapper<>();
        wrapperSkuStock.in(SkuStock::getSkuId,skuIdList);
        skuStockMapper.delete(wrapperSkuStock);

        //3 根据商品id删除product_sku
        productSkuMapper
                .delete(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getProductId, ids));

        //4 根据商品id删除详情
        productDetailsMapper
                .delete(new LambdaQueryWrapper<ProductDetails>()
                        .in(ProductDetails::getProductId, ids));
        return 1;
    }

    //查询畅销商品
    @Override
    public List<ProductSku> getTopSale() {
        return productSkuMapper.selectTopSale();
    }

    //远程调用使用，商品列表
    @Override
    public List<ProductSku> selectProductSkuList(SkuQuery skuQuery) {
        return productSkuMapper.selectProductSkuList(skuQuery);
    }

    @GuiguCache(prefix = "product:")
    public ProductSku getProductSku(Long skuId) {
        return productSkuMapper.selectById(skuId);
    }



}
