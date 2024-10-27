package is.hi.hbv501g.Hugverk1.Security;

import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static is.hi.hbv501g.Hugverk1.Security.PasswordEncoderConfig.passwordEncoder;

// security configuration for the application, It handles login, user registration, password encoding, and more.
@Configuration
@EnableWebSecurity // Enables Spring Security.
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final MyAppUserService myAppUserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;  // Inject handler

    @Autowired
    public SecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint,
                          MyAppUserService myAppUserService,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.myAppUserService = myAppUserService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;  // Initialize handler
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myAppUserService).passwordEncoder(passwordEncoder());
    }

    // Security configuration that configures what users can and cannot access in the web application.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login", "/users/register", "/css/**", "/api/authenticate").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/home/donor", "/home/recipient", "/donorprofile", "/recipientprofile", "/dr").authenticated() // Allow access to the home pages after login
                        .requestMatchers("/messages/donor", "/messages/recipient").authenticated()
                        .anyRequest().authenticated())  // All other requests need authentication
                .formLogin(login -> login
                        .loginPage("/users/login")
                        .successHandler(customAuthenticationSuccessHandler)  // Add success handler
                        .permitAll())
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(authenticationEntryPoint))
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/users/login?logout=true")
                        .permitAll())
                .addFilterBefore(new CustomFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
