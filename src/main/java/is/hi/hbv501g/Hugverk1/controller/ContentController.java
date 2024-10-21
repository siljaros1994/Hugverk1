package is.hi.hbv501g.Hugverk1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;

import java.util.List;

@Controller
public class ContentController {

    @Autowired
    private MyAppUserService myAppUserService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }
    @GetMapping("/Home")
    public String home(){
        return "Home";
    }

    @GetMapping("/favorites-page")
    public String favorites(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        if (loggedInUser != null && "recipient".equalsIgnoreCase(loggedInUser.getUserType())) {
            Long recipientId = loggedInUser.getId();
            List<Long> favorites = myAppUserService.getFavoriteDonors(recipientId);
            model.addAttribute("favorites", favorites);
        }
        return "favorites";
    }

}