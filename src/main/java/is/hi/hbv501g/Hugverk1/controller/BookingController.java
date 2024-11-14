package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Booking;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.forms.BookingForm;
import is.hi.hbv501g.Hugverk1.Services.BookingService;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bookings")
@SessionAttributes("user")
public class BookingController extends BaseController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private DonorProfileService donorProfileService;

    @GetMapping("/recipient")
    public String showRecipientBookingPage(Model model, HttpSession session) {
        MyAppUsers loggedInUser = getLoggedInUser();
        if (loggedInUser == null || !"recipient".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }
        model.addAttribute("user", loggedInUser);

        // Log the recipient's matched donor IDs
        List<Long> matchedUserIds = loggedInUser.getMatchDonorsList();
        System.out.println("Matched Donor User IDs for Recipient ID " + loggedInUser.getId() + ": " + matchedUserIds);

        // Retrieve donor profiles based on matched user IDs
        List<DonorProfile> matchedDonors = donorProfileService.getProfilesByUserIds(matchedUserIds);

        // Log the donor profiles
        System.out.println("Matched Donor Profiles: " + matchedDonors);

        model.addAttribute("matchedDonors", matchedDonors); // Pass matched donors to the model
        model.addAttribute("bookingForm", new BookingForm()); // Create a new booking form
        model.addAttribute("currentAppointments", bookingService.getConfirmedBookingsForRecipient(loggedInUser.getId()));

        return "booking_recipient"; // Render the recipient booking page
    }

    // Recipient can create booking time.
    @PostMapping("/book")
    public String createBooking(@ModelAttribute BookingForm bookingForm, HttpSession session) {
        MyAppUsers loggedInUser = getLoggedInUser();
        if (loggedInUser == null || !"recipient".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }
        bookingService.createBooking(bookingForm.getDonorId(), loggedInUser.getId(),
                bookingForm.getDate(), bookingForm.getTime());
        return "redirect:/bookings/recipient";
    }


    // Here the donor can see pending bookings for donation
    @GetMapping("/donor")
    public String showDonorBookingPage(Model model, HttpSession session) {
        MyAppUsers loggedInUser = getLoggedInUser();
        if (loggedInUser == null || !"donor".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }
        model.addAttribute("user", loggedInUser);

        // Shows all pending bookings for the donor.
        List<Booking> pendingBookings = bookingService.getPendingBookingsForDonor(loggedInUser.getDonorId());

        model.addAttribute("pendingBookings", pendingBookings);
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
