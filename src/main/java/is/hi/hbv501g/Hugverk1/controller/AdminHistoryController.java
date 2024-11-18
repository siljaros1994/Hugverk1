package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Booking;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminHistoryController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    public AdminHistoryController(BookingRepository bookingRepository){
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/history")
    public String adminHistory(Model model) {
        List<Booking> bookings = bookingRepository.findAll();
        model.addAttribute("bookings", bookings);
        return "adminHistory";
    }

}
