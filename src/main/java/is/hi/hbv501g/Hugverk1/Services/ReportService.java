package is.hi.hbv501g.Hugverk1.Services;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
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


    public Report createReport(Long donorId, Long recipientId, Long reporterId, Long reportedId) {

        if (reporterId == null || reportedId == null) {
            throw new IllegalArgumentException("Reporter and Reported IDs must not be null.");
        }

        Report report = new Report();
        report.setDonorId(donorId);
        report.setRecipientId(recipientId);
        report.setReporterId(reporterId);
        report.setReportedId(reportedId);
        report.setSubmitted(false);
        return reportRepository.save(report);
    }

    public void submitReport(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setSubmitted(true);
        reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

}

    //ReportService reportService = new reportService();
    //List<Report> allReports = reportService.getAllReports();

    //public List<Report> getAllReports() {
    //    List<Report> report = List.of();
    //    return report;


    //public  List<Report> getAllReports() {
      //  List<Report> reports = reportRepository.findAll();
        //return reports;
    //}





