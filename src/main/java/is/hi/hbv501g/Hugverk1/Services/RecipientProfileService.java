package is.hi.hbv501g.Hugverk1.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.RecipientProfileRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecipientProfileService {

    @Autowired
    private RecipientProfileRepository recipientProfileRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    public RecipientProfileService(RecipientProfileRepository recipientProfileRepository, @Value("${upload.path}") String uploadPath) {
        this.recipientProfileRepository = recipientProfileRepository;
        this.uploadPath = uploadPath;

        System.out.println("Upload path resolved to: " + this.uploadPath);
    }

    public RecipientProfile findOrCreateProfile(MyAppUsers user) {
        return recipientProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    RecipientProfile newProfile = new RecipientProfile();
                    newProfile.setUser(user);
                    return recipientProfileRepository.save(newProfile);
                });
    }

    public void processProfileImage(RecipientProfile profile, MultipartFile profileImage) throws IOException {
        if (!profileImage.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(profileImage.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");

            profile.setImagePath(imageUrl);
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
        List<RecipientProfile> profiles = recipientProfileRepository.findByUserIdIn(userIds);
        profiles.forEach(profile -> Hibernate.initialize(profile.getUser())); // Initialize the user field
        return profiles;
    }

    // Finds by profile ID
    public Optional<RecipientProfile> findByProfileId(Long recipientProfileId) {
        return recipientProfileRepository.findById(recipientProfileId);
    }

    public void ensureUploadDirectoryExists() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                System.out.println("Upload directory created: " + uploadPath);
            } else {
                System.err.println("Failed to create upload directory: " + uploadPath);
            }
        }
    }
}
