package is.hi.hbv501g.Hugverk1.Security;

import is.hi.hbv501g.Hugverk1.Model.MyAppUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// security configuration for the application. It handles login, user registration, password encoding, and more.

@Configuration
@EnableWebSecurity  // Enables Spring Security.
public class SecurityConfig {
    private final MyAppUserService appUserService;

    // Injects MyAppUserService. Our user service that retrieves user details (like username, password) from the database.
    // This service is where the user data stored in the database is fetched when a user tries to log in.
    public SecurityConfig(MyAppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // Registers our MyAppUserService as the service that Spring Security will use to load the user data from the database.
    // like when a user logs in, this service loads the user details like username, password from the MyAppUsers table.
    @Bean
    public UserDetailsService userDetailsService() {
        return appUserService;
    }

    // authentication provider which validates the user by checking their username and password. It ensures the
    // username and encoded password stored in the database match the provided credentials during login.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Encodes passwords using BCryptPasswordEncoder. This ensures that passwords are stored in a hashed format
    // in the database and not as plain text. The password is hashed before being saved to the MyAppUsers table.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security configuration that configures what users can and cannot access in the web application.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(httpForm -> {
                    httpForm.loginPage("/login").permitAll(); // Specifies /login as the login page
                    httpForm.defaultSuccessUrl("/Home");  // After successful login, users are redirected to /Home
                })
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/register", "/css/**", "/js/**").permitAll();  // Permit access to register page without logging in.
                    registry.anyRequest().authenticated();  // All other requests require authentication
                })
                .build();
    }
}
