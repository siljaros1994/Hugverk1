package is.hi.hbv501g.Hugverk1.Model;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface MyAppUserRepository extends JpaRepository<MyAppUsers, Long> {
    Optional<MyAppUsers> findByUsername(String username);
}
