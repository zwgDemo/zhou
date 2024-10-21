package com.spzx.product.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductUnit extends BaseEntity {

    /** 名称 */
    @Schema(description = "商品单位名称")
    @NotBlank(message = "商品单位名称不能为空")
    @Size(min = 0, max = 10, message = "商品单位名称长度不能超过10个字符")
    private String name;
}
