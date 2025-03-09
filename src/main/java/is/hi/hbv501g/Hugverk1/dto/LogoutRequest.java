package is.hi.hbv501g.Hugverk1.dto;

public class LogoutRequest {
    private Long userId; // Optional: If needed to track who is logging out

    public LogoutRequest() {}

    public LogoutRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
