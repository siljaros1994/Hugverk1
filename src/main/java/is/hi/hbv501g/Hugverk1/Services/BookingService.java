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
        DonorProfile donorProfile = donorProfileRepository.findByUserDonorId(donorId)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

        //if (donorProfile.getDonationsCompleted() >= donorProfile.getDonationLimit()) {
          //  throw new IllegalStateException("Donor has reached the donation limit.");
        //}
        Booking booking = new Booking();
        booking.setDonorId(donorId);
        booking.setRecipientId(recipientId);
        booking.setDate(date);
        booking.setTime(time);
        //donorProfile.incrementDonationsCompleted();
        donorProfileRepository.save(donorProfile);
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}