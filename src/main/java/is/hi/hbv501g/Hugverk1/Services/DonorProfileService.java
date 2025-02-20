package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.DonorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class DonorProfileService {

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    public DonorProfile findOrCreateProfile(MyAppUsers user) {
        return donorProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    DonorProfile newProfile = new DonorProfile();
                    newProfile.setUser(user);
                    newProfile.setDonationLimit(5);
                    newProfile.setDonationsCompleted(0);
                    return newProfile;
                });
    }

    public void processProfileImage(DonorProfile profile, MultipartFile profileImage, String uploadPath) throws IOException {
        if (!profileImage.isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                System.out.println(created ? "Upload directory created" : "Failed to create upload directory");
            }

            String originalFilename = profileImage.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            File destinationFile = new File(uploadDir, uniqueFilename);
            System.out.println("Saving file to: " + destinationFile.getAbsolutePath());
            profileImage.transferTo(destinationFile);

            profile.setImagePath("/uploads/" + uniqueFilename);
            System.out.println("Image path set: " + profile.getImagePath());
        }
    }

    // Save or update the donor profile in the database
    public DonorProfile saveOrUpdateProfile(DonorProfile profile) {
        return donorProfileRepository.save(profile);
    }

    // Finds a donor profile by the donor ID, unique identifier for each donor
    public Optional<DonorProfile> findByUserDonorId(Long donorId) {
        return donorProfileRepository.findByUserDonorId(donorId);
    }

    public Optional<DonorProfile> findByProfileId(Long donorProfileId) {
        return donorProfileRepository.findById(donorProfileId);
    }

    public Optional<DonorProfile> findByUserId(Long userId) {
        return donorProfileRepository.findByUserId(userId);
    }

    // Our method to get all donors by type, like sperm or egg donor.
    public Page<DonorProfile> getDonorsByType(String donorType, Pageable pageable) {
        return donorProfileRepository.findByDonorType(donorType, pageable);
    }

    public List<DonorProfile> getAllDonors() {
        return donorProfileRepository.findAll();
    }

    public List<DonorProfile> getProfilesByIds(List<Long> donorProfileIds) {
        return donorProfileRepository.findByDonorProfileIdIn(donorProfileIds);
    }

    public List<DonorProfile> getProfilesByUserIds(List<Long> userIds) {
        return donorProfileRepository.findByUserIdIn(userIds);
    }

    public Page<DonorProfile> findByKeyword(String keyword, Pageable pageable) {
        return donorProfileRepository.findByKeyword(keyword, pageable);
    }

    // Here we retrieve all donor profiles with pagination
    public Page<DonorProfile> findAll(Pageable pageable) {
        return donorProfileRepository.findAll(pageable);
    }
}