package is.hi.hbv501g.Hugverk1.Services.Implementation;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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

    public void createAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            MyAppUsers user = new MyAppUsers();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("password"));  // Encode the password here
            user.setUserType("admin");
            userRepository.save(user);
            System.out.println("Admin user created with username 'admin' and encoded password.");
        } else {
            System.out.println("Admin user already exists.");
        }
    }

    @PostConstruct
    public void initializeAdminUser() {
        System.out.println("Initializing admin user...");
        createAdminUser();
    }

    @Override
    public Optional<MyAppUsers> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void saveUser(MyAppUsers user) {
        // Encode the password before saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword())); // The password is hashed
        if ("donor".equalsIgnoreCase(user.getUserType()) && user.getDonorId() == null) {
            user.assignDonorId(); // Assign a unique donorId
        } else if ("recipient".equalsIgnoreCase(user.getUserType()) && user.getRecipientId() == null) {
            user.assignRecipientId(); // Assign a unique recipientId
        }
        userRepository.save(user); // Save the user to the database
    }

    // Find user by username
    @Override
    public Optional<MyAppUsers> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Match raw password with the hased password
    @Override
    public boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public List<MyAppUsers> findAllUsers() {
        List<MyAppUsers> users = userRepository.findAll();
        if (users == null || users.isEmpty()) {
            System.out.println("No users found in the database.");
            return Collections.emptyList();  // Return an empty list
        }
        return users;
    }
}
