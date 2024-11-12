package is.hi.hbv501g.Hugverk1.Services.Implementation;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import jakarta.annotation.PostConstruct;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    @Override
    public void addFavoriteDonor(Long recipientId, Long donorId) {
        MyAppUsers recipient = userRepository.findByRecipientId(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        String currentFavorites = recipient.getFavoriteDonors();

        if (currentFavorites == null || !Arrays.asList(currentFavorites.split(",")).contains(donorId.toString())) {
        // Add the new donor ID
        if (currentFavorites == null || currentFavorites.isEmpty()) {
            currentFavorites = donorId.toString();
        } else {
            currentFavorites += "," + donorId;
        }
        // Update recipient entity
        System.out.println("Updated Favorite Donors: " + currentFavorites);
        recipient.setFavoriteDonors(currentFavorites);
        // Save to database
        userRepository.save(recipient);
        System.out.println("Favorite donor added successfully: " + donorId);
        } else {
            System.out.println("Donor ID already in favorites: " + donorId);
        }
    }

    @Override
    public List<Long> getFavoriteDonors(Long recipientId) {
        MyAppUsers recipient = userRepository.findByRecipientId(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        String favoriteDonors = recipient.getFavoriteDonors();
        return (favoriteDonors == null || favoriteDonors.isEmpty()) ? new ArrayList<>() :
                Arrays.stream(favoriteDonors.split(",")).map(Long::parseLong).collect(Collectors.toList());
    }

    // Here we fetch all recipients who have this donor's ID in their favorites list
    @Override
    public List<MyAppUsers> getRecipientsWhoFavoritedTheDonor(Long donorId) {
        List<MyAppUsers> recipients = userRepository.findRecipientsWhoFavoritedDonor(donorId);
        recipients.forEach(recipient -> Hibernate.initialize(recipient.getRecipientProfile()));
        return recipients;
    }

    @Override
    public void approveFavoriteAsMatch(Long donorId, Long recipientId) {
        System.out.println("Attempting to approve match for Donor ID: " + donorId + " with Recipient ID: " + recipientId);

        // Find and update the donor’s matched recipients
        MyAppUsers donor = userRepository.findByDonorId(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        List<Long> matchedRecipients = donor.getMatchRecipients();
        if (!matchedRecipients.contains(recipientId)) {
            matchedRecipients.add(recipientId);
            donor.setMatchRecipients(matchedRecipients);
            userRepository.save(donor);
            System.out.println("Match approved: Donor ID " + donorId + " with Recipient ID " + recipientId);
        } else {
            System.out.println("Recipient ID " + recipientId + " is already matched with Donor ID " + donorId);
        }

        // Update the recipient’s matched donors directly without fetching the recipient
        userRepository.findByRecipientId(recipientId).ifPresent(recipient -> {
            List<Long> matchedDonors = recipient.getMatchDonorsList();
            if (!matchedDonors.contains(donorId)) {
                matchedDonors.add(donorId);
                recipient.setMatchDonorsList(matchedDonors);
                userRepository.save(recipient);
                System.out.println("Match updated for Recipient ID " + recipientId + " with Donor ID " + donorId);
            }
        });
    }

    public void approveMatch(Long donorId, Long recipientId) {
        MyAppUsers donor = userRepository.findByDonorId(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        donor.addMatchedDonor(recipientId);
        userRepository.save(donor); // Save updated donor to persist the matched donor
    }


    @Override
    public List<Long> getMatchRecipients(Long donorId) {
        MyAppUsers donor = userRepository.findByDonorId(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        return donor.getMatchRecipients();
    }

    @Override
    public void addMatchRecipient(Long donorId, Long recipientId) {
        MyAppUsers donor = userRepository.findByDonorId(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        donor.addMatchedRecipient(recipientId);
        userRepository.save(donor);
    }

    @Override
    public void removeMatch(Long donorId, Long recipientId) {
        MyAppUsers donor = userRepository.findByDonorId(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        List<Long> updatedMatches = donor.getMatchRecipients().stream()
                .filter(id -> !id.equals(recipientId))
                .collect(Collectors.toList());
        donor.setMatchRecipients(updatedMatches);
        userRepository.save(donor);
        System.out.println("Removed match: Donor ID " + donorId + " with Recipient ID " + recipientId);
    }

    @Override
    public List<Long> getMatchesForRecipient(Long recipientId) {
        MyAppUsers recipient = userRepository.findByRecipientId(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        return recipient.getFavoriteDonorsList();
    }
}