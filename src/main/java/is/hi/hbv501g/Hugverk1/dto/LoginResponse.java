package is.hi.hbv501g.Hugverk1.dto;

public class LoginResponse {
    private String message;
    private long userId;
    private String userType;

    public LoginResponse(String message, long userId, String userType) {
        this.message = message;
        this.userId = userId;
        this.userType = userType;
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
}