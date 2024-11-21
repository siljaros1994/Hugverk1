package is.hi.hbv501g.Hugverk1.Persistence.Repositories;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

//This file defines the database queries for the Report entity using Spring Data JPA


@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReporterId(Long reporterId);
    List<Report> findByDonorId(Long donorId);
    List<Report> findByRecipientId(Long recipientId);
    List<Report> findByReportedId(Long reportedId);
}




