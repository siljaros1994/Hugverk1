package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/recipientprofile")
@SessionAttributes("user")
public class RecipientProfileController extends BaseController{

    @Autowired
    private RecipientProfileService recipientProfileService;

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Value("${upload.path}") //Path to where uploaded images are stored
    private String uploadPath;

    // Displays the recipient profile page.
    @GetMapping
    public String showRecipientProfilePage(Model model) {
        MyAppUsers sessionUser = getLoggedInUser();
        if (sessionUser == null || !isUserType("recipient")) {
            return "redirect:/users/login";
        }
        // Reload the user from the repository to get the latest data
        MyAppUsers loggedInUser = myAppUserRepository.findById(sessionUser.getId())
                .orElse(sessionUser);
        model.addAttribute("user", loggedInUser);

        // Find or create a new profile (this will return the up‑to‑date profile)
        RecipientProfile recipientProfile = recipientProfileService.findOrCreateProfile(loggedInUser);
        model.addAttribute("recipientProfile", recipientProfile);
        return "recipientprofile";
    }

    //Save or update the recipient profile with an uploaded image of recipient
    @PostMapping("/saveOrEdit")
    public String saveOrEditProfile(@ModelAttribute("recipientProfile") RecipientProfile profileData,
                                    @RequestParam("profileImage") MultipartFile profileImage) throws IOException {

        MyAppUsers loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            return "redirect:/users/login";
        }

        // Bind the logged in user to the profile data
        profileData.setUser(loggedInUser);

        // Log to see the incoming profile data
        System.out.println("Profile Data: " + profileData + " Here we have our users profile data!!");

        RecipientProfile updatedProfile;

        // Here we check if the profile already exists
        Optional<RecipientProfile> existingProfile = recipientProfileService.findByUserId(loggedInUser.getId());

        if (existingProfile.isPresent()) { // Update the existing profile
            RecipientProfile profileToUpdate = existingProfile.get();
            BeanUtils.copyProperties(profileData, profileToUpdate, "recipientProfileId", "user");
            recipientProfileService.processProfileImage(profileToUpdate, profileImage);
            updatedProfile = recipientProfileService.saveOrUpdateProfile(profileToUpdate);
        } else {
            profileData.setUser(loggedInUser);
            recipientProfileService.processProfileImage(profileData, profileImage);
            updatedProfile = recipientProfileService.saveOrUpdateProfile(profileData);
        }
        loggedInUser.setRecipientId(updatedProfile.getRecipientProfileId());
        myAppUserRepository.save(loggedInUser);
        System.out.println("Recipient ID set for user: " + loggedInUser.getRecipientId());

        return "redirect:/recipientprofile";
    }
}