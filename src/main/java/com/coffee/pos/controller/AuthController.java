package com.coffee.pos.controller;

import com.coffee.pos.dto.CommonObjectResponse;
import com.coffee.pos.dto.LoginRequestDTO;
import com.coffee.pos.dto.RegisterRequestDTO;
import com.coffee.pos.service.UserService;
import com.coffee.pos.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "認證 API", description = "使用者認證相關 API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "使用者登入", description = "使用者登入並取得 JWT token")
    public ResponseEntity<CommonObjectResponse<String>> login(@RequestBody LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            log.info("User {} logged in successfully", request.getUsername());
            return ResponseEntity.ok(CommonObjectResponse.success(jwt, "登入成功"));

        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(CommonObjectResponse.error("登入失敗"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "使用者註冊", description = "新使用者註冊")
    public ResponseEntity<CommonObjectResponse<String>> register(@RequestBody RegisterRequestDTO request) {
        try {
            userService.createUser(request);
            log.info("User {} registered successfully", request.getUsername());
            return ResponseEntity.ok(CommonObjectResponse.success("註冊成功", "使用者註冊成功"));
        } catch (Exception e) {
            log.error("Registration failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(CommonObjectResponse.error("註冊失敗: " + e.getMessage()));
        }
    }
}