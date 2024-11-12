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
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired  // Inject MyAppUserRepository
    private MyAppUserRepository userRepository;


    @GetMapping("/donor/matches")
    public String donorMatches(Model model) {
        MyAppUsers user = getLoggedInUser();
        if (user == null || !"donor".equalsIgnoreCase(user.getUserType())) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("userType", user.getUserType());

        // Get list of user IDs that are matched with the donor
        List<Long> matchedUserIds = user.getMatchRecipients();

        // Retrieve recipient profiles by the recipient_profile_id instead of user_id
        List<Long> matchedRecipientProfileIds = matchedUserIds.stream()
                .map(id -> userRepository.findById(id)
                        .map(MyAppUsers::getRecipientProfile)
                        .map(RecipientProfile::getRecipientProfileId)
                        .orElse(null))
                .filter(Objects::nonNull)  // Remove nulls if any IDs were not found
                .collect(Collectors.toList());

        // Fetch profiles using the corrected recipient_profile_ids
        List<RecipientProfile> matchedRecipients = recipientProfileService.getProfilesByIds(matchedRecipientProfileIds);

        System.out.println("User in session: " + user);
        System.out.println("Matched Recipient Profile IDs: " + matchedRecipientProfileIds);
        System.out.println("Matched Recipients: " + matchedRecipients);

        model.addAttribute("matches", matchedRecipients);
        model.addAttribute("user", user);
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

        // Retrieve the donor IDs from donors who have the recipient's ID in their matchedRecipients
        List<Long> matchedDonorIds = userRepository.findAll().stream()
                .filter(donor -> donor.getMatchRecipients().contains(user.getId()))
                .map(MyAppUsers::getDonorId)
                .collect(Collectors.toList());

        // Fetch Donor Profiles based on the matched donor IDs
        List<DonorProfile> matchedDonors = donorProfileService.getProfilesByIds(matchedDonorIds);

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
    public String approveMatch(@RequestParam Long recipientId, HttpSession session) {
        MyAppUsers user = getLoggedInUser();
        if (user == null || user.getDonorId() == null) {
            return "redirect:/users/login";
        }

        myAppUserService.approveFavoriteAsMatch(user.getDonorId(), recipientId);
        System.out.println("Match approved successfully.");
        return "redirect:/match/donor/matches";
    }

    @PostMapping("/unmatch")
    public String unmatch(@RequestParam Long donorId, @RequestParam Long recipientId) {
        myAppUserService.removeMatch(donorId, recipientId);
        return "redirect:/match/donor/matches";
    }
}
