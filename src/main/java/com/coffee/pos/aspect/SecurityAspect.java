package com.coffee.pos.aspect;

import com.coffee.pos.exception.UnauthorizedException;
import com.coffee.pos.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class SecurityAspect {

    @Autowired
    private SecurityService securityService;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint,
                                RequiresPermission requiresPermission) {

        String currentUser = securityService.getCurrentUser();
        String[] requiredPermissions = requiresPermission.value();
        LogicalOperator operator = requiresPermission.operator();

        boolean hasPermission = false;

        if (operator == LogicalOperator.AND) {
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(permission -> securityService.hasPermission(currentUser, permission));
        } else {
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(permission -> securityService.hasPermission(currentUser, permission));
        }

        if (!hasPermission) {
            log.warn("User {} attempted to access method {} without required permissions: {}",
                    currentUser,
                    joinPoint.getSignature().getName(),
                    Arrays.toString(requiredPermissions));

            throw new UnauthorizedException("Insufficient permissions");
        }

        log.debug("Permission check passed for user {} accessing {}",
                currentUser,
                joinPoint.getSignature().getName());
    }
}