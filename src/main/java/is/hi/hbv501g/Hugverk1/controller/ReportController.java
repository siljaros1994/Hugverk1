package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Report;
import is.hi.hbv501g.Hugverk1.Persistence.forms.ReportForm;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import is.hi.hbv501g.Hugverk1.Services.ReportService;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;

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

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody Report report,
                                          @RequestParam Long loggedInUserId,
                                          @RequestParam String loggedInUserType) {
        try {
            Report savedReport = reportservice.createReport(report, loggedInUserId, loggedInUserType);
            return ResponseEntity.ok(savedReport);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/submitted")
    public ResponseEntity<List<Report>> getReportsByLoggedInUser(@RequestParam Long loggedInUserId) {
        List<Report> reports = reportservice.getReportsByLoggedInUser(loggedInUserId);
        return ResponseEntity.ok(reports);
    }
}


    //@GetMapping ("/report")
    //public String showRecipientReportPage (Model model, HttpSession session) {
    //    MyAppUsers loggedInUser = getLoggedInUser();
    //    if (loggedInUser == null || !"recipient".equalsIgnoreCase(loggedInUser.getUserType())) {
    //        return "redirect:/users/login";
    //    }
    //    model.addAttribute("user", loggedInUser);

    //    Long report_name = loggedInUser.getId();
    //    Long accused_user = user_Id;


        //Log the recipient's matched donor IDs so they can report them
    //    List<Long> matchedUserIds = loggedInUser.getMatchDonorsList();
    //    System.out.println("Matched Donor User Ids for Recipient Id" + loggedInUser.getId() + ":" + matchedUserIds);

        //Retrieve donor profiles based on matched user Ids
    //    List<DonorProfile> matchedDonors = donorProfileService.getProfilesByUserIds(matchedUserIds);

        //Log the donor profiles
    //    System.out.println("Matched Donor Profiles:" + matchedDonors);

    //    model.addAttribute("matchedDonors", matchedDonors);
    //    model.addAttribute("reportForm", new ReportForm()); //This creates a new report form for the user to fill out
    //    model.addAttribute("currentReports", reportservice.getConfirmedReportsForRecipient(loggedInUser.getId()));

    //    return "report_recipient"; //Render the recipient report page

    //}
    //Recipient can write in the report
    //@PostMapping("/report")
    //    public String createReport(@ModelAttribute ReportForm reportForm, HttpSession session) {
    //    MyAppUsers loggedInUser = getLoggedInUser();
    //    if (loggedInUser == null|| !"recipient".equalsIgnoreCase(loggedInUser.getUserType())) {
    //        return "redirect:/users/login";
    //    }
    //    reportservice.createReport(reportForm.getDonorId(), loggedInUser.getId(),
    //            reportForm.getIncidentDescription());

    //    return "redirect:/reports/recipient";
    //}
//}

    //The accused_user (i.e. recipient or donor) cannot see if the report_name (the one who reported) reported them

        //RedirectView reportUser(
            //@RequestParam("userId") String adminId) {
    //Redirect to the report page with the userId as a query
        //String redirectUrl ="/report?accusedUser=" + user_Id;
       // return new RedirectView(redirectUrl);


    //@PostMapping
    //public ResponseEntity<String> createReport(
      //      @RequestParam String reportName,
        //    @RequestParam String accusedUser,
          //  @RequestParam String incidentDescription
           // ) {
        //try {
          //  Report report = new Report();
            //report.setreportName(reportName);
            //report.setAccusedUser(accusedUser);
            //report.setIncidentDescription(incidentDescription);
        //}
         //return null; //should not be there
    //}
        //catch(Exception e) {
           // throw new RuntimeException(e);
      //  }
        //reportservice.saveReport(report);
        //return ResponseEntity.status(HttpStatus.CREATED).body("Report submitted successfully!");


