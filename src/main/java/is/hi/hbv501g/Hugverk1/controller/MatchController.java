package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MyAppUserService myAppUserService;

    //@Autowired
    //private MatchController(MyAppUserService myAppUserService) {
    //  this.myAppUserService = myAppUserService;
    //}

    @GetMapping("/matches")
    public String matches(Model model, HttpSession session) {
        Long donorId = (Long) session.getAttribute("donorId");
        if (donorId !=null) {
            List<Long> matches = myAppUserService.getMatchRecipients(donorId);
            model.addAttribute("matches", matches);
        }
        return "matches";
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMatchRecipient(@RequestParam Long donorId, @RequestParam Long recipientId) {
        try {
            myAppUserService.addMatchRecipient(donorId,recipientId);
            return ResponseEntity.ok("Recipient added to matches");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Long>> getMatchRecipients(@RequestParam Long donorId) {
        try {
            List<Long> matches = myAppUserService.getMatchRecipients(donorId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
