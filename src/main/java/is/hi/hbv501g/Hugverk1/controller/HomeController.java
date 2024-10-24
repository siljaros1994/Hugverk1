package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/home/donor")
    public String donorHome(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        model.addAttribute("username", loggedInUser.getUsername());
        return "donorHome";  // Returns the donor-specific homepage
    }

    @GetMapping("/home/recipient")
    public String recipientHome(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        model.addAttribute("username", loggedInUser.getUsername());
        return "recipientHome";  // Returns the recipient-specific homepage
    }
}