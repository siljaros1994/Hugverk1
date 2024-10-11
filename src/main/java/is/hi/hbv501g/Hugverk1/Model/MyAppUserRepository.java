package is.hi.hbv501g.Hugverk1.Model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
public interface MyAppUserRepository extends JpaRepository<MyAppUsers, Long> {
    Optional<MyAppUsers> findByUsername(String username);
    List<MyAppUsers> findByUserType(String userType);
    boolean existsByUsername(String username);
}