package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller("customProfileController")
public class ProfileController {

    @GetMapping("/donorprofile")
    public String donorProfile(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        model.addAttribute("username", loggedInUser.getUsername());
        return "donorprofile";  // Return donorprofile.html
    }

    @GetMapping("/recipientprofile")
    public String recipientProfile(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        model.addAttribute("username", loggedInUser.getUsername());
        return "recipientprofile";  // Return recipientprofile.html
    }
}