package com.spzx.common.redis.cache;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GuiguCache {

    //注解值，redis缓存key的前缀
    String prefix() default "data:";
}
