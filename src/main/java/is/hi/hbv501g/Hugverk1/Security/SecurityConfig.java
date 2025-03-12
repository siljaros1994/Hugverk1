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
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;  // Inject handler
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
                .csrf(csrf ->csrf.ignoringRequestMatchers("/api/**")) // Allow API calls without CSRF
                        //csrf.disable()) // Fully disable CSRF
                //.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints
                //)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //Change IF_REQUIRED to ALWAYS
                        .sessionFixation().migrateSession()
                        .maximumSessions(1).maxSessionsPreventsLogin(false)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/register", "/uploads/**", "/api/authenticate", "/css/**").permitAll()
                        .requestMatchers("/error").permitAll() // Allow all access to /error
                        .requestMatchers("/api/**", "/api/recipient/profile/**", "/api/donor/profile/**", "/api/recipient/profile/saveOrEdit").authenticated()
                        .requestMatchers("/users/login", "/users/register").permitAll() //start of inellij
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/admin/**", "/home/admin", "/donorlimits", "/delete/{username}", "/reports", "/history").hasRole("ADMIN")
                        .requestMatchers("/home/donor", "/donorprofile", "/donor/view/**", "/bookings/donor").authenticated()
                        .requestMatchers("/home/recipient", "/recipientprofile", "/recipient/view/**", "/bookings/recipient", "/api/recipient/favorite/**").authenticated()
                        //added "/api" for /favorite/recipient for authentication debugging
                        .requestMatchers("/messages/**", "/messages/{userType}/{id:[0-9]+}", "/dr").authenticated()
                        .requestMatchers("/match/donor/matches", "/match/recipient/matches", "/match/approveMatch", "/match/unmatch").authenticated()
                        .requestMatchers("/api/**").hasAnyRole("USER","ADMIN")//Ensures only logged-in users access API
                        // .requestMatchers("/api/**").authenticated() //authentication for favorites
                        //.requestMatchers("/api/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN") // Fix role issue
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

    //Favorite APIservice.kt
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) //Allow API calls
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Use Stateless Sessions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/register", "/uploads/**").permitAll()
                        .requestMatchers("/api/**").authenticated()  //Ensure API requires authentication
                )
                //.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) //Add JWT Filter
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(authenticationEntryPoint))
                .build();
    }

     */

}
