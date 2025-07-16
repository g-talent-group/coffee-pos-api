package com.coffee.pos.utils;

import com.coffee.pos.model.User;
import com.coffee.pos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null) {
            return false;
        }

        String username = authentication.getName();
        String permissionString = permission.toString();

        // 檢查是否為管理員
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true; // 管理員有所有權限
        }

        // 檢查資源擁有者權限
        if (targetDomainObject instanceof User) {
            User targetUser = (User) targetDomainObject;
            return username.equals(targetUser.getUsername());
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null) {
            return false;
        }

        String username = authentication.getName();

        // 檢查是否為管理員
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        // 檢查使用者是否擁有該資源
        if ("User".equals(targetType)) {
            return userRepository.findById((Long) targetId)
                    .map(user -> username.equals(user.getUsername()))
                    .orElse(false);
        }

        return false;
    }
}