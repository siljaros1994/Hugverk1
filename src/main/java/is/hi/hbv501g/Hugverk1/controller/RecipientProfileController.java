package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
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
public class RecipientProfileController {
    @Autowired
    private RecipientProfileService recipientProfileService;

    @Value("${upload.path}") //Path to where uploaded images are stored
    private String uploadPath;

    //Displays the recipient profile page.
    @GetMapping
    public String showrecipientProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //Here we get the logged-in user from the security context
        MyAppUsers loggedInUser = (MyAppUsers) authentication.getPrincipal();
        if (loggedInUser == null || !"recipient".equalsIgnoreCase(loggedInUser.getUserType())) { //Here we redirect to login if the user is not a recipient
            return "redirect:/user/login";
        }

        Optional<RecipientProfile> recipientProfile = recipientProfileService.findByUserRecipientId(loggedInUser.getRecipientId()); //Here we find the recipient profile by the user's recipient id
        model.addAttribute("recipientPrfile", recipientProfile.orElseGet(() -> { //If the profile exists, we use it. Otherwise, we create a new profile for the recipient
            RecipientProfile newProfile = new RecipientProfile();
            newProfile.setUser(loggedInUser);
            return newProfile;
        }));
        return "recipientprofile";
    }

    //Save or update the recipient profile with an uploaded image of recipient
    @PostMapping("/saveOrEdit")
    public String saveOrEditProfile(@ModelAttribute("recipientProfile") RecipientProfile profileData,
                                    @RequestParam("profileImage") MultipartFile profileImage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication();

        Optional<RecipientProfile> existingProfile = recipientProfileService.findByUserRecipientId(loggedInUser.getRecipientId()); //Retrieves the existing profile by recipient id to check if it exists

        if (existingProfile.isPresent()) { //If the profile already exists, we use its id to update.
            profileData.setProfileId(existingProfile.get().getProfileId());
        }

        profileData.setUser(loggedInUser);

        if (!profileImage.isEmpty()) { //Save uploaded image if it's included
            try {
                String originalFileName = StringUtils.cleanPath(profileImage.getOriginalFilename());
                String filePath = uploadPath + originalFileName;
                File destinationFile = new File(filePath);
                destinationFile.getParentFile().mkdirs();
                profileImage.transferTo(destinationFile);
                profileData.setImagePath("/uploads/" + originalFileName); //Here is the image path (so it displays)

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (existingProfile.isPresent()) { //Here is the existing image, if no new image is uploaded
            profileData.setImagePath(existingProfile.get().getImagePath());


        }
        recipientProfileService.saveOrUpdateProfile(profileData); //Save or update the profile in the database
        return "redirect:/recipientprofile";
    }

}

