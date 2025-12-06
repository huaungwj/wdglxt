package com.example.dms.exception;

/**
 * JWT认证异常
 * 用于标识JWT相关的认证失败（签名不匹配、过期等）
 */
public class JwtAuthenticationException extends RuntimeException {
    
    public JwtAuthenticationException(String message) {
        super(message);
    }
    
    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

