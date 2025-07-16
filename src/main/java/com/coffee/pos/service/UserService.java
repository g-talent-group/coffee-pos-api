package com.coffee.pos.service;

import com.coffee.pos.dto.RegisterRequestDTO;
import com.coffee.pos.dto.UpdateUserProfileDTO;
import com.coffee.pos.model.User;
import com.coffee.pos.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User createUser(RegisterRequestDTO request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new RuntimeException("使用者名稱已存在");
    }

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("Email 已存在");
    }

    User user =
        User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .fullName(request.getFullName())
            .role(User.UserRole.USER)
            .enabled(true)
            .build();

    return userRepository.save(user);
  }

  public User findByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new RuntimeException("使用者不存在"));
  }

  public User findById(Long userId) {
    return userRepository.findById(userId).orElseThrow();
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public User updateProfile(Long userId, @Valid UpdateUserProfileDTO request) {
    User user = findById(userId);
    if (!request.getEmail().isEmpty()) {
      user.setEmail(request.getEmail());
    }
    if (!request.getFullName().isEmpty()) {
      user.setFullName(request.getFullName());
    }
    userRepository.save(user);
    return user;
  }
}
