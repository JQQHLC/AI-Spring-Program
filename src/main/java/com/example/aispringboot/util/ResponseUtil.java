package com.example.aispringboot.util;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.common.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {
    //过滤器中的异常处理
    public static void writeError(HttpServletResponse response, ResultCode resultCode) {
        //根据不同结果码返回不同的响应
      int status = switch (resultCode) {
            case UNAUTHORIZED,ACCESS_UNAUTHORIZED,TOKEN_INVALID,TOKEN_EXPIRED,TOKEN_BLOCKED-> HttpStatus.HTTP_UNAUTHORIZED;
            case TOKEN_ACCESS_FORBIDDEN -> HttpStatus.HTTP_FORBIDDEN;
            default -> HttpStatus.HTTP_BAD_REQUEST;
        };
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try(PrintWriter writer = response.getWriter()) {
           String JsonResponse =JSONUtil.toJsonStr(Result.error(resultCode.getCode(), resultCode.getMessage(), null));
            writer.print(JsonResponse);
            writer.flush();//确保数据被写入响应
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
