package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;

@Controller
public class DonorHomeController {

    @Autowired
    private MyAppUserService myAppUserService;

    @GetMapping("/home/donor")
    public String donorHome(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        if (loggedInUser == null || !"donor".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }
        Long donorId = loggedInUser.getDonorId();
        List<MyAppUsers> recipientsWhoFavorited = myAppUserService.getRecipientsWhoFavoritedTheDonor(donorId);

        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("recipientsWhoFavorited", recipientsWhoFavorited);
        return "donorHome";  // Returns the donor-specific homepage
    }
}