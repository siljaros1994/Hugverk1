package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.DonorProfile;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;
import is.hi.hbv501g.Hugverk1.Persistence.forms.MessageForm;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MessageService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/messages")
public class MessageController extends BaseController{

    @Autowired
    private MessageService messageService;

    @Autowired
    private MyAppUserService userService;

    @Autowired
    private DonorProfileService donorProfileService;

    @Autowired
    private RecipientProfileService recipientProfileService;

    @GetMapping("/{userType}/{id}")
    public String showMessages(@PathVariable("userType") String userType, @PathVariable("id") Long userId, Model model, HttpSession session) {
        MyAppUsers currentUser = (MyAppUsers) session.getAttribute("user");

        if (currentUser == null) {
            throw new UsernameNotFoundException("User not found in session");
        }

        Long senderId = "donor".equalsIgnoreCase(currentUser.getUserType()) ? currentUser.getDonorId() : currentUser.getRecipientId();
        Long receiverId = userId;

        System.out.println("Receiver ID being set in model: " + receiverId);
        System.out.println("Navigating to messages page:");
        System.out.println("Current User ID (senderId): " + senderId);
        System.out.println("Selected Donor/Recipient ID (receiverId): " + receiverId);

        if (receiverId == null) {
            throw new IllegalArgumentException("Receiver ID cannot be null");
        }

        if ("recipient".equalsIgnoreCase(userType)) {
            Optional<RecipientProfile> recipientProfile = recipientProfileService.findByUserId(receiverId);
            if (recipientProfile.isEmpty()) {
                throw new RuntimeException("Recipient not found");
            }
        }

        List<Message> conversation = messageService.getConversationBetween(senderId, receiverId);
        System.out.println("Retrieved conversation messages (total: " + conversation.size() + "):");
        for (Message message : conversation) {
            System.out.printf("Message ID: %d, Content: '%s', Sender ID: %d, Receiver ID: %d%n",
                    message.getId(), message.getContent(), message.getSenderId(), message.getReceiverId());
        }

        if ("donor".equalsIgnoreCase(userType)) {
            List<MyAppUsers> matchedRecipients = userService.getRecipientsWhoFavoritedTheDonor(senderId);
            model.addAttribute("matchedUsers", matchedRecipients);
        } else {
            List<Long> favoriteDonors = userService.getFavoriteDonors(senderId);
            List<MyAppUsers> matchedDonors = donorProfileService.getProfilesByIds(favoriteDonors)
                    .stream()
                    .map(DonorProfile::getUser)
                    .collect(Collectors.toList());
            model.addAttribute("matchedUsers", matchedDonors);
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("userType", userType);
        model.addAttribute("senderId", senderId);
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("senderImagePath", getSenderImagePath(currentUser));
        model.addAttribute("chats", conversation);
        model.addAttribute("messageForm", new MessageForm(receiverId));


        System.out.println("senderId: " + senderId);
        System.out.println("userType: " + userType);
        System.out.println("receiverId (model): " + model.getAttribute("receiverId"));

        return "messages";
    }

    // Set profile image path based on user type
    private String getSenderImagePath(MyAppUsers user) {
        if ("donor".equalsIgnoreCase(user.getUserType())) {
            return donorProfileService.findByUserId(user.getId())
                    .map(DonorProfile::getImagePath)
                    .orElse("/uploads/default-donor-avatar.png");
        } else {
            return recipientProfileService.findByUserId(user.getId())
                    .map(RecipientProfile::getImagePath)
                    .orElse("/uploads/default-recipient-avatar.png");
        }
    }


    @PostMapping("/send")
    public String sendMessage(@ModelAttribute MessageForm messageForm, Model model, HttpSession session) {
        MyAppUsers sender = (MyAppUsers) session.getAttribute("user");

        if (sender == null) {
            throw new UsernameNotFoundException("User not found in session");
        }

        Long senderId = "donor".equalsIgnoreCase(sender.getUserType()) ? sender.getDonorId() : sender.getRecipientId();
        Long receiverId = messageForm.getReceiverId();

        System.out.println("Attempting to send message:");
        System.out.println("Sender ID: " + senderId);
        System.out.println("Receiver ID: " + receiverId);
        System.out.println("Message Content: " + messageForm.getText());

        if (receiverId == null) {
            throw new IllegalArgumentException("Receiver ID cannot be null");
        }

        Message newMessage = new Message(senderId, receiverId, messageForm.getText(), LocalDateTime.now());
        messageService.saveMessage(newMessage);

        return "redirect:/messages/" + sender.getUserType() + "/" + receiverId;
    }

    @PostMapping("/report/{receiverId}")
    public String reportUser(@PathVariable Long receiverId, Model model, HttpSession session) {
        MyAppUsers reporter = (MyAppUsers) session.getAttribute("user");

        if (reporter == null) {
            throw new UsernameNotFoundException("User not found in session");
        }

        messageService.reportUser(reporter.getId(), receiverId);
        model.addAttribute("message", "User reported successfully.");
        return "redirect:/messages/" + reporter.getUserType();
    }
}