package is.hi.hbv501g.Hugverk1.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// security configuration for the application, It handles login, user registration, password encoding, and more.
@Configuration
@EnableWebSecurity // Enables Spring Security.
public class SecurityConfig {

    // Encodes passwords using BCryptPasswordEncoder. This ensures that passwords are stored in a hashed format
    // in the database and not as plain text. The password is hashed before being saved to the MyAppUsers table.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security configuration that configures what users can and cannot access in the web application.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(login -> login
                        .loginPage("/users/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login", "/users/register", "/css/**").permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}