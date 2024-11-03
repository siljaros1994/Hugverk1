package is.hi.hbv501g.Hugverk1.Persistence.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipientProfileRepository extends JpaRepository<RecipientProfile, Long> {
}
