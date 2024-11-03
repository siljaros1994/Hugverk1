package is.hi.hbv501g.Hugverk1.Persistence.Entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Here we are defining the user and maps it to a database table. It controls how user information
// (ID, username, email, password) is stored and retrieved from the database.
@Entity // lets the program know that this class represents a table in the database.
@Table(name = "MyAppUsers")
public class MyAppUsers implements UserDetails { // Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
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
    private String donorId;

    @Column(name = "recipient_id", unique = true, nullable = true)
    private String recipientId;

    @Column(nullable = false)
    private String userType; // Either donor or recipient.

    @Transient
    private String confirmPassword;

    // One-to-One relationship with DonorProfile like in gagnasafnsfræði
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DonorProfile donorProfile;

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

    public String getDonorId() {
        return donorId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
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

    // Utility methods for assigning IDs
    public void assignDonorId() {
        this.donorId = UUID.randomUUID().toString();
    }

    public void assignRecipientId() {
        this.recipientId = UUID.randomUUID().toString();
    }

    public String getFavoriteDonors() {
        return favoriteDonors;
    }
    public void setFavoriteDonors(String favoriteDonors) {
        this.favoriteDonors = favoriteDonors;
    }


}


