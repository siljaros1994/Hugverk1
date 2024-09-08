package is.hi.hbv501g.Hugverk1;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().permitAll()
                )
                .csrf((csrf) -> csrf.disable())  // Disable CSRF for development purposes
                .formLogin((form) -> form.disable());  // Disable the login form
        return http.build();
    }
}
