package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.DonorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

    public Optional<DonorProfile> findByProfileId(Long profileId) {
        return donorProfileRepository.findById(profileId);
    }

    // Our method to get all donors by type, like sperm or egg donor.
    public Page<DonorProfile> getDonorsByType(String donorType, Pageable pageable) {
        return donorProfileRepository.findByDonorType(donorType, pageable);
    }

    public List<DonorProfile> getAllDonors() {
        return donorProfileRepository.findAll();
    }

    public Page<DonorProfile> findByKeyword(String keyword, Pageable pageable) {
        return donorProfileRepository.findByKeyword(keyword, pageable);
    }

    // Here we retrieve all donor profiles with pagination
    public Page<DonorProfile> findAll(Pageable pageable) {
        return donorProfileRepository.findAll(pageable);
    }
}