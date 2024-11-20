package is.hi.hbv501g.Hugverk1.Services;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Report;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.DonorProfileRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.RecipientProfileRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.ReportRepository;
import  org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


//This file contains the business logic for managing reports
//ReportService.java interacts with the repository to perform database operations and implements key functionalities


@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    @Autowired
    private RecipientProfileRepository recipientProfileRepository;



    /**
     *
     * @param donorId
     * @param recipientId
     * @param incidentdescription
     * @return
     */

    /**
     * Create a new report and save it to the database
     *
     * @param report The report to be saved
     * @return The saved report
     * @throws IllegalAccessException if the logged-in user is not the reporter
     */


    public Report createReport(Report report,Long loggedInUserId, String loggedInUserType) throws IllegalAccessException {
        //Ensures the logged-in user is the reporter
        if (!loggedInUserId.equals(report.getReportName())) {
            throw new IllegalAccessException("You are not authorized to create this report.");
        }

        //Validate reporter's type
        if (!loggedInUserType.equalsIgnoreCase("donor_id") && !loggedInUserType.equalsIgnoreCase("recipient_id")) {
            throw new IllegalAccessException("Invalid reporter type. Must be a donor or recipient");
        }

        //Validate accused user's type
        if (!report.getAccusedUserType().equalsIgnoreCase("donor_id") && !loggedInUserType.equalsIgnoreCase("recipient_id")) {
            throw new IllegalAccessException("Invalid accused user type. Must be a donor or recipient");
        }
        return reportRepository.save(report);

    }

    public List<Report> getReportsByLoggedInUser(Long loggedInUserId) {
        return null;
    }
    //Report report = new Report();
        //report.setDonorId(donorId);
        //report.setRecipientId(recipientId);
        //report.setIncidentDescription(incidentdescription);
        //report.setReportName(userId);
        //report.setReport(false);
        //return reportRepository.save(report);
    }

    //public List<Report> getAllReports() { return reportRepository.findAll(); }

    //public List<Report> getReportsBy

    //public Report saveReport(Report report) {
    //    return reportRepository.save(report);
    //}

    //public Object getConfirmedReportsForRecipient(Long id) {
    //    return reportRepository.findByRecipientId(id);
    //}

    //public void createReport(Long donorId, Long id, Object incidentDescription) {
    //}
//}
