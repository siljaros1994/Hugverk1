package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Report;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
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
    private ReportService reportService;

    @Autowired
    private MyAppUserService userService;


    @Autowired
    private RecipientProfileService recipientProfileService;

    @Autowired
    private DonorProfileService donorProfileService;

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private ReportForm reportForm;

    @GetMapping("/{userType}/{id}")
    public String showUserIdReportPage(@PathVariable("userType") String userType, @PathVariable("id")Long userId, Model model, HttpSession session) {
        MyAppUsers currentUser = (MyAppUsers) session.getAttribute("user");

        if (currentUser == null) {
            throw new UsernameNotFoundException("User not found in session");

        }
        model.addAttribute("user", currentUser);
        model.addAttribute("userType", userType);
        //model.addAttribute("reporterId", reporterId);
        //model.addAttribute("reportedId", reportedId);
        //model.addAttribute("matchedUsers", userService.getMatchedUsers(currentUser.getId(), currentUser.getUserType()));
        model.addAttribute("reportForm", new ReportForm()); // This creates a new report form
        return "report";

    }
    @PostMapping("/report")
    public String createReport(@ModelAttribute ReportForm reportForm, HttpSession session) {
    MyAppUsers currentUser = (MyAppUsers) session.getAttribute("user");
    if (currentUser == null) {
        throw new UsernameNotFoundException("User not found in session");
    }
    if (reportForm.getReportedId() == null || reportForm.getReportedId().equals(currentUser.getId())) {
        throw new IllegalArgumentException("Cannot report yourself or invalid user ID.");
    }
    //Call the service method to create a report
        reportService.createReport(
                reportForm.getDonorId(),
                reportForm.getRecipientId(),
                currentUser.getId(),
                reportForm.getReportedId()
        );
    return "redirect:/users/login";
    }

    @PostMapping("/submit/{id}")
    public String submitReport(@PathVariable Long id){
        reportService.submitReport(id);
        return "redirect:/meesages";
    }
}



/**
        Long reporterId = currentUser.getId();
    Long reportedId = userId;
    Long donorId = reportForm.getdonorId();
    Long recipientId = reportForm.getrecipientId();

    List<MyAppUsers> matchedUsers = userService.getMatchedUsers(currentUser.getId(),currentUser.getUserType());




        return "report"; //Render the report page
    }

    //Users can create a report
@PostMapping("/report")
public String createReport(@ModelAttribute ReportForm reportForm, Model model, HttpSession session) {
    MyAppUsers loggedInUser = getLoggedInUser();
    if (loggedInUser == null || "user".equalsIgnoreCase(loggedInUser.getUserType())) {
        return "redirect:/users/login";
    }


    reportService.createReport(
            getreporterId,
            getreportedId,
            loggedInUser.getId(),
    reportForm.getReportedId()
    );
    return "redirect:/users/login";
}

/**
    //Users can create a report
  @PostMapping("/report")
public String createReport(@ModelAttribute ReportForm reportForm, HttpSession session) {
    MyAppUsers currentUser = (MyAppUsers) session.getAttribute("user");

    if (currentUser == null)
        throw new UsernameNotFoundException("User not found in session"); //|| ! "recipient".equalsIgnoreCase(getLoggedInUser().getUserType())) {}
    }

    Long reporterId = currentUser.getId();
    Long reportedId = reportForm.getReportedUserId();
    String description = reportForm.getDescription();

    if (reportedId == null || reportedId.equals(reporterId)) {
        throw new IllegalArgumentException("Cannot report yourself or invalid user ID.");
    }


//Create and save the report
Report newReport = new Report(reporterId, reporterdId, incidentdescription);
reportService.createReport(newReport);

System.out.printIn("New report created:" + newReport);

return "redirect:/reports/" + currentUser.getUserType() + "/" + reportedId;

}
//Submit a report
@PostMapping("/submit/{id}")
public String submitReport(@PathVariable Long id) {
    reportService.submitReport(id);
    return "redirect:/messages";
}
}
//Users can submit a booking
//@PostMapping("/submit/{id}")
//public String submitReport(@PathVariable Long id) {
//    reportService.submitreport(id);
//    return "redirect:/bookings/donor";
//}
**/
