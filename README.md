# Spring Security

Spring Security allows to secure the access to API endpoints and
grant priviliges only to trusted users or permissions.

## ApplicationSecurityConfig class

Under the package `com.example.security` is the class where all configurations about security are done

```java
package com.example.spring.security;

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

#### Role base Authentication

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
some type that implements `GrantedAuthority` interface, such as `SimpleGrantedAuthority`:

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

#### Permission base Authentication on a method level
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

### Form base Authentication

Just by adding the spring-security dependency, a form to login the API endpoints is generated
and provides a local development password that changes every time the app runs (username is always 'user').
This is the default type of auth that comes with Spring security.

[form image]
