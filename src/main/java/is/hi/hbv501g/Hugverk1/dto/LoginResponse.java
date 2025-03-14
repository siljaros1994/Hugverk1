package is.hi.hbv501g.Hugverk1.dto;

public class LoginResponse {
    private String message;
    private long userId;
    private String userType;
    private String username;
    private Long donorId;
    private Long recipientId;

    // Constructor for donors
    public LoginResponse(String message, long userId, String userType, String username, Long donorId) {
        this.message = message;
        this.userId = userId;
        this.userType = userType;
        this.username = username;
        this.donorId = donorId;
        this.recipientId = null;
    }

    // Constructor for recipients
    public LoginResponse(String message, long userId, String userType, String username, Long recipientId, boolean isRecipient) {
        this.message = message;
        this.userId = userId;
        this.userType = userType;
        this.username = username;
        this.recipientId = recipientId;
        this.donorId = null;
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
}