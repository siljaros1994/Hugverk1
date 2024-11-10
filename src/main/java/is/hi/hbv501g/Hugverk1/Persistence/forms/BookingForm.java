package is.hi.hbv501g.Hugverk1.Persistence.forms;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingForm {
    private Long donorId;
    private Long recipientId;
    private LocalDate date;
    private LocalTime time;

    // Getters and Setters
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
