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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import static is.hi.hbv501g.Hugverk1.Security.PasswordEncoderConfig.passwordEncoder;

// security configuration for the application, It handles login, user registration, password encoding, and more.
@Configuration
@EnableWebSecurity // Enables Spring Security.
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final MyAppUserService myAppUserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint,
                          MyAppUserService myAppUserService,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
                          PasswordEncoder passwordEncoder) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.myAppUserService = myAppUserService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myAppUserService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/uploads/**");
    }

    // Security configuration that configures what users can and cannot access in the web application.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1).maxSessionsPreventsLogin(false)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/register", "/uploads/**", "/api/authenticate", "/css/**").permitAll()
                        .requestMatchers("/error").permitAll() // Allow all access to /error
                        .requestMatchers("/api/**", "/api/recipient/profile/**", "/api/donor/profile/**", "/api/recipient/profile/saveOrEdit").authenticated()
                        .requestMatchers("/api/recipient/**", "/api/donor/**", "/api/match/**", "/api/bookings/book").authenticated()
                        .requestMatchers("/api/**","/api/messages/send", "/api/messages/conversation/**").authenticated()
                        .requestMatchers("/api/bookings/donor/{donorId}/pending").authenticated()
                        .requestMatchers("/api/bookings/confirm/**").authenticated()
                        .requestMatchers("/api/bookings/cancel/**").authenticated()
                        .requestMatchers("/users/login", "/users/register").permitAll() //start of intellij
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/admin/**", "/home/admin", "/donorlimits", "/delete/{username}", "/reports", "/history").hasRole("ADMIN")
                        .requestMatchers("/home/donor", "/donorprofile", "/donor/view/**", "/bookings/donor").authenticated()
                        .requestMatchers("/home/recipient", "/recipientprofile", "/recipient/view/**", "/bookings/recipient", "/recipient/favorite/**").authenticated()
                        .requestMatchers("/messages/**", "/messages/{userType}/{id:[0-9]+}", "/dr").authenticated()
                        .requestMatchers("/match/donor/matches", "/match/recipient/matches", "/match/approveMatch", "/match/unmatch").authenticated()
                        .requestMatchers("/bookings/book").authenticated()
                        .requestMatchers("/bookings/confirm/**").authenticated()
                        .requestMatchers("/bookings/cancel/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/users/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout", "POST"))
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/users/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .addFilterBefore(new CustomFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}