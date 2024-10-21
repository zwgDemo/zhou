package com.spzx.auth.service;

import com.spzx.auth.form.LoginBody;
import com.spzx.auth.form.RegisterBody;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.system.api.model.LoginUser;
import com.spzx.user.api.RemoteUserInfoService;
import com.spzx.user.domain.UserInfo;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class H5LoginService
{

    @Resource
    private SysRecordLogService recordLogService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SysPasswordService passwordService;

    @Resource
    private RemoteUserInfoService remoteUserInfoService;

    //注册
    public void register(RegisterBody registerBody) {

        //1 TODO 数据非空校验

        //2 验证码校验
        //把reids存储验证码 和 输入的验证码比较
        String inputCode = registerBody.getCode();
        String phone = registerBody.getUsername();
        String redisCode = stringRedisTemplate.opsForValue().get("phone:code:" + phone);
        if(!inputCode.equals(redisCode)) {
            throw new ServiceException("验证码不正确");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(registerBody.getUsername());
        userInfo.setNickName(registerBody.getNickName());
        //密码
        String encryptPassword = SecurityUtils.encryptPassword(registerBody.getPassword());
        userInfo.setPassword(encryptPassword);

        remoteUserInfoService.register(userInfo, SecurityConstants.INNER);
    }

    //登录的方法
    public LoginUser loginUser(String username,String password) {

        //1 TODO 非空校验

        //2 根据用户名查询用户信息
        R<UserInfo> userInfoResult =
                remoteUserInfoService.getUserInfo(username, SecurityConstants.INNER);
        UserInfo userInfo = userInfoResult.getData();
        //判断userInfo为空
        if(userInfo == null) { //用户名不存在
            throw new ServiceException("登录用户：" + username + " 不存在");
        }

        //3 如果用户名存在，比较密码
        //输入的密码和数据库查询出来的用户对应密码进行比较

//        String dataPassword = userInfo.getPassword();
//        String encryptPassword = SecurityUtils.encryptPassword(password);
//        if(!encryptPassword.equals(dataPassword)) {
//            throw new ServiceException("密码不正确");
//        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(userInfo.getId());
        loginUser.setUsername(userInfo.getUsername());
        loginUser.setPassword(userInfo.getPassword());
        loginUser.setStatus(userInfo.getStatus()+"");

        //密码校验
        passwordService.validate(loginUser,password);

        return loginUser;
    }
}
