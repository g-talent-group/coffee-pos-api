package com.coffee.pos.service;

import com.coffee.pos.model.User;
import com.coffee.pos.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Loading user details for username: {}", username);

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
              log.error("User not found: {}", username);
              return new UsernameNotFoundException("User not found: " + username);
            });

    // 檢查帳號是否被停用
    if (!user.getEnabled()) {
      log.warn("User account is disabled: {}", username);
      throw new DisabledException("User account is disabled: " + username);
    }

    // 構建 Spring Security UserDetails
    List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
    );

    log.debug("User {} loaded with role: {}", username, user.getRole());

    return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.getEnabled())
            .build();
  }
}
