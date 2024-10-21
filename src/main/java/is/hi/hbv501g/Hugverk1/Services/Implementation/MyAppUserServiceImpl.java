package is.hi.hbv501g.Hugverk1.Services.Implementation;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
<<<<<<< Updated upstream
=======
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;


import java.util.Collections;
import java.util.List;
>>>>>>> Stashed changes
import java.util.Optional;

@Service
public class MyAppUserServiceImpl implements MyAppUserService, UserDetailsService {

    private final MyAppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MyAppUserServiceImpl(MyAppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyAppUsers> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            MyAppUsers userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Override
    public void saveUser(MyAppUsers user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encode the password
        if ("donor".equalsIgnoreCase(user.getUserType())) {
            user.assignDonorId();
        } else if ("recipient".equalsIgnoreCase(user.getUserType())) {
            user.assignRecipientId();
        }
        userRepository.save(user);
    }

    @Override
    public Optional<MyAppUsers> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
<<<<<<< Updated upstream
=======

    @Override
    public List<MyAppUsers> findAllUsers() {
        List<MyAppUsers> users = userRepository.findAll();
        if (users == null || users.isEmpty()) {
            System.out.println("No users found in the database.");
            return Collections.emptyList();  // Return an empty list
        }
        return users;
    }

    //Favorites:
    @Override
    public void addFavoriteDonor(Long recipientId, Long donorId) {
        MyAppUsers recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        String currentFavorites = recipient.getFavoriteDonors();

        // Add the new donor ID
        if (currentFavorites == null || currentFavorites.isEmpty()) {
            currentFavorites = donorId.toString();
        } else {
            currentFavorites += "," + donorId;
        }
        // Update recipient entity
        recipient.setFavoriteDonors(currentFavorites);

        // Save to database
        userRepository.save(recipient);
    }
    @Override
    public List<Long> getFavoriteDonors(Long recipientId) {
        MyAppUsers recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        String favoriteDonors = recipient.getFavoriteDonors();
        if (favoriteDonors == null || favoriteDonors.isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(favoriteDonors.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
>>>>>>> Stashed changes
}