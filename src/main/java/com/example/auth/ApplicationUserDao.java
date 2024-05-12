package com.example.auth;

import java.util.Optional;

public interface ApplicationUserDao {
    Optional<ApplicationUser> selectUserbyUsername(String username);
}
