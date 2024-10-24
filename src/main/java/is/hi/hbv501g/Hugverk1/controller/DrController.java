package is.hi.hbv501g.Hugverk1.controller;


import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DrController {

    @GetMapping("/dr")
    public String dr(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("username", loggedInUser.getUsername());
        } else {
            model.addAttribute("message", "Hello, welcome to our webpage!");
        }
        return "dr";  // Return home.html when /home is accessed
    }

}
