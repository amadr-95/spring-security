package com.example.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.security.ApplicationUserRole.ADMIN;
import static com.example.security.ApplicationUserRole.STUDENT;

@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;

    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUser> selectUserbyUsername(String username) {
        return applicationUsers().stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }

    private List<ApplicationUser> applicationUsers() {
        return List.of(
                new ApplicationUser(
                        "admin2",
                        passwordEncoder.encode("admin2"),
                        ADMIN.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUser(
                        "student2",
                        "student2",
                        STUDENT.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                )
        );
    }
}
