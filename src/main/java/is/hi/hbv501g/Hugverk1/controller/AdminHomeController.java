package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;

@Controller
public class AdminHomeController {

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    private DonorProfileService donorProfileService;

    @GetMapping("/home/admin")
    public String adminHome(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        if (loggedInUser == null || !"admin".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }
        Long donorId = loggedInUser.getDonorId();
        List<MyAppUsers> recipientsWhoFavorited = myAppUserService.getRecipientsWhoFavoritedTheDonor(donorId);

        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("recipientsWhoFavorited", recipientsWhoFavorited);
        return "adminHome";
    }
    @GetMapping("/donorlimits")
    public String viewDonorLimits(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        if (loggedInUser == null || !"admin".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }

        List<DonorProfile> allDonors = donorProfileService.getAllDonors();
        model.addAttribute("allDonors", allDonors);

        return "donorLimits";
    }
}
