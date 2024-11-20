package is.hi.hbv501g.Hugverk1.Persistence.Repositories;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

//This file defines the database queries for the Report entity using Spring Data JPA


@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    /**
     * Find all reports submitted by a specific reporter (report_name)
     *
     * @param reportName The ID of the reporter
     * @return List of reports submitted by the reporter.
     */
    List<Report> findByReportName(Long reportName);

    /**
     * Find all reports involving a specific donor
     */
    List<Report> findByDonorId(Long donorId);

    /**
     * Find all reports involving a specific recipient
     */
    List<Report> findByRecipientId(Long recipientId);

    /**
     * Find reports by both reporter and accused user
     */
    //List<Report> findByReportNameAndAccusedUser(Long reportName, Long accusedUser);

    /**
     * Find all reports against a specific accused user
     *
     * @param accusedUser The ID of the accused user
     * @return List of reports against the accused user.
     */
    List<Report> findByAccusedUser(Long accusedUser);

}



