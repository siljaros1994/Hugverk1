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
    Optional<MyAppUsers> findById(Long id);
    boolean matchPassword(String rawPassword, String encodedPassword);
    List<MyAppUsers> findAllUsers();

    void createAdminUser();
    void addFavoriteDonor(Long userId, Long favoriteUserId);
    List<Long> getFavoriteDonors(Long userId);
    List<MyAppUsers> getRecipientsWhoFavoritedTheDonor(Long userId);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    void removeMatch(Long userId, Long matchedUserId);
    void addMatchRecipient(Long userId, Long matchedUserId);
    List<Long> getMatchRecipients(Long userId);
    List<Long> getMatchesForRecipient(Long userId);
    void approveFavoriteAsMatch(Long userId, Long matchedUserId);
    List<MyAppUsers> getMatchedUsers(Long userId, String userType);
    void removeFavoriteDonor(Long userId, Long donorId);

}
