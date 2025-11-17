package com.cypress.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "统一响应结果")
public class Response<T> {
    @ApiModelProperty(value = "响应码")
    private String code;

    @ApiModelProperty(value = "响应信息")
    private String info;

    @ApiModelProperty(value = "响应数据")
    private T data;

    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .code("200")
                .info("操作成功")
                .data(data)
                .build();
    }

    public static <T> Response<T> error(String code, String info) {
        return Response.<T>builder()
                .code("400")
                .info("客户端错误")
                .data(null)
                .build();
    }
}