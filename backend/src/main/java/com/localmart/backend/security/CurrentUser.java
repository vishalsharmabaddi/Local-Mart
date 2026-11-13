package com.localmart.backend.security;

import org.springframework.security.oauth2.jwt.Jwt;

import com.localmart.backend.user.Role;

// "who is calling?" — read from the already-verified token, so services don't depend on JWT classes
public record CurrentUser(Long id, Role role) {

    public static CurrentUser from(Jwt jwt) {
        return new CurrentUser(Long.valueOf(jwt.getSubject()), Role.valueOf(jwt.getClaimAsString("role")));
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
