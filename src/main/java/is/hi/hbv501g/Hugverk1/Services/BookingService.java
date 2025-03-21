package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Booking;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.BookingRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.DonorProfileRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    public Booking createBooking(Long donorId, Long recipientId, LocalDate date, LocalTime time) {
        Booking booking = new Booking();
        booking.setDonorId(donorId);
        booking.setRecipientId(recipientId);
        booking.setDate(date);
        booking.setTime(time);
        booking.setConfirmed(false);
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByRecipientId(Long recipientId) {
        return bookingRepository.findByRecipientId(recipientId);
    }

    public List<Booking> getPendingBookingsForDonor(Long donorId) {
        List<Booking> pendingBookings = bookingRepository.findByDonorIdAndConfirmedFalse(donorId);
        System.out.println("Pending bookings for Donor ID " + donorId + ": " + pendingBookings);
        return pendingBookings;
    }

    public List<Booking> getConfirmedBookingsForDonor(Long donorId) {
        return bookingRepository.findByDonorIdAndConfirmedTrue(donorId);
    }

    public List<Booking> getConfirmedBookingsForRecipient(Long recipientId) {
        return bookingRepository.findByRecipientIdAndConfirmedTrue(recipientId);
    }

    public void confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        DonorProfile donorProfile = donorProfileRepository.findByUserId(booking.getDonorId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));
        if (donorProfile.getDonationsCompleted() >= donorProfile.getDonationLimit()) {
            throw new IllegalStateException("Donor has reached their donation limit.");
        }
        booking.setConfirmed(true);
        booking.setStatus("Booked");
        bookingRepository.save(booking);
        donorProfile.incrementDonationsCompleted();
        donorProfileRepository.save(donorProfile);
    }


public void cancelBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}