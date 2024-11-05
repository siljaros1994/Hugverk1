package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
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

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Value("${upload.path}") //Path to where uploaded images are stored
    private String uploadPath;

    // Displays the recipient profile page.
    @GetMapping
    public String showRecipientProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //Here we get the logged-in user from the security context
        MyAppUsers loggedInUser = (MyAppUsers) authentication.getPrincipal();
        if (loggedInUser == null || !"recipient".equalsIgnoreCase(loggedInUser.getUserType())) { //Here we redirect to login if the user is not a recipient
            return "redirect:/user/login";
        }

        Optional<RecipientProfile> recipientProfile = recipientProfileService.findByUserRecipientId(loggedInUser.getRecipientId()); //Here we find the recipient profile by the user's recipient id
        model.addAttribute("recipientProfile", recipientProfile.orElseGet(() -> { //If the profile exists, we use it. Otherwise, we create a new profile for the recipient
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
        MyAppUsers loggedInUser = (MyAppUsers) authentication.getPrincipal();

        Optional<MyAppUsers> user = myAppUserRepository.findById(loggedInUser.getId());
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        MyAppUsers currentUser = user.get();
        profileData.setUser(currentUser);

        RecipientProfile savedProfile = recipientProfileService.saveOrUpdateProfile(profileData);

        if (currentUser.getRecipientId() == null) {
            currentUser.setRecipientId(profileData.getRecipientProfileId());
            myAppUserRepository.save(currentUser);
        }

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
        }
        return "redirect:/recipientprofile";
    }
}