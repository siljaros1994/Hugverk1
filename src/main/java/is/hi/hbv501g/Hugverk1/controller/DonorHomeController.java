package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Controller
public class DonorHomeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(DonorHomeController.class);

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    private RecipientProfileService recipientProfileService;

    @GetMapping("/home/donor")
    public String donorHome(Model model, HttpSession session) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("user");
        if (loggedInUser == null || !"donor".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }

        Long donorId = loggedInUser.getDonorId();
        List<MyAppUsers> recipientsWhoFavorited = myAppUserService.getRecipientsWhoFavoritedTheDonor(donorId);

        model.addAttribute("user", loggedInUser);
        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("recipientsWhoFavorited", recipientsWhoFavorited);

        return "donorHome";  // Returns the donor-specific homepage
    }

    @GetMapping("/donor/view/{recipientId}")
    public String viewRecipientProfile(@PathVariable Long recipientId, Model model, HttpSession session) {
        MyAppUsers user = (MyAppUsers) session.getAttribute("user");

        if (user == null) {
            return "redirect:/users/login";
        }

        Optional<RecipientProfile> recipientProfile = recipientProfileService.findByProfileId(recipientId);

        if (recipientProfile.isPresent()) {
            model.addAttribute("user", user);
            model.addAttribute("recipientProfile", recipientProfile.get());
            logger.info("Displaying profile for recipient with profileId: {}", recipientId);
            return "recipientPage";
        } else {
            logger.warn("Recipient profile with profileId {} not found. Redirecting to donor home.", recipientId);
            return "redirect:/home/donor";
        }
    }
}