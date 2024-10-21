package is.hi.hbv501g.Hugverk1.Services;

import java.util.List;
import java.util.Optional;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

// Here we load the user data during the login process.
public interface MyAppUserService extends UserDetailsService {
    void saveUser(MyAppUsers user);
    Optional<MyAppUsers> findByUsername(String username);
    Optional<MyAppUsers> findById(Long id);
    boolean matchPassword(String rawPassword, String encodedPassword);
    List<MyAppUsers> findAllUsers();

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}