package com.security.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static com.security.security.UserPermission.STUDENT_READ;
import static com.security.security.UserPermission.STUDENT_WRITE;

public enum UserRole {
    STUDENT(Set.of(STUDENT_READ)),
    ADMIN(Set.of(STUDENT_READ, STUDENT_WRITE));

    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        //convert every ApplicationUserPermission to a SimpleGrantedAuthority
        for (UserPermission permission : permissions) {
            grantedAuthorities.add(
                    new SimpleGrantedAuthority(permission.getPermission())
            );
        }
        //Add role
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this));

        return grantedAuthorities;
    }
}
