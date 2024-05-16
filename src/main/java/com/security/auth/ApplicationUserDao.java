package com.security.auth;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface ApplicationUserDao {
    Optional<UserDetails> selectUserbyUsername(String username);
}
