package com.example.dms.common.util;

import com.example.dms.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    // 密钥
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 过期时间（7天）
    private final long expiration = 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成Token
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (JwtAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtAuthenticationException("Token解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("userId", Long.class);
        } catch (JwtAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtAuthenticationException("Token解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析Token
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("Token已过期", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthenticationException("不支持的Token格式", e);
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException("Token格式错误", e);
        } catch (SignatureException e) {
            throw new JwtAuthenticationException("Token签名不匹配", e);
        } catch (JwtException e) {
            throw new JwtAuthenticationException("Token解析失败: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("Token为空", e);
        } catch (Exception e) {
            throw new JwtAuthenticationException("Token解析失败: " + e.getMessage(), e);
        }
    }
}
