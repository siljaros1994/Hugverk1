package is.hi.hbv501g.Hugverk1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterController {

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";  // Refers to register.html
    }
}
