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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/messages")
@SessionAttributes("user")
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

        Long senderId = currentUser.getId();
        Long receiverId = userId;

        Boolean showInstruction = (Boolean) session.getAttribute("showInstruction");
        if (showInstruction == null || showInstruction) {
            session.setAttribute("showInstruction", true);
            model.addAttribute("showInstruction", true);
        } else {
            model.addAttribute("showInstruction", false);
        }

        if (receiverId == null || receiverId.equals(senderId)) {
            model.addAttribute("errorMessage", "Please select a user from the list to start a conversation.");
            model.addAttribute("chats", Collections.emptyList());
        } else {
            List<Message> conversation = messageService.getConversationBetween(senderId, receiverId);
            model.addAttribute("chats", conversation);
        }

        System.out.println("Current User ID (senderId): " + senderId);
        System.out.println("Selected Donor/Recipient ID (receiverId): " + receiverId);

        List<MyAppUsers> matchedUsers = userService.getMatchedUsers(currentUser.getId(), currentUser.getUserType());

        model.addAttribute("user", currentUser);
        model.addAttribute("userType", userType);
        model.addAttribute("senderId", senderId);
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("matchedUsers", userService.getMatchedUsers(currentUser.getId(), currentUser.getUserType()));
        model.addAttribute("senderImagePath", getSenderImagePath(currentUser));
        model.addAttribute("messageForm", new MessageForm(receiverId));

        return "messages";
    }

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

    @PostMapping("/dismissPopup")
    public String dismissPopup(HttpSession session, @RequestParam String userType, @RequestParam Long userId) {
        session.setAttribute("showInstruction", false);
        return "redirect:/messages/" + userType + "/" + userId;
    }

    @PostMapping("/send")
    public String sendMessage(@ModelAttribute MessageForm messageForm, Model model, HttpSession session) {
        MyAppUsers sender = (MyAppUsers) session.getAttribute("user");

        if (sender == null) {
            throw new UsernameNotFoundException("User not found in session");
        }

        Long senderId = sender.getId();
        Long receiverId = messageForm.getReceiverId();

        System.out.println("Attempting to send message:");
        System.out.println("Sender ID: " + senderId);
        System.out.println("Receiver ID: " + receiverId);
        System.out.println("Message Content: " + messageForm.getText());

        if (receiverId == null || receiverId.equals(senderId)) {
            model.addAttribute("errorMessage", "Please select a valid user to start a conversation.");
            return "messages";
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