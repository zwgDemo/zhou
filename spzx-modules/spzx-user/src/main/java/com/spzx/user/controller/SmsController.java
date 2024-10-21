package com.spzx.user.controller;

import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.user.service.ISmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "短信接口")
@RestController
@RequestMapping("/sms")
public class SmsController extends BaseController {

    @Resource
    private ISmsService iSmsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //发送短信验证码的方法
    @Operation(summary = "获取图片验证码")
    @GetMapping(value = "sendCode/{phone}")
    public AjaxResult sendCode(@PathVariable("phone") String phone) {

        //根据手机号查询redis，如果redis有手机号验证码数据直接返回
        String code = stringRedisTemplate.opsForValue().get("phone:code:" + phone);
        if(StringUtils.hasText(code)) {
            return success();
        }

        //1 生成验证码
        code = new DecimalFormat("0000").format(new Random().nextInt(10000));

        //2 根据手机号，把验证码发送到手机里面
        Boolean flag = iSmsService.sendPhoneCode(phone,code);


        //3 发送成功
        if(flag) {
            //把验证码存储到redis里面，设置过期时间
            //key：手机号  value：验证码
            stringRedisTemplate.opsForValue().set("phone:code:"+phone,code,30, TimeUnit.MINUTES);
        }
        return success();
    }
}
