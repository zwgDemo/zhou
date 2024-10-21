package com.spzx.user.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.user.domain.UserInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.spzx.user.mapper.UserInfoMapper;
import com.spzx.user.service.IUserInfoService;

/**
 * 会员Service业务层处理
 *
 * @author atguigu
 * @date 2024-07-10
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService
{
    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员
     */
    @Override
    public List<UserInfo> selectUserInfoList(UserInfo userInfo)
    {
        return userInfoMapper.selectUserInfoList(userInfo);
    }

    //远程调用：用户注册
    @Override
    public void register(UserInfo userInfo) {
        //判断username是否相同，如果相同不能添加
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUsername,userInfo.getUsername());
        Long count = userInfoMapper.selectCount(wrapper);
        if(count >0) {
            throw new ServiceException("手机号已经存在");
        }

        userInfo.setStatus(1);
        userInfo.setSex(0);
        userInfo.setAvatar("http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoj0hHXhgJNOTSOFsS4uZs8x1ConecaVOB8eIl115xmJZcT4oCicvia7wMEufibKtTLqiaJeanU2Lpg3w/132");
        userInfoMapper.insert(userInfo);
    }

    //用户名查询
    @Override
    public UserInfo selectInfoByUserName(String username) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUsername,username);
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);
        return userInfo;
    }


}
