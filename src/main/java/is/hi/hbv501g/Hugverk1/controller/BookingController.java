package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.forms.BookingForm;
import is.hi.hbv501g.Hugverk1.Services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public String showBookingPage(Model model) {
        model.addAttribute("bookingForm", new BookingForm());
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "booking";
    }

    @PostMapping("/book")
    public String createBooking(@ModelAttribute BookingForm bookingForm) {
        bookingService.createBooking(bookingForm.getDonorId(), bookingForm.getRecipientId(),
                bookingForm.getDate(), bookingForm.getTime());
        return "redirect:/bookings";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return "redirect:/bookings";
    }
}
