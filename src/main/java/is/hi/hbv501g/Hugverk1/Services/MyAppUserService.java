package is.hi.hbv501g.Hugverk1.Services;

import java.util.Optional;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Here we load the user data during the login process.
public interface MyAppUserService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    void saveUser(MyAppUsers user);
    Optional<MyAppUsers> findByUsername(String username);
    boolean matchPassword(String rawPassword, String encodedPassword);
<<<<<<< Updated upstream
=======
    List<MyAppUsers> findAllUsers();
    //favorite
    void addFavoriteDonor(Long recipientId, Long donorId);
    List<Long> getFavoriteDonors(Long recipientId);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
>>>>>>> Stashed changes
}