package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.DonorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DonorProfileService {

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    // Save or update the donor profile in the database
    public void saveOrUpdateProfile(DonorProfile profile) {
        donorProfileRepository.save(profile);
    }

    // Finds a donor profile by the donor ID, unique identifier for each donor
    public Optional<DonorProfile> findByUserDonorId(String donorId) {
        return donorProfileRepository.findByUserDonorId(donorId);
    }
}