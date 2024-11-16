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
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;  // Inject handler

    @Autowired
    public SecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint,
                          MyAppUserService myAppUserService,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.myAppUserService = myAppUserService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
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
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)  // Here we always create a session
                        .sessionFixation().migrateSession()  // Migrate the session to prevent session fixation attacks
                        .maximumSessions(1).maxSessionsPreventsLogin(false))  // at last we allow only one session per user
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login", "/users/register", "/css/**", "/api/authenticate").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/admin/**", "/home/admin", "/delete/{username}", "/reports").authenticated()
                        .requestMatchers("/home/donor", "/donorprofile", "/donor/view/**", "/bookings/donor").authenticated()
                        .requestMatchers("/home/recipient", "/recipientprofile",  "/recipient/view/**", "/bookings/recipient", "/recipient/favorite/**").authenticated()
                        .requestMatchers("/messages/**", "/messages/{userType}/{id}", "/dr").authenticated()
                        .requestMatchers("/match/donor/matches", "/match/recipient/matches", "/match/approveMatch", "/match/unmatch").authenticated()
                        .anyRequest().authenticated())  // All other requests need authentication
                .formLogin(login -> login
                        .loginPage("/users/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll())
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(authenticationEntryPoint))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout", "POST"))
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/users/login?logout=true")
                        .invalidateHttpSession(true) //This will invalidate session on logout
                        .clearAuthentication(true) // This will clear authentication on logout
                        .permitAll()) // Allow all users to access logout
                .addFilterBefore(new CustomFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
