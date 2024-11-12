package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.forms.BookingForm;
import is.hi.hbv501g.Hugverk1.Services.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/bookings")
@SessionAttributes("user")
public class BookingController extends BaseController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/recipient")
    public String showRecipientBookingPage(Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("user");

        if (loggedInUser == null || !"recipient".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("bookingForm", new BookingForm());
        model.addAttribute("bookings", bookingService.getBookingsByRecipientId(loggedInUser.getId()));
        return "booking_recipient";
    }

    // Recipient can create booking time.
    @PostMapping("/book")
    public String createBooking(@ModelAttribute BookingForm bookingForm) {
        bookingService.createBooking(bookingForm.getDonorId(), bookingForm.getRecipientId(),
                bookingForm.getDate(), bookingForm.getTime());
        return "redirect:/bookings/recipient";
    }

    // Here the donor can see pending bookings for donation
    @GetMapping("/donor")
    public String showDonorBookingPage(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("user");

        if (loggedInUser == null || !"donor".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("pendingBookings", bookingService.getPendingBookingsForDonor(loggedInUser.getId()));
        return "booking_donor";
    }

    // Donor can confirm a booking
    @PostMapping("/confirm/{id}")
    public String confirmBooking(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return "redirect:/bookings/donor";
    }

    // Recipient can cancel a booking.
    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return "redirect:/bookings/recipient";
    }
}
