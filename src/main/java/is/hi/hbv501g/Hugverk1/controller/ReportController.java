package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.forms.ReportForm;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import is.hi.hbv501g.Hugverk1.Services.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import java.util.List;

//This file handles the HTTP requests and connects the frontend views to the backend logic


@Controller
@RequestMapping("/reports")
@SessionAttributes("user")
public class ReportController extends BaseController {

    @Autowired
    private ReportService reportservice;

    @Autowired
    private MyAppUserService userService;

    @Autowired
    private RecipientProfileService recipientProfileService;

    @Autowired
    private DonorProfileService donorProfileService;

    @GetMapping("/{userType}/{id}")
    public String showUserIdReportPage(@PathVariable("userType") String userType, @PathVariable("id")Long userId, Model model, HttpSession session) {
    MyAppUsers currentUser = (MyAppUsers) session.getAttribute("user");

    if (currentUser == null) {
        throw new UsernameNotFoundException("User not found in session");

    }
    model.addAttribute("user", currentUser);

    Long reporterId = currentUser.getId();
    Long reportedId = userId;





        // Log the recipient's matched donor IDs
        List<Long> matchedUserIds = currentUser.getMatchDonorsList();
        System.out.println("Matched Donor User IDs for Recipient ID " + currentUser.getId() + ": " + matchedUserIds);

        // Retrieve donor profiles based on matched user IDs
        List<DonorProfile> matchedDonors = donorProfileService.getProfilesByUserIds(matchedUserIds);

        // Log the donor profiles
        System.out.println("Matched Donor Profiles: " + matchedDonors);

        model.addAttribute("matchedDonors", matchedDonors);
        model.addAttribute("reportForm", new ReportForm()); // This creates a new report form
        model.addAttribute("currentreports", reportservice.createReport(currentUser.getId(), recipientId, reporterId));

        return "report"; //Render the report page
    }



    //Users can create a report
//@PostMapping("/report")
//public String createReport(@ModelAttribute ReportForm reportForm, HttpSession session) {
//    MyAppUsers currentUser = getcurrentUser();
//    if (currentUser == null || ! "recipient".equalsIgnoreCase(loggedInUser.getId())
//    }
}

