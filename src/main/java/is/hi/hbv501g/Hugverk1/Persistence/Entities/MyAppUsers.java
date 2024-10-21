package is.hi.hbv501g.Hugverk1.Persistence.Entities;

import jakarta.persistence.*;
import java.util.UUID;

// Here we are defining the user and maps it to a database table. It controls how user information
// (ID, username, email, password) is stored and retrieved from the database.

@Entity // lets the program know that this class represents a table in the database.
@Table(name = "MyAppUsers")
public class MyAppUsers {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String userType;

    //var að bæta við
    @Column(name = "favorite_donors")
    private String favoriteDonors; //


    @Transient
    private String confirmPassword;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    // Utility methods for assigning IDs
    public void assignDonorId() {
        this.donorId = UUID.randomUUID().toString();
    }

    public void assignRecipientId() {
        this.recipientId = UUID.randomUUID().toString();
    }

    //favorites getters and setters
    public String getFavoriteDonors() {
        return favoriteDonors;
    }

    public void setFavoriteDonors(String favoriteDonors) {
        this.favoriteDonors = favoriteDonors;
    }
}