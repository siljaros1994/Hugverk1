package is.hi.hbv501g.Hugverk1.Persistence.forms;

public class MessageForm {
    private String text;
    private Long receiverId;
    private Long senderId;

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long recipientId) {
        this.receiverId = recipientId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
}