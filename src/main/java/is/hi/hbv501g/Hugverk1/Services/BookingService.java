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
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    public Booking createBooking(Long donorId, Long recipientId, LocalDate date, LocalTime time) {
        //if (donorProfile.getDonationsCompleted() >= donorProfile.getDonationLimit()) {
          //  throw new IllegalStateException("Donor has reached the donation limit.");
        //}
        Booking booking = new Booking();
        booking.setDonorId(donorId);
        booking.setRecipientId(recipientId);
        booking.setDate(date);
        booking.setTime(time);
        //donorProfile.incrementDonationsCompleted();
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
        return bookingRepository.findByDonorIdAndConfirmedFalse(donorId);
    }

    public void confirmBooking(Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setConfirmed(true);
            bookingRepository.save(booking);
        }
    }

    public void cancelBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}