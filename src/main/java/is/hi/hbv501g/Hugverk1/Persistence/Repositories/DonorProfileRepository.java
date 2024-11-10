package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {
    Optional<DonorProfile> findByUserDonorId(Long donorId); // Custom query to find a donor profile by the donor id
    Optional<DonorProfile> findByUserId(Long userId); // Here we find the profile by the unique user Id
    Page<DonorProfile> findByDonorType(String donorType, Pageable pageable); // For filtering by donor type
    Page<DonorProfile> findAll(Pageable pageable); // For all donors

    @Query("SELECT d FROM DonorProfile d WHERE " +
            "LOWER(d.eyeColor) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.hairColor) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.race) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.bloodType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.donorType) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<DonorProfile> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}