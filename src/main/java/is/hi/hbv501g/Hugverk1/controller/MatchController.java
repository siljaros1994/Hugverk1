package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
@RequestMapping("/match")
@SessionAttributes("user")
public class MatchController extends BaseController {

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    private DonorProfileService donorProfileService;

    @Autowired
    private RecipientProfileService recipientProfileService;

    @Autowired
    private MyAppUserRepository userRepository;


    @GetMapping("/donor/matches")
    public String donorMatches(Model model) {
        MyAppUsers loggedInUser = getLoggedInUser();
        if (loggedInUser == null || !"donor".equalsIgnoreCase(loggedInUser.getUserType())) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("userType", loggedInUser.getUserType());

        // Here we use the loggedInUser to get the matched recipient IDs
        List<Long> matchedRecipientIds = loggedInUser.getMatchRecipients();
        List<RecipientProfile> matchedRecipients = recipientProfileService.getProfilesByUserIds(matchedRecipientIds);

        System.out.println("User in session: " + loggedInUser);
        System.out.println("Matched Recipient Profile IDs: " + matchedRecipientIds);
        System.out.println("Matched Recipients: " + matchedRecipients);

        model.addAttribute("matches", matchedRecipients);
        return "donorMatchesPage";
    }

    @GetMapping("/recipient/matches")
    public String getRecipientMatches(Model model) {
        MyAppUsers user = getLoggedInUser();
        if (user == null || !"recipient".equalsIgnoreCase(user.getUserType())) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("userType", user.getUserType());
        System.out.println("User in session: " + user);

        // Here we also use user to get the matched donor IDs
        List<Long> matchedDonorIds = user.getMatchDonorsList();
        List<DonorProfile> matchedDonors = donorProfileService.getProfilesByUserIds(matchedDonorIds);

        model.addAttribute("matches", matchedDonors);
        return "recipientMatchesPage";
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMatchRecipient(@RequestParam Long donorId, @RequestParam Long recipientId) {
        try {
            myAppUserService.addMatchRecipient(donorId, recipientId);
            return ResponseEntity.ok("Recipient added to matches");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/approveMatch")
    public String approveMatch(@RequestParam("recipientId") Long recipientId, HttpSession session) {
        MyAppUsers donor = getLoggedInUser();
        if (donor == null || !"donor".equalsIgnoreCase(donor.getUserType())) {
            return "redirect:/users/login";
        }

        myAppUserService.approveFavoriteAsMatch(donor.getId(), recipientId);
        System.out.println("Match approved: Donor ID " + donor.getId() + " with Recipient ID " + recipientId);
        return "redirect:/match/donor/matches";
    }

    @PostMapping("/unmatch")
    public String unmatch(@RequestParam Long donorId, @RequestParam Long recipientId) {
        myAppUserService.removeMatch(donorId, recipientId);
        return "redirect:/match/donor/matches";
    }
}
