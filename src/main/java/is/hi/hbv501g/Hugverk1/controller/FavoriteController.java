package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
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

@Controller
@RequestMapping("/recipient")
public class FavoriteController {

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    private DonorProfileService donorProfileService;

    @GetMapping("/favorites")
    public String favorites(Model model, HttpSession session) {
        String recipientId = (String) session.getAttribute("recipientId");
        List<DonorProfile> favoriteProfiles = new ArrayList<>();  // Here we initialize an empty list

        if (recipientId != null) {
            List<String> favoriteIds = myAppUserService.getFavoriteDonors(recipientId);
            System.out.println("Favorite Donor IDs for Recipient " + recipientId + ": " + favoriteIds);
            favoriteProfiles = donorProfileService.getProfilesByIds(favoriteIds);
        }

        model.addAttribute("favorites", favoriteProfiles);
        return "favorites";
    }

    @GetMapping("/favorite/{profileId}")
    public String addFavoriteDonor(@PathVariable String profileId, HttpSession session) {
        String recipientId = (String) session.getAttribute("recipientId");
        System.out.println("Adding Favorite Donor ID: " + profileId + " to Recipient ID: " + recipientId);

        if (recipientId != null){
            myAppUserService.addFavoriteDonor(recipientId, profileId);
        }
        return "redirect:/home/recipient";
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> getFavoriteDonors(@RequestParam String recipientId) {
        try {
            List<String> favorites = myAppUserService.getFavoriteDonors(recipientId);
            System.out.println("Favorite Donor IDs for Recipient " + recipientId + ": " + favorites);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}