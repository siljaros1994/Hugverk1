package is.hi.hbv501g.Hugverk1.Persistence.Repositories;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByDonorId(Long donorId);
    List<Booking> findByRecipientId(Long recipientId);
    List<Booking> findByDonorIdAndConfirmedFalse(Long donorId);
}
