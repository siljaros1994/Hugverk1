package is.hi.hbv501g.Hugverk1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @GetMapping("/donor")
    public String showDonorMessages(Model model) {
        return "donormessages";  // returns donormessages.html
    }

    @GetMapping("/recipient")
    public String showRecipientMessages(Model model) {
        return "recipientmessages";  // returns recipientmessages.html
    }
}