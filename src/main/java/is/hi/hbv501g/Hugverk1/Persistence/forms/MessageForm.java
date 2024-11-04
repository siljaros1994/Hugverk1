package is.hi.hbv501g.Hugverk1.Persistence.forms;

public class MessageForm {
    private String text;
    private String receiverId;

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String recipientId) {
        this.receiverId = recipientId;
    }
}