package is.hi.hbv501g.Hugverk1.Persistence.Entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

// Here we are defining the user and maps it to a database table. It controls how user information
// (ID, username, email, password) is stored and retrieved from the database.
// lets the program know that this class represents a table in the database.
@Entity
@Table(name = "MyAppUsers")
public class MyAppUsers implements UserDetails { // Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    // Here we store the user's username, email, and password, which will be saved in the MyAppUsers table.
    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(nullable = false)
    private String password;

    // Fields for donorId and recipientId
    @Column(name = "donor_id", unique = true, nullable = true)
    private Long donorId;

    @Column(name = "recipient_id", unique = true, nullable = true)
    private Long recipientId;

    @Column(nullable = false)
    private String userType; // Either donor or recipient.

    @Column(name = "favorite_donors", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String favoriteDonors = "";

    @Column(name = "matched_recipients")
    private String matchedRecipients;

    @Column(name = "matched_donors")
    private String matchedDonors;

    public List<Long> getFavoriteDonorsList() {
        if (favoriteDonors == null || favoriteDonors.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(favoriteDonors.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public void setFavoriteDonorsList(List<Long> donorIds) {
        if (donorIds == null || donorIds.isEmpty()) {
            this.favoriteDonors = "";
        } else {
            this.favoriteDonors = donorIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }

    public List<Long> getMatchRecipients() {
        if (matchedRecipients == null || matchedRecipients.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(matchedRecipients.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public void setMatchRecipients(List<Long> recipientIds) {
        if (recipientIds == null || recipientIds.isEmpty()) {
            this.matchedRecipients = "";
        } else {
            this.matchedRecipients = recipientIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }

    // Getter for matchedDonorsList
    public List<Long> getMatchDonorsList() {
        if (matchedDonors == null || matchedDonors.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(matchedDonors.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    // Setter for matchedDonorsList
    public void setMatchDonorsList(List<Long> donorIds) {
        if (donorIds == null || donorIds.isEmpty()) {
            this.matchedDonors = "";
        } else {
            this.matchedDonors = donorIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }

    public void addMatchedRecipient(Long recipientId) {
        List<Long> recipients = getMatchRecipients();
        if (!recipients.contains(recipientId)) {
            recipients.add(recipientId);
            setMatchRecipients(recipients);
        }
    }

    public void addMatchedDonor(Long donorId) {
        List<Long> matchedDonors = getMatchDonorsList();
        if (!matchedDonors.contains(donorId)) {
            matchedDonors.add(donorId);
            setMatchDonorsList(matchedDonors);
        }
    }

    @Transient
    private String confirmPassword;

    // One-to-One relationship with DonorProfile like in gagnasafnsfræði
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DonorProfile donorProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RecipientProfile recipientProfile;

    // Implementations for UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if ("admin".equalsIgnoreCase(userType)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if ("donor".equalsIgnoreCase(userType)) {
            return List.of(new SimpleGrantedAuthority("ROLE_DONOR"));
        } else if ("recipient".equalsIgnoreCase(userType)) {
            return List.of(new SimpleGrantedAuthority("ROLE_RECIPIENT"));
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Indicate the account is not expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Indicate the account is not locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Indicate the credentials are not expired
    }

    @Override
    public boolean isEnabled() {
        return true; // Indicate the account is enabled
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    // Getters and setters for donorProfile.
    public DonorProfile getDonorProfile() {
        return donorProfile;
    }

    public void setDonorProfile(DonorProfile donorProfile) {
        this.donorProfile = donorProfile;
    }

    public RecipientProfile getRecipientProfile() {
        return recipientProfile;
    }

    public void setRecipientProfile(RecipientProfile recipientProfile) {
        this.recipientProfile = recipientProfile;
    }

    public String getFavoriteDonors() {
        return favoriteDonors;
    }
    public void setFavoriteDonors(String favoriteDonors) {
        this.favoriteDonors = favoriteDonors;
    }

    public String getMatchedRecipients() {
        return matchedRecipients;
    }

    public void setMatchedRecipients(String matchedRecipients) {
        this.matchedRecipients = matchedRecipients;
    }

}
