package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminDonorLimitsController {

    @Autowired
    private DonorProfileService donorProfileService;

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
