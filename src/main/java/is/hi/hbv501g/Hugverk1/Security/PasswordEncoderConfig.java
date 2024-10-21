package is.hi.hbv501g.Hugverk1.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    //  hashing the passwords using BCryptPasswordEncoder. This ensures that passwords are stored in a hashed format
    // in the database and not as plain text. The password is hashed before being saved to
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}