package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class ApplicationSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "index.html", "/css/*", "/js/*")
                        .permitAll()
                        //.requestMatchers("/api/**").hasRole(ADMIN.toString())
                        /* MOVE TO CONTROLLER METHODS
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAuthority(STUDENT_WRITE.getPermission())
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAuthority(STUDENT_WRITE.getPermission())
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(STUDENT_WRITE.getPermission())
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority(
                                STUDENT_WRITE.getPermission(), STUDENT_READ.getPermission())
                                */
                        .anyRequest()
                        .authenticated()
                )
                //.formLogin(Customizer.withDefaults()) //SESSIONID expires after 30' of inactivity
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                                .loginPage("/login")
                                .passwordParameter("password") //same name as form name in html
                                .usernameParameter("username") //same name as form name in html
                                .permitAll()
                        //.defaultSuccessUrl("/api/v1/students", true)
                )
                //.rememberMe(Customizer.withDefaults()) //two weeks by default
                .rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                        .key("securekey") //key to encrypt the username and expiration time instead of default one
                )
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/logout")
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .logoutSuccessUrl("/index.html")
                )
                .build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
        UserDetails studentUser = User.builder()
                .username("student")
                .password(passwordEncoder.encode("student"))
                //.roles(STUDENT.toString()) //ROL_STUDENT
                .authorities(ApplicationUserRole.STUDENT.getGrantedAuthorities())
                .build();

        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                //.roles(ADMIN.toString()) //ROL_ADMIN
                .authorities(ApplicationUserRole.ADMIN.getGrantedAuthorities())
                .build();
        return new InMemoryUserDetailsManager(studentUser, adminUser);
    }
}
