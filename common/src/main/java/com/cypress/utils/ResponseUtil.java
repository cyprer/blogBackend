package com.cypress.utils;

import com.cypress.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 响应工具类，用于将自定义Response转换为ResponseEntity
 */
public class ResponseUtil {

    /**
     * 将自定义Response转换为ResponseEntity，并根据Response中的code设置HTTP状态码
     * @param response 自定义响应对象
     * @param <T> 响应数据类型
     * @return ResponseEntity包装的响应
     */
    public static <T> ResponseEntity<Response<T>> toResponseEntity(Response<T> response) {
        HttpStatus status = getHttpStatus(response.getCode());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 根据自定义响应码获取对应的HTTP状态码
     * @param code 自定义响应码
     * @return HTTP状态码
     */
    private static HttpStatus getHttpStatus(String code) {
        switch (code) {
            case "200":
                return HttpStatus.OK;
            case "201":
                return HttpStatus.CREATED;
            case "400":
                return HttpStatus.BAD_REQUEST;
            case "401":
                return HttpStatus.UNAUTHORIZED;
            case "403":
                return HttpStatus.FORBIDDEN;
            case "404":
                return HttpStatus.NOT_FOUND;
            case "500":
                return HttpStatus.INTERNAL_SERVER_ERROR;
            default:
                // 默认返回200状态码
                return HttpStatus.OK;
        }
    }
}