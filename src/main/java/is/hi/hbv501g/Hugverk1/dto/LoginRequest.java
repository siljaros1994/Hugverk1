package is.hi.hbv501g.Hugverk1.dto;

//class that we use to pass login credentials (username and password) in API requests, such as from Postman.
//It does not interact with our database.
public class LoginRequest {
    private String username;
    private String password;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}