package com.example.dms.exception;

import com.example.dms.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理JWT认证异常（签名不匹配、过期等）
     * 返回401状态码，前端会自动跳转到登录页
     */
    @ExceptionHandler(JwtAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleJwtAuthentication(JwtAuthenticationException e) {
        log.warn("JWT认证失败: {}", e.getMessage());
        return ApiResponse.fail("登录已过期，请重新登录");
    }

    /**
     * 处理缺少必需的请求头异常（如缺少Authorization）
     * 返回401状态码，前端会自动跳转到登录页
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleMissingRequestHeader(MissingRequestHeaderException e) {
        log.warn("缺少必需的请求头: {}", e.getMessage());
        if (e.getHeaderName().equals("Authorization")) {
            return ApiResponse.fail("请先登录");
        }
        return ApiResponse.fail("缺少必需的请求头: " + e.getHeaderName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValid(MethodArgumentNotValidException e){
        String msg = e.getBindingResult().getFieldError() != null ? e.getBindingResult().getFieldError().getDefaultMessage() : "参数错误";
        return ApiResponse.fail(msg);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBiz(RuntimeException e){
        // 如果是JWT认证异常，已经在上面处理了，这里跳过
        if (e instanceof JwtAuthenticationException) {
            return handleJwtAuthentication((JwtAuthenticationException) e);
        }
        log.error("业务异常", e);
        String msg = e.getMessage();
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            msg = msg + ": " + e.getCause().getMessage();
        }
        return ApiResponse.fail(msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleOther(Exception e){
        // 忽略Chrome DevTools的自动请求
        if (e instanceof org.springframework.web.servlet.resource.NoResourceFoundException) {
            String message = e.getMessage();
            if (message != null && (message.contains(".well-known") || message.contains("favicon.ico"))) {
                // 这些是浏览器自动请求的资源，不需要记录为错误
                return ApiResponse.fail("资源不存在");
            }
        }
        log.error("服务器异常", e);
        return ApiResponse.fail("服务器异常: " + e.getMessage());
    }
}
