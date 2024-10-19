package is.hi.hbv501g.Hugverk1.Services;

import java.util.Optional;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Here we load the user data during the login process.

// Here we load the user data during the login process.
@Service
public class MyAppUserService implements UserDetailsService { // lodes user-specific data like username and password from the database when someone tries to log in.

    @Autowired
    private MyAppUserRepository repository;

    // Here we retrieve a user by their username from the database. It finds the user trying to log in
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyAppUsers> user = repository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}