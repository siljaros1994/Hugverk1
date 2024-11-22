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

        List<Long> favoriteDonors = recipient.getFavoriteDonorsList();
        if (!favoriteDonors.contains(donorId)) {
            favoriteDonors.add(donorId);
            recipient.setFavoriteDonorsList(favoriteDonors);
            userRepository.save(recipient);
            System.out.println("Donor ID " + donorId + " added to recipient ID " + recipientId + "'s favorites.");
        } else {
            System.out.println("Donor ID " + donorId + " is already in recipient ID " + recipientId + "'s favorites.");
        }
    }

    @Override
    public List<Long> getFavoriteDonors(Long recipientId) {
        MyAppUsers recipient = userRepository.findByRecipientId(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        return recipient.getFavoriteDonorsList();
    }

    // Here we fetch all recipients who have this donor's ID in their favorites list
    @Override
    public List<MyAppUsers> getRecipientsWhoFavoritedTheDonor(Long userId) {
        List<MyAppUsers> recipients = userRepository.findRecipientsWhoFavoritedDonor(userId);
        recipients.forEach(recipient -> Hibernate.initialize(recipient.getRecipientProfile()));
        return recipients;
    }

    @Override
    public void removeFavoriteDonor(Long userId, Long donorId) {
        MyAppUsers recipient = userRepository.findByRecipientId(userId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        String currentFavorites = recipient.getFavoriteDonors();

        if (currentFavorites != null && !currentFavorites.isEmpty()) {
            List<String> favoriteList = new ArrayList<>(Arrays.asList(currentFavorites.split(",")));

            if (favoriteList.contains(donorId.toString())) {
                favoriteList.remove(donorId.toString());
                String updatedFavorites = String.join(",", favoriteList);

                // Here we update recipient entity
                recipient.setFavoriteDonors(updatedFavorites);
                userRepository.save(recipient);
                System.out.println("Favorite donor removed successfully: " + donorId);
            } else {
                System.out.println("Donor ID not found in favorites: " + donorId);
            }
        } else {
            System.out.println("No favorites to remove.");
        }
    }


    @Override
    public void approveFavoriteAsMatch(Long donorId, Long recipientId) {
        System.out.println("Approving match: Donor ID " + donorId + " with Recipient ID " + recipientId);

        // Fetch the donor and recipient
        MyAppUsers donor = userRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        MyAppUsers recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        // Update donor's matchedRecipients (recipient IDs)
        List<Long> matchedRecipients = donor.getMatchRecipients();
        if (!matchedRecipients.contains(recipientId)) {
            matchedRecipients.add(recipientId);
            donor.setMatchRecipients(matchedRecipients);
            userRepository.save(donor);
            System.out.println("Donor's matched recipients updated: " + matchedRecipients);
        }

        // Update recipient's matchedDonors (donor IDs)
        List<Long> matchedDonors = recipient.getMatchDonorsList();
        if (!matchedDonors.contains(donorId)) {
            matchedDonors.add(donorId);
            recipient.setMatchDonorsList(matchedDonors);
            userRepository.save(recipient);
            System.out.println("Recipient's matched donors updated: " + matchedDonors);
        }
    }

    @Override
    public List<MyAppUsers> getMatchedUsers(Long userId, String userType) {
        MyAppUsers user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Determine matched user IDs based on userType
        List<Long> matchedUserIds = "recipient".equalsIgnoreCase(userType)
                ? user.getMatchDonorsList()
                : user.getMatchRecipients();

        if (matchedUserIds == null || matchedUserIds.isEmpty()) {
            System.out.println("No matches found for user ID: " + userId);
            return Collections.emptyList();
        }

        // Fetch users for matched IDs and filter by user type
        return userRepository.findAllById(matchedUserIds).stream()
                .filter(java.util.Objects::nonNull) // Exclude null matches
                .filter(matchedUser -> {
                    if ("recipient".equalsIgnoreCase(userType)) {
                        return "donor".equalsIgnoreCase(matchedUser.getUserType());
                    } else if ("donor".equalsIgnoreCase(userType)) {
                        return "recipient".equalsIgnoreCase(matchedUser.getUserType());
                    }
                    return false;
                })
                .peek(matchedUser -> System.out.println("Matched user: " + matchedUser.getId() + ", Type: " + matchedUser.getUserType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getMatchRecipients(Long userId) {
        MyAppUsers donor = userRepository.findByDonorId(userId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        return donor.getMatchRecipients();
    }

    @Override
    public void addMatchRecipient(Long userId, Long matchedUserId) {
        MyAppUsers donor = userRepository.findByDonorId(userId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        donor.addMatchedRecipient(matchedUserId);
        userRepository.save(donor);
    }

    @Override
    public void removeMatch(Long donorId, Long recipientId) {
        MyAppUsers donor = userRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        List<Long> updatedDonorMatches = donor.getMatchRecipients().stream()
                .filter(id -> !id.equals(recipientId))
                .collect(Collectors.toList());
        donor.setMatchRecipients(updatedDonorMatches.isEmpty() ? null : updatedDonorMatches);
        userRepository.save(donor);

        MyAppUsers recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        List<Long> updatedRecipientMatches = recipient.getMatchDonorsList().stream()
                .filter(id -> !id.equals(donorId))
                .collect(Collectors.toList());
        recipient.setMatchDonorsList(updatedRecipientMatches.isEmpty() ? null : updatedRecipientMatches);
        userRepository.save(recipient);

        System.out.println("Removed match: Donor ID " + donorId + " with Recipient ID " + recipientId);
    }


    @Override
    public List<Long> getMatchesForRecipient(Long userId) {
        MyAppUsers recipient = userRepository.findByRecipientId(userId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        return recipient.getFavoriteDonorsList();
    }
}