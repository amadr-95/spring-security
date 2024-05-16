# Spring Security

Spring Security allows to secure the access to API endpoints and
grant priviliges only to trusted users or permissions.
In this example we start from a API already built.

## ApplicationSecurityConfig class

Under the package `com.security.security` is the class where all configurations about security are done

```java
package com.security.spring.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {
}
```

### User Roles and Permissions (Authorities)

We can create **roles** and **permissions** as follows:

#### Permissions

```java
public enum ApplicationUserPermission {
    STUDENT_READ("student:read"),
    STUDENT_WRITE("student:write"),
    STUDENT_DELETE("student:delete"),
    STUDENT_UPDATE("student:update");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
```

#### Roles

Once permissions are stablished, they can be assigned to roles. So in
this manner a specific role can have a set of permissions.

```java
public enum ApplicationUserRole {
    STUDENT(Set.of(STUDENT_READ, STUDENT_UPDATE)),
    ADMIN(Set.of(STUDENT_READ, STUDENT_WRITE, STUDENT_DELETE, STUDENT_UPDATE));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}
```

## Types of Auth

### Basic Auth

In this method the client send the user and password in
every request, which are encrypted by Basic64.
There is no posibility to logout
(mostly used with external API's)

[basic-auth-image]

#### Role based Authentication

Granting access to API endpoints filtering by **user role**.

> [!WARNING]
> **Cross Site Request Forgery**
> The action of forging a copy or imitation of a document, signature,
> banknote, or work of art.
> Spring Security try to protect teh API by default.
> [csrf-image]
> On POST, PUT, DELETE methods the request is rejected
> because no token was sent to the client when logged in. That is
> what csrf does by default when it is enabled.
> [csrf-image-diagram]  
> [**When to use CSRF protection**](https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/csrf.html)
> CSRF protection is recommended to use for any request that could be
> processed by a browser by normal users. If you are only creating a service that is used by non-browser clients, you
> will
> likely want to disable CSRF protection. So in this case we can disable it.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
            .authorizeHttpRequests(authz -> authz
                    .csrf(AbstractHttpConfigurer::disable)
                    .requestMatchers("/", "index.html", "/css/*", "/js/*")
                    .permitAll()
                    .requestMatchers("/api/**").hasRole(ADMIN.toString())
                    .anyRequest()
                    .authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
}
```

Creating the user using `InMemoryUserDetailsManager` with its role (ADMIN).

```java
@Bean
public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("password"))
            .roles("ADMIN")
            .build();
    return new InMemoryUserDetailsManager(admin);
}
```

> [!NOTE]
> The password must be encoded

#### Permission based Authentication

Granting access to API endpoints filtering by **user's permissions** instead of the role itself.

Now we have to change `.role(ROLE)` for `.authorities(authorities)`:

```java

@Bean
public InMemoryUserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
    UserDetails studentUser = User.builder()
            .username("student")
            .password(passwordEncoder.encode("student"))
            //.roles(STUDENT.toString()) //ROL_STUDENT
            .authorities(STUDENT.getGrantedAuthorities())
            .build();

    UserDetails adminUser = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            //.roles(ADMIN.toString()) //ROL_ADMIN
            .authorities(ADMIN.getGrantedAuthorities())
            .build();
    return new InMemoryUserDetailsManager(studentUser, adminUser);
}
```

To be able to obtain permissions from an user, we have to convert permissions from plain text to
a type that implements `GrantedAuthority` interface, such as `SimpleGrantedAuthority`:

```java
public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
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
```

By doing this, we have attached the correct authorities to users.

#### Permission based Authentication on a method level

We can also set authentication by annotating methods with `@PreAuthorized()`.
Within the annotation we can both filter by roles or permissions passing it these strings:

- hasRole('ROLE_')
- hasAnyRole('ROLE_')
- hasAuthority('permission')
- hasAnyAuthority('permission')

To use that we also have to annotate `ApplicationSecurityConfig` with `@EnableMethodSecurity`

```java

@GetMapping
@PreAuthorize("hasRole('ADMIN')")
public List<Student> findAllStudents() {
    return studentService.findAllStudents();
}

@GetMapping("{id}")
@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
public Student findStudentById(@PathVariable("id") Integer studentId) throws StudentNotFoundException {
    return studentService.findStudentById(studentId);
}

@PostMapping
@PreAuthorize("hasAuthority('student:write')")
public void addNewStudent(@RequestBody StudentRequest student) throws StudentException {
    studentService.addNewStudent(student);
}
```

### Form based Authentication

Form based is the default type of auth that comes with Spring security.
It auto-generates a form to login with user credentials. Once logged,
a cookie `SESSIONID` (generated when the user login for first time) is sending
to the server on every request.

Enabling form auth instead of basic auth is just a matter of changing one line:

```java

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
            .authorizeHttpRequests(authz -> authz
                    ...
            )
            .formLogin(Customizer.withDefaults())
            .build();
}
```

[form image]

#### Options

* Custom login page
    ```java
    .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
        .passwordParameter("password") //same name as form name in html
        .usernameParameter("username")
        .loginPage("/login")
        .permitAll()
        .defaultSuccessUrl("url", true)
    )
    ```

* Custom remember me expiration time  
  By default SESSIONID cookie expires after 30' of inactivity.
  These can be changed as follows:
    ```java
    //.rememberMe(Customizer.withDefaults()) //valid 30'
    .rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
        .rememberMeParameter("remember-me")  
        .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
        .key("securekey") //key to encrypt the username and expiration time instead of default one
    ) 
    ```
* Custom logout
    ```java
    .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
        .logoutUrl("/logout")
        .clearAuthentication(true)
        .deleteCookies("JSESSIONID", "remember-me")
        .logoutSuccessUrl("/index.html")
    )
    ```

## JWT (JSON Web Token)

[jwt-image]

### Java JWT Library

Managing authentication requests based on username and password using JSON Web Tokens (JWT).

[jwt-image2]

To use these library we have to add the dependecies to `pom.xml`:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>

<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
```

`UsernamePasswordAuthenticationFilter` is the dafault class Spring uses.
By extending from it we can override some methods and give them specific implementation.

### First step: validates credentials
- Modelate authentication request with `UsernameAndPasswordAuthenticationRequest.class`
- Override `attempAuthentication` method.
- If username and password are correct, this method return the Authentication

### Second step: generates token and send it to the client
- `successfulAuthentication` method will be executed only if `attempAuthentication` does not fail.
- Create the token
- Send it to the client