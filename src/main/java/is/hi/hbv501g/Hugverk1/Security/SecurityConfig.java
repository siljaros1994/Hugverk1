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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyAppUserService appUserService;

    // Constructor injection of the custom user service
    public SecurityConfig(MyAppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // Register the custom UserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        return appUserService;
    }

    // authentication provider with user details service and password encoder
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Password encoder for encoding and verifying passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security configuration with a custom login page, registration access, and static resources access
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for simplicity
                .formLogin(httpForm -> {
                    httpForm.loginPage("/login").permitAll();  // Custom login page
                    httpForm.defaultSuccessUrl("/Home");  // Default redirect after login
                })
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/register", "/css/**", "/js/**").permitAll();  // Permit access to register page and static resources
                    registry.anyRequest().authenticated();  // All other requests require authentication
                })
                .build();
    }
}
