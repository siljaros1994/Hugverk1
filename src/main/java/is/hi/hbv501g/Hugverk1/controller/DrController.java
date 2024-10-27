package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DrController {

    @Autowired
    private MyAppUserService myAppUserService; // Inject the service

    @GetMapping("/dr") // Adjust the URL as needed
    public String displayUsers(Model model) {
        List<MyAppUsers> users = myAppUserService.findAllUsers(); // Use the service to fetch all users
        model.addAttribute("users", users); // Add users to the model
        return "dr"; // Return the name of the HTML template to display users
    }
}
