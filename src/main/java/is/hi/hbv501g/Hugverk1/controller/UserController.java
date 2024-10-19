package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

// This controller handles user registration requests from the frontend and interacts with the database.
//  This is where new users are created and stored in the MyAppUsers table.

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private MyAppUserService myAppUserService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new MyAppUsers());
        return "login";  // Return the login page
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new MyAppUsers());
        return "register"; // Return the registration page
    }

    // Method to handle the registration form submission
    @PostMapping("/register")
    public String createUser(@ModelAttribute("user") MyAppUsers user, Model model) {
        if (myAppUserService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("message", "Username already exists!");
            return "register";  // Return registration page with an error message
        }

        // Validate that passwords match
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("message", "Passwords do not match!");
            return "register";  // Return registration page with an error message
        }

        myAppUserService.saveUser(user);
        model.addAttribute("message", "User registered successfully!");
        return "login";  // Redirect to login page with success message
    }

    // Handle the login form submission
    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") MyAppUsers loginUser, Model model, HttpSession session) {
        Optional<MyAppUsers> userOptional = myAppUserService.findByUsername(loginUser.getUsername());
        if (userOptional.isPresent()) {
            MyAppUsers user = userOptional.get();
            if (myAppUserService.matchPassword(loginUser.getPassword(), user.getPassword())) {
                session.setAttribute("LoggedInUser", user);
                return "redirect:/home";  // Redirect to home page upon successful login
            } else {
                model.addAttribute("message", "Invalid password!");
                model.addAttribute("user", new MyAppUsers());
                return "login";  // Return to login page with an error message
            }
        } else {
            model.addAttribute("message", "User not found!");
            model.addAttribute("user", new MyAppUsers());
            return "login";  // Return to login page with an error message
        }
    }

    // Display the logged inn user details
    //@GetMapping("/loggedin")
    //public String getLoggedInUser(HttpSession session, Model model) {
    //MyAppUsers loggedInUser = (MyAppUsers) session.getAttribute("LoggedInUser");
    //if (loggedInUser != null) {
    //model.addAttribute("user", loggedInUser);
    //return "loggedInUser";
    //} else {
    //model.addAttribute("message", "No user is logged in.");
    //return "redirect:/login";  // Redirect to login page if no user is logged in.
    //}
    //}
}