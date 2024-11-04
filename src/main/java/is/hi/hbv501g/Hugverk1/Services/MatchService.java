package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.DonorProfileRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.RecipientProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {
    @Autowired
    private DonorProfileRepository donorProfileRepository;

```
    @Autowired
    private RecipientProfileRepository recipientProfileRepository;

    public void favoriteRecipient(Long donorId, Long recipientId) {
        donorId donorid = donorProfileRepository.findById(donorId).orElseThrow();
        recipientId recipientid = recipientProfileRepository.findById(recipientId).orElseThrow();
        donorId.getFavoritedRecipients().add(recipientid);
        donorProfileRepository.save(donorid);
    }

    public void favoriteDonor(long recipientId, Long donorId) {
        recipientId recipientid = recipientProfileRepository.findById(recipientId).orElseThrow();
        donorId donorid = donorProfileRepository.findById(donorId).orElseThrow();
        recipientid.getFavoritedDonorsId().add(donorid);
        recipientProfileRepository.save(recipientid);
    }

    //Checks if there is a mutual match
    public boolean isMatch(Long donorId, Long recipientId) {
        DonorProfile donorProfile = donorProfileRepository.findById(recipientId).orElseThrow();
        return donorId.getFavoritedRecipientsId().contains(recipientId) && recipientId.getFavoritedDonors().contains(donorId);
    }

```

}