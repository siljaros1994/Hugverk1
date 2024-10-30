package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    public FavoriteController(MyAppUserService myAppUserService) {
        this.myAppUserService = myAppUserService;
    }
    @GetMapping("/favorites")
    public String favorites(Model model, HttpSession session) {
        Long recipientId = (Long) session.getAttribute("recipientId");
        if (recipientId != null) {
            List<Long> favorites = myAppUserService.getFavoriteDonors(recipientId);
            model.addAttribute("favorites", favorites);
        }
        return "favorites";
    }


    @PostMapping("/add")
    public ResponseEntity<String> addFavoriteDonor(@RequestParam Long recipientId, @RequestParam Long donorId) {
        try {
            myAppUserService.addFavoriteDonor(recipientId, donorId);
            return ResponseEntity.ok("Donor added to favorites");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<Long>> getFavoriteDonors(@RequestParam Long recipientId) {
        try {
            List<Long> favorites = myAppUserService.getFavoriteDonors(recipientId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}