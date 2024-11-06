package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Booking;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public Booking createBooking(Long donorId, Long recipientId, LocalDate date, LocalTime time) {
        Booking booking = new Booking();
        booking.setDonorId(donorId);
        booking.setRecipientId(recipientId);
        booking.setDate(date);
        booking.setTime(time);
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}