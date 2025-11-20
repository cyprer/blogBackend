package com.cypress.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.function.Function;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // 从配置文件读取JWT密钥（建议在application.yml中配置）
    @Value("${jwt.secret}")
    private String secretKey;

    // 令牌过期时间（例如24小时）
    @Value("${jwt.expiration}")
    private long expiration;

    // 生成密钥（基于配置的secretKey）
    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID（作为令牌的主体）
     * @return 令牌字符串
     */
    public String generateToken(Long userId) {
        // 可选：添加自定义声明（如角色、用户名等）
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId.toString());
    }

    /**
     * 创建令牌的核心方法
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // 主体（通常为用户ID）
                .setIssuedAt(now) // 签发时间
                .setExpiration(expirationDate) // 过期时间
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // 签名算法
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long extractUserId(String token) {
        return Long.parseLong(extractSubject(token));
    }

    /**
     * 验证令牌是否有效（仅基于签名和过期时间）
     * @param token 令牌
     * @return 提取的用户ID，如果无效则返回null
     */
    public Long validateToken(String token) {
        try {
            if (isTokenExpired(token)) {
                return null;
            }
            return extractUserId(token);
        } catch (Exception e) {
            return null;
        }
    }

    // 以下为内部工具方法
    private String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}