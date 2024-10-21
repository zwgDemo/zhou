package com.spzx.product;

import com.spzx.common.security.annotation.EnableCustomConfig;
import com.spzx.common.security.annotation.EnableRyFeignClients;
import com.spzx.product.domain.ProductSku;
import com.spzx.product.mapper.ProductSkuMapper;
import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class SpzxProductApplication implements CommandLineRunner{

    public static void main(String[] args) {
        SpringApplication.run(SpzxProductApplication.class,args);
    }

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ProductSkuMapper productSkuMapper;

    //商品功能
    //初始化布隆过滤器
    @Override
    public void run(String... args) throws Exception {
        //创建布隆过滤器
        RBloomFilter<Object> bloomFilter =
                redissonClient.getBloomFilter("sku:bloom:filter");
        //设置布隆过滤器 //设置数据规模 误判率
        bloomFilter.tryInit(100000,0.01);

        //查询mysql里面商品skuId
        List<ProductSku> productSkuList = productSkuMapper.selectList(null);
        productSkuList.forEach(item -> {
            bloomFilter.add(item.getId());
        });
    }
}
