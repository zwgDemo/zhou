package com.spzx.product.lock;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.spzx.common.core.utils.uuid.UUID;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TestServiceImpl implements TestService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //////////////可重入 和 续期 ////////////////////////

    //解锁方法
    private void unlock(String lockName, String uuid){
        String script = "if (redis.call('hexists', KEYS[1], ARGV[1]) == 0) then" +
                "    return nil;" +
                "end;" +
                "if (redis.call('hincrby', KEYS[1], ARGV[1], -1) > 0) then" +
                "    return 0;" +
                "else" +
                "    redis.call('del', KEYS[1]);" +
                "    return 1;" +
                "end;";
        // 这里之所以没有跟加锁一样使用 Boolean ,这是因为解锁 lua 脚本中，三个返回值含义如下：
        // 1 代表解锁成功，锁被释放
        // 0 代表可重入次数被减 1
        // null 代表其他线程尝试解锁，解锁失败
        Long result = this.stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Lists.newArrayList(lockName), uuid);
        // 如果未返回值，代表尝试解其他线程的锁
        if (result == null) {
            throw new IllegalMonitorStateException("attempt to unlock lock, not locked by lockName: "
                    + lockName + " with request: "  + uuid);
        }
    }

    //加锁方法
    private Boolean tryLock(String lockName, String uuid, Long expire){
        String script = "if (redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1) " +
                "then" +
                "    redis.call('hincrby', KEYS[1], ARGV[1], 1);" +
                "    redis.call('expire', KEYS[1], ARGV[2]);" +
                "    return 1;" +
                "else" +
                "   return 0;" +
                "end";
        if (!this.stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class),
                Arrays.asList(lockName), uuid, expire.toString())){
            try {
                // 没有获取到锁，重试
                Thread.sleep(200);
                tryLock(lockName, uuid, expire);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 锁续期
        this.renewTime(lockName, uuid, expire * 1000);
        // 获取到锁，返回true
        return true;
    }

    //续期的方法
    private void renewTime(String lockName, String uuid, Long expire){
        String script= "if(redis.call('hexists', KEYS[1], ARGV[1]) == 1) then redis.call('expire', KEYS[1], ARGV[2]); return 1; else return 0; end";

        new Thread(()->{
            DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Boolean.class);
            Boolean flag = stringRedisTemplate.execute(redisScript,
                    Arrays.asList(lockName),
                    uuid, expire.toString());
            while(flag) {
                try {
                    Thread.sleep(expire / 3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    //分布式锁 （自己释放自己的锁,lua脚步）
    public void testLock() {
        //1 获取到当前锁
        String uuid = UUID.randomUUID().toString();
        Boolean tryLock = this.tryLock("lock", uuid, 30l);

        //2 如果可以获取到，进行数据操作
        if(tryLock) {
            try {
                // 查询Redis中的num值
                String value = (String)this.stringRedisTemplate.opsForValue().get("num");
                // 没有该值return
                if (StringUtils.isBlank(value)){
                    return ;
                }
                // 有值就转成成int
                int num = Integer.parseInt(value);

                //出现异常

                // 把Redis中的num值+1
                this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));

            } finally {
                //释放
                this.unlock("lock",uuid);
            }
        } else {
            //3 如果获取不到当前锁，等待
            try {
                Thread.sleep(200);
                this.testLock();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //////////////可重入 和 续期 ////////////////////////
    ////////////////////////////////////////////////////

    //分布式锁 （自己释放自己的锁,lua脚步）
    public void testLock6() {
        //1 获取到当前锁
        // setIfAbsent  就是命令 setnx
        //获取锁时候，设置过期时间，到期自动释放
        String uuid = UUID.randomUUID().toString();
        Boolean ifAbsent = stringRedisTemplate.opsForValue()
                .setIfAbsent("lock", uuid,
                        3, TimeUnit.SECONDS);

        //2 如果可以获取到，进行数据操作
        if(ifAbsent) {
            try {
                // 查询Redis中的num值
                String value = (String)this.stringRedisTemplate.opsForValue().get("num");
                // 没有该值return
                if (StringUtils.isBlank(value)){
                    return ;
                }
                // 有值就转成成int
                int num = Integer.parseInt(value);

                //出现异常

                // 把Redis中的num值+1
                this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));

            } finally {
                //操作完成之后，释放锁，lua脚本保证原子性
//                String lock = stringRedisTemplate.opsForValue().get("lock");
//                if(uuid.equals(lock)) {
//
//                    stringRedisTemplate.delete("lock");
//
//                }
                //操作完成之后，释放锁，lua脚本保证原子性
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                //创建lua脚本
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                //设置lua脚本到对象
                redisScript.setScriptText(script);
                //设置响应类型
                redisScript.setResultType(Long.class);
                //RedisTemplate执行lua脚本
                stringRedisTemplate.execute(redisScript,
                        Arrays.asList("lock"),
                        uuid);
            }
        } else {
            //3 如果获取不到当前锁，等待
            try {
                Thread.sleep(200);

                this.testLock();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //分布式锁 （自己释放自己的锁）
    public void testLock5() {
        //1 获取到当前锁
        // setIfAbsent  就是命令 setnx
        //获取锁时候，设置过期时间，到期自动释放
        String uuid = UUID.randomUUID().toString();
        Boolean ifAbsent = stringRedisTemplate.opsForValue()
                .setIfAbsent("lock", uuid,
                        3, TimeUnit.SECONDS);

        //2 如果可以获取到，进行数据操作
        if(ifAbsent) {
            try {
                // 查询Redis中的num值
                String value = (String)this.stringRedisTemplate.opsForValue().get("num");
                // 没有该值return
                if (StringUtils.isBlank(value)){
                    return ;
                }
                // 有值就转成成int
                int num = Integer.parseInt(value);

                //出现异常

                // 把Redis中的num值+1
                this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));

            } finally {
                //操作完成之后，是否锁
                String lock = stringRedisTemplate.opsForValue().get("lock");
                if(uuid.equals(lock)) {

                    stringRedisTemplate.delete("lock");

                }
            }
        } else {
            //3 如果获取不到当前锁，等待
            try {
                Thread.sleep(200);

                this.testLock();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //分布式锁 （初步实现）
    public void testLock4() {
        //1 获取到当前锁
        // setIfAbsent  就是命令 setnx
//        Boolean ifAbsent = stringRedisTemplate.opsForValue()
//                .setIfAbsent("lock", "lock");

        //获取锁时候，设置过期时间，到期自动释放
        Boolean ifAbsent = stringRedisTemplate.opsForValue()
                .setIfAbsent("lock", "lock",3, TimeUnit.SECONDS);

        //2 如果可以获取到，进行数据操作
        if(ifAbsent) {
            try {
                // 查询Redis中的num值
                String value = (String)this.stringRedisTemplate.opsForValue().get("num");
                // 没有该值return
                if (StringUtils.isBlank(value)){
                    return ;
                }
                // 有值就转成成int
                int num = Integer.parseInt(value);

                //出现异常

                // 把Redis中的num值+1
                this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));

            } finally {
                //操作完成之后，是否锁
                stringRedisTemplate.delete("lock");
            }
        } else {
            //3 如果获取不到当前锁，等待
            try {
                Thread.sleep(200);

                this.testLock();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //本地锁加锁
    public synchronized void testLock3() {
        // 查询Redis中的num值
        String value = (String)this.stringRedisTemplate.opsForValue().get("num");
        // 没有该值return
        if (StringUtils.isBlank(value)){
            return ;
        }
        // 有值就转成成int
        int num = Integer.parseInt(value);
        // 把Redis中的num值+1
        this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));
    }

    //加锁
    public void testLock2() {
        Lock lock = new ReentrantLock();

        lock.lock();
        try {
            // 查询Redis中的num值
            String value = (String)this.stringRedisTemplate.opsForValue().get("num");
            // 没有该值return
            if (StringUtils.isBlank(value)){
                return ;
            }
            // 有值就转成成int
            int num = Integer.parseInt(value);
            // 把Redis中的num值+1
            this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));

        } finally {
            lock.unlock();
        }
    }

    //没有加锁的方法
    public void testLock1() {
        //1 从redis获取数据
        String value = (String)stringRedisTemplate.opsForValue().get("num");

        //2 判断
        if(StringUtils.isBlank(value)) {
            return;
        }

        int num = Integer.parseInt(value);

        //把value值 +1
        stringRedisTemplate.opsForValue().set("num",String.valueOf(++num));
    }
}
