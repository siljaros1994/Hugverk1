package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.RecipientProfileRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RecipientProfileService {

    @Autowired
    private RecipientProfileRepository recipientProfileRepository;

    @Autowired
    public RecipientProfileService(RecipientProfileRepository recipientProfileRepository) {
        this.recipientProfileRepository = recipientProfileRepository;
    }

    public RecipientProfile findOrCreateProfile(MyAppUsers user) {
        return recipientProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    RecipientProfile newProfile = new RecipientProfile();
                    newProfile.setUser(user);
                    return recipientProfileRepository.save(newProfile);
                });
    }

    public void processProfileImage(RecipientProfile profileData, MultipartFile profileImage, String uploadPath) throws IOException {
        if (!profileImage.isEmpty()) {
            String originalFileName = StringUtils.cleanPath(profileImage.getOriginalFilename());
            String filePath = uploadPath + originalFileName;
            File destinationFile = new File(filePath);
            destinationFile.getParentFile().mkdirs();
            profileImage.transferTo(destinationFile);
            profileData.setImagePath("/uploads/" + originalFileName);
        }
    }

    //Save or update the recipient profile in the database
    public RecipientProfile saveOrUpdateProfile(RecipientProfile profile) {
        // Retrieve the existing profile, if present
        Optional<RecipientProfile> existingProfile = recipientProfileRepository.findByUserId(profile.getUser().getId());

        if (existingProfile.isPresent()) {
            // Copy non-null properties from incoming profile to the existing profile
            RecipientProfile profileToUpdate = existingProfile.get();
            BeanUtils.copyProperties(profile, profileToUpdate, "recipientProfileId", "user");
            return recipientProfileRepository.save(profileToUpdate);
        } else {
            // Save a new profile
            return recipientProfileRepository.save(profile);
        }
    }

    // Finds a recipient profile by user ID
    public Optional<RecipientProfile> findByUserId(Long userId) {
        return recipientProfileRepository.findByUserId(userId);
    }

    public Page<RecipientProfile> getRecipientByType(String recipientType, Pageable pageable) {
        return recipientProfileRepository.findByRecipientType(recipientType, pageable);
    }

    public List<RecipientProfile> getProfilesByIds(List<Long> recipientProfileIds) {
        return recipientProfileRepository.findByRecipientProfileIdIn(recipientProfileIds);
    }

    public List<RecipientProfile> getProfilesByUserIds(List<Long> userIds) {
        return recipientProfileRepository.findByUserIdIn(userIds);
    }

    // Finds by profile ID
    public Optional<RecipientProfile> findByProfileId(Long recipientProfileId) {
        return recipientProfileRepository.findById(recipientProfileId);
    }
}
