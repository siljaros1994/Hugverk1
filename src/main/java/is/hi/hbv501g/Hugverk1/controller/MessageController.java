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

    // Display recipient messages page with profile images
    @GetMapping("/recipient")
    public String showMessagesRecipient(Model model, Principal principal) {
        Optional<MyAppUsers> currentUserOptional = userService.findByUsername(principal.getName());

        if (currentUserOptional.isPresent()) {
            MyAppUsers currentUser = currentUserOptional.get();
            String recipientId = currentUser.getRecipientId();

            // Set the default paths to the uploaded images
            String senderImagePath;
            if ("donor".equalsIgnoreCase(currentUser.getUserType())) {
                Optional<DonorProfile> donorProfile = donorProfileService.findByUserDonorId(currentUser.getDonorId());
                senderImagePath = donorProfile.map(DonorProfile::getImagePath).orElse("/uploads/default-donor-avatar.png");
            } else {
                senderImagePath = "/uploads/default-recipient-avatar.png";
            }

            model.addAttribute("senderId", recipientId);
            model.addAttribute("recipientId", recipientId);
            model.addAttribute("chats", messageService.getConversation(recipientId, recipientId));
            model.addAttribute("messageForm", new MessageForm());
            model.addAttribute("senderImagePath", senderImagePath);

        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
        return "messages";
    }

    // Endpoint for sending messages
    @PostMapping("/send")
    public String sendMessage(@ModelAttribute MessageForm messageForm, Model model, Principal principal) {
        Optional<MyAppUsers> currentUserOptional = userService.findByUsername(principal.getName());

        if (currentUserOptional.isPresent()) {
            MyAppUsers currentUser = currentUserOptional.get();
            Message newMessage = new Message();
            newMessage.setSenderId(currentUser.getRecipientId());
            newMessage.setRecipientId(messageForm.getRecipientId());
            newMessage.setContent(messageForm.getText());
            newMessage.setTimestamp(LocalDateTime.now());
            messageService.saveMessage(newMessage);

            // Re-fetch the conversation and other attributes
            model.addAttribute("chats", messageService.getConversation(currentUser.getRecipientId(), messageForm.getRecipientId()));
            model.addAttribute("senderId", currentUser.getRecipientId());
            model.addAttribute("recipientId", messageForm.getRecipientId());
            model.addAttribute("messageForm", new MessageForm());

            // Determine sender image path based on user type and profile image
            String senderImagePath;
            if ("donor".equalsIgnoreCase(currentUser.getUserType())) {
                Optional<DonorProfile> donorProfile = donorProfileService.findByUserDonorId(currentUser.getDonorId());
                senderImagePath = donorProfile.map(DonorProfile::getImagePath).orElse("/uploads/default-donor-avatar.png");
            } else {
                senderImagePath = "/uploads/default-recipient-avatar.png";
            }
            model.addAttribute("senderImagePath", senderImagePath);
        }

        return "messages";
    }
}