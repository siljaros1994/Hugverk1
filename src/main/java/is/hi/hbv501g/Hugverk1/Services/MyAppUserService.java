package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

// Here we load the user data during the login process.
public interface MyAppUserService extends UserDetailsService {
    void saveUser(MyAppUsers user);
    Optional<MyAppUsers> findByUsername(String username);
    Optional<MyAppUsers> findById(String id);
    boolean matchPassword(String rawPassword, String encodedPassword);
    List<MyAppUsers> findAllUsers();

    void createAdminUser();
    void addFavoriteDonor(String recipientId, String donorId);
    List<String> getFavoriteDonors(String recipientId);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
