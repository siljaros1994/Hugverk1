package is.hi.hbv501g.Hugverk1.dto;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;

import java.util.Optional;

public class MessageConverter {
    public static MessageDTO convertToDTO(Message message,
                                          MyAppUserService userService,
                                          DonorProfileService donorProfileService,
                                          RecipientProfileService recipientProfileService) {
        MyAppUsers sender = userService.findById(message.getSenderId())
                .orElse(null);
        String profileImageUrl = null;
        if (sender != null) {
            if ("donor".equalsIgnoreCase(sender.getUserType())) {
                // hér sækjum við donor prófílinn og fáum imagePath
                Optional<DonorProfile> donorProfileOpt = donorProfileService.findByUserId(sender.getId());
                if (donorProfileOpt.isPresent()) {
                    profileImageUrl = donorProfileOpt.get().getImagePath();
                }
            } else if ("recipient".equalsIgnoreCase(sender.getUserType())) {
                Optional<RecipientProfile> recipientProfileOpt = recipientProfileService.findByUserId(sender.getId());
                if (recipientProfileOpt.isPresent()) {
                    profileImageUrl = recipientProfileOpt.get().getImagePath();
                }
            }
        }

        return new MessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getTimestamp(),
                profileImageUrl
        );
    }
}
