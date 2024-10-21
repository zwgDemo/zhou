package com.spzx.user.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.domain.UserInfo;

/**
 * 会员Service接口
 *
 * @author atguigu
 * @date 2024-07-10
 */
public interface IUserInfoService extends IService<UserInfo>
{

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

    //远程调用：用户注册
    void register(UserInfo userInfo);

    UserInfo selectInfoByUserName(String username);
}
