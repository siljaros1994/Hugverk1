package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipientProfileRepository extends JpaRepository<RecipientProfile, Long> {
    Optional<RecipientProfile> findByUserRecipientId(String recipientId); //Custom query to find a recipient profile by the recipient id

    Page<RecipientProfile> findByRecipientType(String recipientType, Pageable pageable); //For filtering by recipient type 

    Page<RecipientProfile> findAll(Pageable pageable); //For all recipients
    

}
