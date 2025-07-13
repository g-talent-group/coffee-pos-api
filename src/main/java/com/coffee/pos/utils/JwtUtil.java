package com.coffee.pos.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // 生成包含角色資訊的 Token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // 加入角色資訊到 claims
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        claims.put("roles", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return createToken(claims, userDetails.getUsername());
    }

    // 生成帶有自定義聲明的 Token
    public String generateTokenWithClaims(String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        SecretKey key = getSigningKey();

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 驗證 Token
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    // 檢查 Token 是否過期
    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            log.error("Failed to extract expiration from token: {}", e.getMessage());
            return true;
        }
    }

    // 提取過期時間
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 提取使用者名稱
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 提取角色資訊
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    // 提取所有聲明
    private Claims extractAllClaim(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 提取特定聲明
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    // 檢查 Token 是否快要過期（用於刷新機制）
    public boolean isTokenExpiringSoon(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            long timeUntilExpiry = expiration.getTime() - now.getTime();
            // 如果 Token 在 30 分鐘內過期，認為需要刷新
            return timeUntilExpiry < (30 * 60 * 1000);
        } catch (JwtException e) {
            return true;
        }
    }
}
