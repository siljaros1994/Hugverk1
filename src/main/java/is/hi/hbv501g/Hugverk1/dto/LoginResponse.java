package is.hi.hbv501g.Hugverk1.dto;

public class LoginResponse {
    private String message;
    private long userId;
    private String userType;
    private String username;

    public LoginResponse(String message, long userId, String userType, String userName) {
        this.message = message;
        this.userId = userId;
        this.userType = userType;
        this.username = userName;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }
}