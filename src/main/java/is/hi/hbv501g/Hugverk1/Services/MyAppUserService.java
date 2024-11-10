package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

// Here we load the user data during the login process.
public interface MyAppUserService extends UserDetailsService {
    static List<Long> getMatchRecipients(Long donorId) {
    }

    void saveUser(MyAppUsers user);
    Optional<MyAppUsers> findByUsername(String username);
    Optional<MyAppUsers> findById(Long id);
    boolean matchPassword(String rawPassword, String encodedPassword);
    List<MyAppUsers> findAllUsers();

    void createAdminUser();
    void addFavoriteDonor(Long recipientId, Long donorId);
    List<Long> getFavoriteDonors(Long recipientId);
    List<MyAppUsers> getRecipientsWhoFavoritedTheDonor(Long donorId);
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    void addMatchRecipient();

    void addMatchRecipient(Long donorId, Long recipientId);
}
