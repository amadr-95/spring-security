package com.example.spring.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.spring.security.ApplicationUserPermission.STUDENT_READ;
import static com.example.spring.security.ApplicationUserPermission.STUDENT_WRITE;

public enum ApplicationUserRole {
    STUDENT(Set.of(STUDENT_READ)),
    ADMIN(Set.of(STUDENT_READ, STUDENT_WRITE));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        //Declarative
        /*Set<SimpleGrantedAuthority> grantedAuthorities =
                getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        //Add role
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this));
        return grantedAuthorities;*/

        //Imperative
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        //convert every ApplicationUserPermission to a SimpleGrantedAuthority
        for (ApplicationUserPermission permission : permissions) {
            grantedAuthorities.add(
                    new SimpleGrantedAuthority(permission.getPermission())
            );
        }
        //Add role
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this));

        return grantedAuthorities;
    }
}
