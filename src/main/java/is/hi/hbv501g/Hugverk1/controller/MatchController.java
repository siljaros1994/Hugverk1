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
public class  MatchController extends BaseController {

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

        // Here we fetch the latest user data
        MyAppUsers refreshedUser = myAppUserService.findById(loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", refreshedUser);
        model.addAttribute("userType", refreshedUser.getUserType());

        // Here we use the loggedInUser to get the matched recipient IDs
        List<Long> matchedRecipientIds = refreshedUser.getMatchRecipients();
        List<RecipientProfile> matchedRecipients = recipientProfileService.getProfilesByUserIds(matchedRecipientIds).stream()
                .filter(Objects::nonNull)
                .filter(recipient -> recipient.getUser() != null)
                .collect(Collectors.toList());

        System.out.println("User in session: " + refreshedUser);
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

        // Here we approve the match
        myAppUserService.approveFavoriteAsMatch(donor.getId(), recipientId);

        // Here we refresh the session with updated user data so we dont have to logout and login again to update.
        MyAppUsers updatedDonor = myAppUserService.findById(donor.getId())
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        session.setAttribute("user", updatedDonor);

        System.out.println("Match approved: Donor ID " + donor.getId() + " with Recipient ID " + recipientId);
        return "redirect:/match/donor/matches";
    }

    @PostMapping("/unmatch")
    public String unmatch(@RequestParam Long donorId, @RequestParam Long recipientId, HttpSession session) {
        MyAppUsers loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            return "redirect:/users/login";
        }

        // Here we unmatch based on the user type
        if ("recipient".equalsIgnoreCase(loggedInUser.getUserType())) {
            myAppUserService.removeMatch(donorId, loggedInUser.getId());
            System.out.println("Recipient (ID: " + loggedInUser.getId() + ") unmatched with Donor ID: " + donorId);
            return "redirect:/match/recipient/matches";
        } else if ("donor".equalsIgnoreCase(loggedInUser.getUserType())) {
            // Remove match first
            myAppUserService.removeMatch(loggedInUser.getId(), recipientId);
            // Also remove the donor's profile id from the recipient's favorites list.
            myAppUserService.removeFavoriteDonor(recipientId, loggedInUser.getDonorId());
            System.out.println("Donor (ID: " + loggedInUser.getId() + ") unmatched with Recipient ID: " + recipientId);
            return "redirect:/match/donor/matches";
        } else {
            return "redirect:/users/login";
        }
    }
}
