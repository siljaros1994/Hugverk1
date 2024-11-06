package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.forms.MessageForm;
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

    @GetMapping("/{userType}")
    public String showMessages(@PathVariable("userType") String userType, Model model, Principal principal) {
        Optional<MyAppUsers> currentUserOptional = userService.findByUsername(principal.getName());

        if (currentUserOptional.isPresent()) {
            MyAppUsers currentUser = currentUserOptional.get();
            Long senderId = "donor".equalsIgnoreCase(userType) ? currentUser.getDonorId() : currentUser.getRecipientId();
            Long receiverId = senderId; // For now, set to senderId. This can be replaced with the actual receiver's ID when available.

            // Set sender's image path based on userType, donor or recipient.
            String senderImagePath = "/uploads/default-recipient-avatar.png";
            if ("donor".equalsIgnoreCase(currentUser.getUserType())) {
                senderImagePath = donorProfileService.findByUserDonorId(currentUser.getDonorId())
                        .map(DonorProfile::getImagePath)
                        .orElse("/uploads/default-donor-avatar.png");
            }

            model.addAttribute("senderId", senderId);
            model.addAttribute("receiverId", receiverId);
            model.addAttribute("chats", messageService.getConversation(senderId, receiverId));
            model.addAttribute("messageForm", new MessageForm());
            model.addAttribute("senderImagePath", senderImagePath);
            model.addAttribute("userType", userType);
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
        return "messages";
    }

    @PostMapping("/send")
    public String sendMessage(@ModelAttribute MessageForm messageForm, Model model, Principal principal) {
        Optional<MyAppUsers> currentUserOptional = userService.findByUsername(principal.getName());

        if (currentUserOptional.isPresent()) {
            MyAppUsers currentUser = currentUserOptional.get();
            Long senderId = currentUser.getUserType().equalsIgnoreCase("donor") ? currentUser.getDonorId() : currentUser.getRecipientId();
            Long receiverId = messageForm.getReceiverId();

            Message newMessage = new Message();
            newMessage.setSenderId(senderId);
            newMessage.setReceiverId(receiverId);
            newMessage.setContent(messageForm.getText());
            newMessage.setTimestamp(LocalDateTime.now());
            messageService.saveMessage(newMessage);

            model.addAttribute("chats", messageService.getConversation(senderId, receiverId));
            model.addAttribute("senderId", senderId);
            model.addAttribute("receiverId", receiverId);
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