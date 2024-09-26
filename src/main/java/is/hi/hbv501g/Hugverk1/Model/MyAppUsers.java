package is.hi.hbv501g.Hugverk1.Model;

import jakarta.persistence.*;

// Here we are defining the user and maps it to a database table. It controls how user information
// (ID, username, email, password) is stored and retrieved from the database.

@Entity // lets the program know that this class represents a table in the database.
@Table(name = "MyAppUsers")
public class MyAppUsers {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Here we store the user's username, email, and password, which will be saved in the MyAppUsers table.
    @Column(unique = true)
    private String username;

    @Column(name = "email")
    private String email;

    private String password;

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
}