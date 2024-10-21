package com.spzx.product.lock;

import com.spzx.common.core.web.domain.AjaxResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("testLock")
    public AjaxResult testLock() {
        testService.testLock();
        return AjaxResult.success();
    }
}
