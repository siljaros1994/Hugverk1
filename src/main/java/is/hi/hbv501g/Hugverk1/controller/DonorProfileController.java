package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/donorprofile")
public class DonorProfileController {

    @Autowired
    private DonorProfileService donorProfileService;

    @Autowired
    private MyAppUserRepository myAppUserRepository;

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

        // Retrieve profile based on the unique user ID to avoid mixing profiles
        Optional<DonorProfile> donorProfile = donorProfileService.findByUserId(loggedInUser.getId()); // Here we find the donor profile by the user's donor id.

        model.addAttribute("donorProfile", donorProfile.orElseGet(() -> {  // If profile exists, we use it. Otherwise we create a new profile for the donor
            DonorProfile newProfile = new DonorProfile();
            newProfile.setUser(loggedInUser);
            // Assign donorId if not already set
            if (loggedInUser.getDonorId() == null) {
                myAppUserRepository.save(loggedInUser);
            }
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

        // Ensure the logged-in user has a valid ID and is fully loaded from the database
        Optional<MyAppUsers> user = myAppUserRepository.findById(loggedInUser.getId());
        if (user.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        MyAppUsers currentUser = user.get();
        profileData.setUser(currentUser);

        // Here we save the profile to get the generated donorProfileId
        DonorProfile savedProfile = donorProfileService.saveOrUpdateProfile(profileData);

        // Assign donorId in MyAppUsers.
        if (currentUser.getDonorId() == null) {
            currentUser.setDonorId(profileData.getDonorProfileId());
            myAppUserRepository.save(currentUser);
        }

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
        }
        donorProfileService.saveOrUpdateProfile(profileData); // Save or update the profile in the database
        return "redirect:/donorprofile";
    }
    @PutMapping("/{id}/setDonationLimit")
    public ResponseEntity<String> setDonationLimit(@PathVariable Long id, @RequestParam int limit) {
        Optional<DonorProfile> donorProfileOpt = donorProfileService.findByProfileId(id);

        if (donorProfileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donor profile not found");
        }

        DonorProfile donorProfile = donorProfileOpt.get();
        donorProfile.setDonationLimit(limit);
        donorProfileService.saveOrUpdateProfile(donorProfile);

        return ResponseEntity.ok("Donation limit updated.");
    }
}