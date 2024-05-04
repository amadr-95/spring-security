package com.example.spring.security;

import java.util.Set;

import static com.example.spring.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    STUDENT(Set.of(STUDENT_UPDATE)),
    ADMIN(Set.of(STUDENT_READ, STUDENT_WRITE, STUDENT_DELETE, STUDENT_UPDATE));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}
