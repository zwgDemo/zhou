package com.spzx.user.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.user.domain.UserAddress;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.spzx.user.mapper.UserAddressMapper;
import com.spzx.user.service.IUserAddressService;

/**
 * 用户地址Service业务层处理
 *
 * @author atguigu
 * @date 2024-07-10
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService
{
    @Resource
    private UserAddressMapper userAddressMapper;


}
