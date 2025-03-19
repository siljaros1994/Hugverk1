package is.hi.hbv501g.Hugverk1.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDTO {
    private Long id;
    private Long donorId;
    private Long recipientId;
    private LocalDate date;
    private LocalTime time;
    private boolean confirmed;
    private String status;

    // Constructor
    public BookingDTO(Long id, Long donorId, Long recipientId, LocalDate date, LocalTime time, boolean confirmed, String status) {
        this.id = id;
        this.donorId = donorId;
        this.recipientId = recipientId;
        this.date = date;
        this.time = time;
        this.confirmed = confirmed;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDonorId() { return donorId; }
    public void setDonorId(Long donorId) { this.donorId = donorId; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
