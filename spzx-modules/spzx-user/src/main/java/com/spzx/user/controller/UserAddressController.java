package com.spzx.user.controller;

import java.util.List;
import java.util.Arrays;

import com.spzx.common.core.domain.R;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.user.domain.UserAddress;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.service.IUserAddressService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 用户地址Controller
 *
 * @author atguigu
 * @date 2024-07-10
 */
@Tag(name = "用户地址接口管理")
@RestController
@RequestMapping("/userAddress")
public class UserAddressController extends BaseController
{
    @Resource
    private IUserAddressService userAddressService;

}
