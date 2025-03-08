package is.hi.hbv501g.Hugverk1.dto;

public class UserDTO {
    private Long id;
    private String username;
    private String userType;

    public UserDTO(Long id, String username, String userType) {
        this.id = id;
        this.username = username;
        this.userType = userType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}

