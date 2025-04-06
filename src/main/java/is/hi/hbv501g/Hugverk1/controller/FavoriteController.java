package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/recipient")
@SessionAttributes("user")
public class FavoriteController extends BaseController{

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    private DonorProfileService donorProfileService;

    @GetMapping("/favorites")
    public String favorites(Model model, HttpSession session) {
        MyAppUsers user = (MyAppUsers) session.getAttribute("user");
        Long userId = user != null ? user.getRecipientId() : null;

        if (user == null || userId == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", user);
        List<DonorProfile> favoriteProfiles = new ArrayList<>();  // Here we initialize an empty list

        // Fetch favorite donor profiles if recipientId is available
        List<Long> favoriteIds = myAppUserService.getFavoriteDonors(userId);
        favoriteProfiles = donorProfileService.getProfilesByIds(favoriteIds);
        model.addAttribute("favorites", favoriteProfiles);
        return "favorites";
    }

    @GetMapping("/favorite/{donorProfileId}")
    public String addFavoriteDonor(@PathVariable Long donorProfileId, HttpSession session) {
        MyAppUsers user = (MyAppUsers) session.getAttribute("user");
        Long userId = user != null ? user.getRecipientId() : null;

        if (userId != null) {
            myAppUserService.addFavoriteDonor(userId, donorProfileId);
        }
        return "redirect:/home/recipient";
    }

    @GetMapping("/list")
    public ResponseEntity<List<Long>> getFavoriteDonors(@RequestParam Long userId) {
        try {
            List<Long> favorites = myAppUserService.getFavoriteDonors(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/unfavorite/{donorProfileId}")
    public String unfavoriteDonor(@PathVariable Long donorProfileId, HttpSession session) {
        MyAppUsers user = (MyAppUsers) session.getAttribute("user");
        if (user == null || user.getRecipientId() == null) {
            return "redirect:/users/login";
        }

        Long recipientProfileId = user.getRecipientId();

        // Remove donor from favorites list only.
        myAppUserService.removeFavoriteDonor(recipientProfileId, donorProfileId);

        // Here we also add unfavoriting, so we can also cancel an approved match,
        Optional<DonorProfile> donorProfileOpt = donorProfileService.findByProfileId(donorProfileId);
        if (donorProfileOpt.isPresent()) {
            Long donorUserId = donorProfileOpt.get().getUser().getId();
            Long recipientUserId = user.getId();
            myAppUserService.removeMatch(donorUserId, recipientUserId);
        } else {
            System.err.println("Donor profile not found for ID: " + donorProfileId);
        }

        return "redirect:/recipient/favorites";
    }
}