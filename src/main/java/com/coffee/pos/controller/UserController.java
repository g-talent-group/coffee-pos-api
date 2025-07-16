package com.coffee.pos.controller;

import com.coffee.pos.dto.UpdateUserProfileDTO;
import com.coffee.pos.dto.UserProfileDTO;
import com.coffee.pos.model.User;
import com.coffee.pos.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        UserProfileDTO profile = UserProfileDTO.fromUser(user);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'READ')")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        User user = userService.findById(userId);
        UserProfileDTO profile = UserProfileDTO.fromUser(user);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile/{userId}")
    @PreAuthorize("hasPermission(#userId, 'User', 'WRITE')")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserProfileDTO request) {

        User updatedUser = userService.updateProfile(userId, request);
        UserProfileDTO profile = UserProfileDTO.fromUser(updatedUser);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        log.info(users.toString());
        List<UserProfileDTO> profiles = users.stream()
                .map(UserProfileDTO::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(profiles);
    }
}