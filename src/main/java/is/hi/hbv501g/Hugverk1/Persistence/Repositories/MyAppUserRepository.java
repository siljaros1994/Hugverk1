package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyAppUserRepository extends JpaRepository<MyAppUsers, String> {
    Optional<MyAppUsers> findByUsername(String username);
    List<MyAppUsers> findByUserType(String userType);
    List<MyAppUsers> findAll();
    boolean existsByUsername(String username);
    Optional<MyAppUsers> findByDonorId(String donorId);
    Optional<MyAppUsers> findByRecipientId(String recipientId);

    @Query("SELECT u FROM MyAppUsers u WHERE u.favoriteDonors LIKE %:donorId% AND u.userType = 'recipient'")
    List<MyAppUsers> findRecipientsWhoFavoritedDonor(@Param("donorId") String donorId);
}
