package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        MyAppUsers loggedInUser = getLoggedInUser();

        if (loggedInUser == null || !isUserType("recipient")) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", loggedInUser);

        // Find or create a new profile
        RecipientProfile recipientProfile = recipientProfileService.findOrCreateProfile(loggedInUser);
        model.addAttribute("recipientProfile", recipientProfile);

        return "recipientprofile";
    }

    //Save or update the recipient profile with an uploaded image of recipient
    @PostMapping("/saveOrEdit")
    public String saveOrEditProfile(@ModelAttribute("recipientProfile") RecipientProfile profileData,
                                  @RequestParam("profileImage") MultipartFile profileImage) throws IOException {

        MyAppUsers loggedInUser = getLoggedInUser();
        profileData.setUser(loggedInUser);

        // Here we check if the profile already exists
        Optional<RecipientProfile> existingProfile = recipientProfileService.findByUserId(loggedInUser.getId());

        if (existingProfile.isPresent()) { // Update the existing profile
            RecipientProfile profileToUpdate = existingProfile.get();
            BeanUtils.copyProperties(profileData, profileToUpdate, "recipientProfileId", "user");
            recipientProfileService.processProfileImage(profileToUpdate, profileImage, uploadPath);
            recipientProfileService.saveOrUpdateProfile(profileToUpdate);
        } else {
            // Create a new profile if none exists
            profileData.setUser(loggedInUser);
            recipientProfileService.processProfileImage(profileData, profileImage, uploadPath);
            recipientProfileService.saveOrUpdateProfile(profileData); // Save or update the profile

            // Assign recipientId if not already set
            if (loggedInUser.getRecipientId() == null) {
                loggedInUser.setRecipientId(profileData.getRecipientProfileId());
                myAppUserRepository.save(loggedInUser);
            }
        }

        return "redirect:/recipientprofile";
    }
}