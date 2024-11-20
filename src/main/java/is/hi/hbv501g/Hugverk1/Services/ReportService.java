package is.hi.hbv501g.Hugverk1.Services;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Report;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.DonorProfileRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.RecipientProfileRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.ReportRepository;
import  org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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


    public Report createReport(Long donorId) {
        Report report = new Report();
        report.setDonorId(donorId);
        report.setRecipientId(recipientId);
        report.setReporterId(reporterId);
        report.setReportedId(reportedId);
        report.setSubmitted(false);
        return reportRepository.save(report);
    }
}




