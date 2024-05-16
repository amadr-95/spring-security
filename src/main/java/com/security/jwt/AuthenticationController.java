package com.security.jwt;

import com.security.auth.ApplicationUserDao;
import com.security.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final ApplicationUserDao userDao;
    private final JwtUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, ApplicationUserDao applicationUserDao, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDao = applicationUserDao;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        authenticationManager.authenticate(authentication);

        Optional<UserDetails> user = userDao.selectUserbyUsername(request.getUsername());

        return user.map(userDetails ->
                ResponseEntity.ok(jwtUtil.generateToken(userDetails)))
                .orElseGet(() -> ResponseEntity.status(400).body("An error ocurred"));

    }


}
