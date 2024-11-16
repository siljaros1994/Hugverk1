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
        MyAppUsers user = getLoggedInUser();
        if (user == null || !"donor".equalsIgnoreCase(user.getUserType())) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("userType", user.getUserType());

        List<Long> matchedUserIds = user.getMatchRecipients();
        List<RecipientProfile> matchedRecipients = recipientProfileService.getProfilesByUserIds(matchedUserIds);

        System.out.println("User in session: " + user);
        System.out.println("Matched Recipient Profile IDs: " + matchedUserIds);
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

        // Retrieve matched donor user IDs for this recipient
        List<Long> matchedUserIds = user.getMatchDonorsList();

        // Fetch Donor Profiles based on user_id
        List<DonorProfile> matchedDonors = donorProfileService.getProfilesByUserIds(matchedUserIds);

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
    public String approveMatch(@RequestParam("userId") Long userId, HttpSession session) {
        MyAppUsers user = getLoggedInUser();
        if (user == null || !"donor".equalsIgnoreCase(user.getUserType())) {
            return "redirect:/users/login";
        }

        myAppUserService.approveFavoriteAsMatch(user.getId(), userId); // Using userId for both
        System.out.println("Match approved successfully.");
        return "redirect:/match/donor/matches";
    }

    @PostMapping("/unmatch")
    public String unmatch(@RequestParam Long donorId, @RequestParam Long recipientId) {
        myAppUserService.removeMatch(donorId, recipientId);
        return "redirect:/match/donor/matches";
    }
}
