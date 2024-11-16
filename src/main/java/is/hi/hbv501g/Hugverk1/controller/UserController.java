package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// This controller handles user registration requests from the frontend and interacts with the database.
//  This is where new users are created and stored in the MyAppUsers table.

@Controller
@RequestMapping("/users")
@SessionAttributes("user")
public class UserController extends BaseController {

    @Autowired
    private MyAppUserService myAppUserService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginForm(Model model, @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("message", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        model.addAttribute("user", new MyAppUsers());
        return "login";  // Return the login page
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new MyAppUsers());
        return "register"; // Return the registration page
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
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

        // Ensure the userType is set
        if (user.getUserType() == null || user.getUserType().isEmpty()) {
            model.addAttribute("message", "Please select a valid user type.");
            return "register";  // Return registration page with an error message
        }

        myAppUserService.saveUser(user); // Save the user to the database
        model.addAttribute("message", "User registered successfully!");
        return "login";  // Redirect to login page with success message
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginUser(@ModelAttribute("user") MyAppUsers user, Model model, HttpSession session) {
        Optional<MyAppUsers> foundUser = myAppUserService.findByUsername(user.getUsername());

        if (foundUser.isPresent()) {
            // Check if password matches
            if (myAppUserService.matchPassword(user.getPassword(), foundUser.get().getPassword())) {
                MyAppUsers loggedInUser = foundUser.get();

                // Store the full user object and its attributes in the session
                session.setAttribute("user", loggedInUser);
                session.setAttribute("userId", loggedInUser.getId());
                session.setAttribute("userType", loggedInUser.getUserType());

                model.addAttribute("userType", loggedInUser.getUserType());

                System.out.println("User and attributes stored in session.");
                System.out.println("Stored in session - userId: " + session.getAttribute("userId"));
                System.out.println("Stored in session - userType: " + session.getAttribute("userType"));

                // Redirect based on user type
                if ("donor".equalsIgnoreCase(loggedInUser.getUserType())) {
                    return "redirect:/home/donor";
                } else if ("recipient".equalsIgnoreCase(loggedInUser.getUserType())) {
                    return "redirect:/home/recipient";
                } else if ("admin".equalsIgnoreCase(loggedInUser.getUserType())) {
                    return "redirect:/home/admin";
                } else {
                    model.addAttribute("message", "Unexpected user type.");
                    return "login";
                }
            } else {
                model.addAttribute("message", "Invalid password.");
                return "login";
            }
        } else {
            model.addAttribute("message", "Invalid username.");
            return "login";
        }
    }

    // Endpoint to get all users and return as JSON
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<MyAppUsers> getAllUsers() {
        List<MyAppUsers> users = myAppUserService.findAllUsers();
        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
            return Collections.emptyList();  // Return an empty list if there are no users to be found.
        }
        return users;
    }
}
