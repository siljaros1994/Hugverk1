package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import java.util.List;
import java.util.Optional;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyAppUserRepository extends JpaRepository<MyAppUsers, Long> {
    Optional<MyAppUsers> findByUsername(String username);
    List<MyAppUsers> findByUserType(String userType);
    boolean existsByUsername(String username);
}