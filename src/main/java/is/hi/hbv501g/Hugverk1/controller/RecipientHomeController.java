package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

@Controller
public class RecipientHomeController {

    private static final Logger logger = LoggerFactory.getLogger(RecipientHomeController.class);

    @Autowired
    private DonorProfileService donorProfileService;

    @GetMapping("/home/recipient")
    public String recipientHome(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "age") String sortBy,
            Model model,
            HttpSession session
    ) {
        MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
        if (loggedInUser == null) {
            logger.warn("LoggedInUser not found in session. Redirecting to login.");
            return "redirect:/users/login";
        }
        model.addAttribute("username", loggedInUser.getUsername());

        // Configure the sorting direction and it's attributes
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, 9, sort);

        Page<DonorProfile> donorsPage;
        if (keyword != null && !keyword.isEmpty()) {
            donorsPage = donorProfileService.findByKeyword(keyword, pageable);
        } else {
            donorsPage = donorProfileService.findAll(pageable);
        }

        // Log the number of donors in the page, i like to see how many donors have created a profile.
        logger.info("Number of donors in donorsPage: {}", donorsPage.getContent().size());

        // Add data to the model
        model.addAttribute("donorsPage", donorsPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "recipientHome";
    }

    @GetMapping("/recipient/view/{profileId}")
    public String viewDonorProfile(@PathVariable String profileId, Model model) {
        Optional<DonorProfile> donorProfile = donorProfileService.findByProfileId(profileId);
        if (donorProfile.isPresent()) {
            model.addAttribute("donorProfile", donorProfile.get());
            logger.info("Displaying profile for donor with profileId: {}", profileId);
            return "donorsPage";
        } else {
            logger.warn("Donor profile with profileId {} not found. Redirecting to recipient home.", profileId);
            return "redirect:/home/recipient";
        }
    }
}