package com.spzx.common.redis.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Aspect
@Component
public class GuiguCacheAspect {
    
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    //@GuiguCache(prefix = "product:")
    // ProceedingJoinPoint可以操作增强的方法，比如执行业务方法，比如获取业务方法参数等等
    @Around("@annotation(guiguCache)")
    public Object guiGuCacheAdvice(ProceedingJoinPoint joinPoint,GuiguCache guiguCache) {

        //1 根据注解前缀 + 业务方法参数值构建数据在redis的key名字
        //获取注解前缀
        String prefix = guiguCache.prefix();

        //获取业务方法里面参数列表
        Object[] args = joinPoint.getArgs();
        //创建变量 参数
        String paramValue = "data";
        // 把业务方法里面多个参数值获取到 之间  ： 隔开
        if (args != null && args.length > 0) {
            //args数组转换list集合
            List<Object> list = Arrays.asList(args);
            //使用stream操作list集合
            paramValue = list.stream()
                    .map(arg -> arg.toString())
                    .collect(Collectors.joining(":"));
        }
        //构建key
        String dateKey = prefix+paramValue;

        //2 根据第一步构建key的名字查询redis
        Object resultObject = redisTemplate.opsForValue().get(dateKey);

        //3 如果redis查询到数据，直接返回
        if(resultObject != null) {
            return resultObject;
        }

        //4 如果redis查询不到数据，查询mysql
        //4.1 添加分布式锁
        String lockKey = dateKey+":lock";
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean tryLock = rLock.tryLock(3, 10, TimeUnit.SECONDS);

            if(tryLock) {
                //执行业务方法，查询mysql
                resultObject = joinPoint.proceed();

                //4.2 查询mysql，执行业务方法,把查询数据放到redis里面
                redisTemplate.opsForValue().set(dateKey,resultObject,10,TimeUnit.MINUTES);

                return resultObject;
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            //4.3 释放锁
            rLock.unlock();
        }
        return null;
    }
}
