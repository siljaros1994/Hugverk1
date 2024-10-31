package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MessageForm;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MessageService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MyAppUserService userService;

    @Autowired
    private DonorProfileService donorProfileService;

    // needs to be updated with match.
    @GetMapping("/{userType}")
    public String showMessages(@PathVariable("userType") String userType, Model model, Principal principal) {
        Optional<MyAppUsers> currentUserOptional = userService.findByUsername(principal.getName());

        if (currentUserOptional.isPresent()) {
            MyAppUsers currentUser = currentUserOptional.get();
            String senderId = "donor".equalsIgnoreCase(userType) ? currentUser.getDonorId() : currentUser.getRecipientId();
            String recipientId = senderId;

            // Set sender's image path based on userType, donor or recipient.
            String senderImagePath = "/uploads/default-recipient-avatar.png";
            if ("donor".equalsIgnoreCase(currentUser.getUserType())) {
                Optional<DonorProfile> donorProfile = donorProfileService.findByUserDonorId(currentUser.getDonorId());
                senderImagePath = donorProfile.map(DonorProfile::getImagePath).orElse("/uploads/default-donor-avatar.png");
            }

            model.addAttribute("senderId", senderId);
            model.addAttribute("recipientId", recipientId);
            model.addAttribute("chats", messageService.getConversation(senderId, recipientId));
            model.addAttribute("messageForm", new MessageForm());
            model.addAttribute("senderImagePath", senderImagePath);
            model.addAttribute("userType", userType);
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
        return "messages";
    }

    // needs to be updated with match.
    @PostMapping("/send")
    public String sendMessage(@ModelAttribute MessageForm messageForm, Model model, Principal principal) {
        Optional<MyAppUsers> currentUserOptional = userService.findByUsername(principal.getName());

        if (currentUserOptional.isPresent()) {
            MyAppUsers currentUser = currentUserOptional.get();
            String senderId = currentUser.getUserType().equalsIgnoreCase("donor") ? currentUser.getDonorId() : currentUser.getRecipientId();
            String recipientId = messageForm.getRecipientId();

            Message newMessage = new Message();
            newMessage.setSenderId(senderId);
            newMessage.setRecipientId(recipientId);
            newMessage.setContent(messageForm.getText());
            newMessage.setTimestamp(LocalDateTime.now());
            messageService.saveMessage(newMessage);

            model.addAttribute("chats", messageService.getConversation(senderId, recipientId));
            model.addAttribute("senderId", senderId);
            model.addAttribute("recipientId", recipientId);
            model.addAttribute("messageForm", new MessageForm());

            String senderImagePath = "donor".equalsIgnoreCase(currentUser.getUserType())
                    ? donorProfileService.findByUserDonorId(currentUser.getDonorId())
                    .map(DonorProfile::getImagePath)
                    .orElse("/uploads/default-donor-avatar.png")
                    : "/uploads/default-recipient-avatar.png";

            model.addAttribute("senderImagePath", senderImagePath);
        }
        return "messages";
    }
}
