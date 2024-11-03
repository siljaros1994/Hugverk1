package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.RecipientProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipientProfileService {
    @Autowired
    private RecipientProfileRepository recipientProfileRepository;

    //Save or update the recipient profile in the database
    public void saveOrUpdateProfile(RecipientProfile profile) {
        recipientProfileRepository.save(profile);
    }

    //Finds a recipient profile by the recipient ID, unique identifier for each recipient
    public Optional<RecipientProfile> findByUserRecipientId(String recipientId) {
        return recipientProfileRepository.findByUserRecipientId(recipientId);
    }

    public Optional<RecipientProfile> findByProfileId(Long profileId) {
        return recipientProfileRepository.findById(profileId);
    }

    //Get all the recipients by type, like single parent or couple
    public Page<RecipientProfile> getRecipientsByType(String recipientType, Pageable pageable) {
        return recipientProfileRepository.findByRecipientType(recipientType, pageable);
    }

    public List<RecipientProfile> getAllRecipients() {
        return recipientProfileRepository.findAll();
    }

    //Retrieve all recipient profile with pagination
    public Page<RecipientProfile> findAll(Pageable pageable) {
        return recipientProfileRepository.findAll(pageable);
    }
}
