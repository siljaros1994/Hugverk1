package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import org.springframework.data.domain.Page;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface RecipientProfileRepository extends JpaRepository<RecipientProfile, Long> {
  Optional<RecipientProfile> findByUserId(Long userId);
  Page<RecipientProfile> findByRecipientType(String recipientType, Pageable pageable);
  List<RecipientProfile> findByRecipientProfileIdIn(List<Long> recipientProfileIds);
  List<RecipientProfile> findByUserIdIn(List<Long> userIds);
}

