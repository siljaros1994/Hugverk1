package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.BeanUtils;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/donorprofile")
@SessionAttributes("user")
public class DonorProfileController extends BaseController {

    @Autowired
    private DonorProfileService donorProfileService;

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Value("${upload.path}") // the Path to where uploaded images are stored
    private String uploadPath;

    // Displays the donor profile page.
    @GetMapping
    public String showDonorProfilePage(Model model) {
        MyAppUsers loggedInUser = getLoggedInUser();

        if (loggedInUser == null || !isUserType("donor")) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", loggedInUser);

        // Retrieve or create a donor profile
        DonorProfile donorProfile = donorProfileService.findOrCreateProfile(loggedInUser);
        model.addAttribute("donorProfile", donorProfile);

        return "donorprofile";
    }

    // Save or update the donor profile with an uploaded image of donor.
    @PostMapping("/saveOrEdit")
    public String saveOrEditProfile(@ModelAttribute("donorProfile") DonorProfile profileData,
                                    @RequestParam("profileImage") MultipartFile profileImage) throws IOException {
        MyAppUsers loggedInUser = getLoggedInUser();
        profileData.setUser(loggedInUser);

        // Here we check if the profile already exists
        Optional<DonorProfile> existingProfile = donorProfileService.findByUserId(loggedInUser.getId());

        if (existingProfile.isPresent()) {
            // Update the existing profile
            DonorProfile profileToUpdate = existingProfile.get();
            BeanUtils.copyProperties(profileData, profileToUpdate, "donorProfileId", "user"); // Copy data
            donorProfileService.processProfileImage(profileToUpdate, profileImage, uploadPath); // Process the image
            donorProfileService.saveOrUpdateProfile(profileToUpdate); // Save the updated profile
        } else {
            // Create a new profile
            donorProfileService.processProfileImage(profileData, profileImage, uploadPath); // Process image
            donorProfileService.saveOrUpdateProfile(profileData); // Save or update the profile
        }

        // Assign donorId if not already set (ensure it's the user's id)
        if (loggedInUser.getDonorId() == null) {
            loggedInUser.setDonorId(loggedInUser.getId()); //Set to user id
            myAppUserRepository.save(loggedInUser);
        }

        return "redirect:/donorprofile";
    }
}
