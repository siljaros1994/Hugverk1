package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    private DonorProfileService donorProfileService;

    @Autowired
    private RecipientProfileService recipientProfileService;

    @GetMapping("/donor/matches")
    public String donorMatches(Model model, HttpSession session) {
        MyAppUsers user = (MyAppUsers) session.getAttribute("user");
        Long donorId = (Long) session.getAttribute("donorId");

        if (user == null) {
            System.out.println("User in session is null.");
            return "redirect:/users/login";
        }

        System.out.println("User in session: " + user);
        System.out.println("Donor ID in session: " + donorId);

        model.addAttribute("user", user);
        model.addAttribute("userType", user.getUserType());

        if (donorId !=null) {
            List<Long> matchedRecipientIds = myAppUserService.getMatchRecipients(donorId);
            List<RecipientProfile> matchedRecipients = recipientProfileService.getProfilesByIds(matchedRecipientIds);
            model.addAttribute("matches", matchedRecipients);
        } else {
            System.out.println("Donor ID is null in session");
        }
        return "matches";
    }

    @GetMapping("/recipient/matches")
    public String recipientMatches(Model model, HttpSession session) {
        MyAppUsers user = (MyAppUsers) session.getAttribute("user");
        Long recipientId = (Long) session.getAttribute("recipientId");

        if (user == null) {
            System.out.println("User in session is null.");
            return "redirect:/users/login";
        }

        System.out.println("User in session: " + user);
        System.out.println("Recipient ID in session: " + recipientId);

        model.addAttribute("user", user);
        model.addAttribute("userType", user.getUserType());

        if (recipientId != null) {
            List<Long> matchedDonorIds = myAppUserService.getMatchesForRecipient(recipientId);
            List<DonorProfile> matchedDonors = donorProfileService.getProfilesByIds(matchedDonorIds);
            model.addAttribute("matches", matchedDonors);
        } else {
            System.out.println("Recipient ID is null in session");
        }
        return "matches";
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMatchRecipient(@RequestParam Long donorId, @RequestParam Long recipientId) {
        try {
            myAppUserService.addMatchRecipient(donorId,recipientId);
            return ResponseEntity.ok("Recipient added to matches");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Long>> getMatchRecipients(@RequestParam Long donorId) {
        try {
            List<Long> matches = myAppUserService.getMatchRecipients(donorId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/unmatch")
    public ResponseEntity<String> unmatch(@RequestParam Long donorId, @RequestParam Long recipientId) {
        try {
            myAppUserService.removeMatch(donorId, recipientId);
            return ResponseEntity.ok("Recipient removed from matches");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
