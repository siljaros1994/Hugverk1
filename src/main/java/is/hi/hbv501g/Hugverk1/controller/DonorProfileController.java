package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
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
@RequestMapping("/donorprofile")
public class DonorProfileController {

    @Autowired
    private DonorProfileService donorProfileService;

    @Value("${upload.path}") // the Path to where uploaded images are stored
    private String uploadPath;

    // Displays the donor profile page.
    @GetMapping
    public String showDonorProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Here we get the logged-in user from the security context
        MyAppUsers loggedInUser = (MyAppUsers) authentication.getPrincipal();
        if (loggedInUser == null || !"donor".equalsIgnoreCase(loggedInUser.getUserType())) { // Here we redirect to login if the user is not a donor.
            return "redirect:/users/login";
        }

        Optional<DonorProfile> donorProfile = donorProfileService.findByUserDonorId(loggedInUser.getDonorId()); // Here we find the donor profile by the user's donor id.
        model.addAttribute("donorProfile", donorProfile.orElseGet(() -> {  // If profile exists, we use it. Otherwise we create a new profile for the donor
            DonorProfile newProfile = new DonorProfile();
            newProfile.setUser(loggedInUser);
            return newProfile;
        }));
        return "donorprofile";
    }

    // Save or update the donor profile with an uploaded image of donor.
    @PostMapping("/saveOrEdit")
    public String saveOrEditProfile(@ModelAttribute("donorProfile") DonorProfile profileData,
                                    @RequestParam("profileImage") MultipartFile profileImage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyAppUsers loggedInUser = (MyAppUsers) authentication.getPrincipal();

        Optional<DonorProfile> existingProfile = donorProfileService.findByUserDonorId(loggedInUser.getDonorId()); // Here we retrieve the existing profile by donor id to check if it exists.

        if (existingProfile.isPresent()) { // If the profile already exists, we use its id to update.
            profileData.setProfileId(existingProfile.get().getProfileId());
        }

        profileData.setUser(loggedInUser);

        if (!profileImage.isEmpty()) { // Save uploaded image if it is included.
            try {
                String originalFileName = StringUtils.cleanPath(profileImage.getOriginalFilename());
                String filePath = uploadPath + originalFileName;
                File destinationFile = new File(filePath);
                destinationFile.getParentFile().mkdirs();
                profileImage.transferTo(destinationFile);
                profileData.setImagePath("/uploads/" + originalFileName); // Here we set the image path so it displays.

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (existingProfile.isPresent()) { // Here we use the existing image if no new image is uploaded.
            profileData.setImagePath(existingProfile.get().getImagePath());
        }

        donorProfileService.saveOrUpdateProfile(profileData); // Save or update the profile in the database
        return "redirect:/donorprofile";
    }
}