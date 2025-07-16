package com.coffee.pos.controller;

import com.coffee.pos.dto.CommonObjectResponse;
import com.coffee.pos.dto.LoginRequestDTO;
import com.coffee.pos.dto.RegisterRequestDTO;
import com.coffee.pos.dto.TokenResponseDTO;
import com.coffee.pos.model.RefreshToken;
import com.coffee.pos.service.RefreshTokenService;
import com.coffee.pos.service.UserService;
import com.coffee.pos.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "認證 API", description = "使用者認證相關 API")
@Validated
public class AuthController {

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserDetailsService userDetailsService;
    @Autowired
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    @Operation(summary = "使用者登入", description = "使用者登入並取得 JWT token")
    public ResponseEntity<CommonObjectResponse<TokenResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        log.info("Login attempt for user: {}", request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 生成 access token
            String accessToken = jwtUtil.generateAccessToken(userDetails);

            // 生成 refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

            TokenResponseDTO response = TokenResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(900L) // 15 分鐘
                    .build();

            return ResponseEntity.ok(CommonObjectResponse.success(response, "登入成功"));


        } catch (BadCredentialsException e) {
            log.warn("Login failed for user {}: Invalid credentials", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonObjectResponse.error("使用者名稱或密碼錯誤"));
        } catch (DisabledException e) {
            log.warn("Login failed for user {}: Account disabled", request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CommonObjectResponse.error("帳號已被停用"));
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonObjectResponse.error("登入失敗，請稍後再試"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "使用者註冊", description = "新使用者註冊")
    public ResponseEntity<CommonObjectResponse<String>> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        log.info("Registration attempt for user: {}", request.getUsername());

        try {
//      // 檢查使用者名稱是否已存在
//      if (userService.existsByUsername(request.getUsername())) {
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(CommonObjectResponse.error("使用者名稱已存在"));
//      }
//
//      // 檢查郵箱是否已存在
//      if (userService.existsByEmail(request.getEmail())) {
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(CommonObjectResponse.error("郵箱地址已被使用"));
//      }

            // 創建使用者
            userService.createUser(request);

            log.info("User {} registered successfully", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonObjectResponse.success("註冊成功", "使用者註冊成功，請使用新帳號登入"));

        } catch (Exception e) {
            log.error("Registration failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonObjectResponse.error("註冊失敗: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用有效的 Token 獲取新的 Token")
    public ResponseEntity<CommonObjectResponse<String>> refreshToken(
            HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonObjectResponse.error("缺少有效的 Authorization Header"));
        }

        try {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.extractUsername(jwt);

            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    String newToken = jwtUtil.generateToken(userDetails);
                    log.info("Token refreshed for user: {}", username);
                    return ResponseEntity.ok(CommonObjectResponse.success(newToken, "Token 刷新成功"));
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonObjectResponse.error("無效的 Token"));

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonObjectResponse.error("Token 刷新失敗"));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "使用者登出", description = "登出並清除認證資訊")
    public ResponseEntity<CommonObjectResponse<String>> logout(
            HttpServletRequest request) {

        // 清除 Security Context
        SecurityContextHolder.clearContext();

        // 在實際應用中，這裡可以將 Token 加入黑名單
        log.info("User logged out successfully");
        return ResponseEntity.ok(CommonObjectResponse.success("", "登出成功"));
    }
}
