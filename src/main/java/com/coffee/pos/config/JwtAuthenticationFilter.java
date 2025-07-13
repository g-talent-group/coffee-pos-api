package com.coffee.pos.config;

import com.coffee.pos.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 獲取 Authorization Header
        final String authorizationHeader = request.getHeader("Authorization");

        // 檢查是否包含 Bearer Token
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found in request to {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 提取 JWT Token
            final String jwt = authorizationHeader.substring(7);
            final String username = jwtUtil.extractUsername(jwt);

            // 檢查使用者名稱存在且當前沒有認證資訊
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 載入使用者詳情
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 驗證 Token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // 創建認證 Token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // 設定請求詳情
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 設定安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Successfully authenticated user: {} for request: {}",
                            username, request.getRequestURI());

                    // 檢查 Token 是否快要過期，記錄警告
                    if (jwtUtil.isTokenExpiringSoon(jwt)) {
                        log.warn("JWT token for user {} is expiring soon", username);
                        response.setHeader("X-Token-Warning", "Token expiring soon");
                    }
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Invalid token\"}");
                    return;
                }
            }
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token expired\",\"message\":\"" + e.getMessage() + "\"}");
            return;
        } catch (JwtException e) {
            log.error("JWT processing failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid token format\",\"message\":\"" + e.getMessage() + "\"}");
            return;
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication error\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 跳過認證的路徑
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.equals("/favicon.ico");
    }
}