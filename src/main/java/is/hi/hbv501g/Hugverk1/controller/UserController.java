package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Model.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Model.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

// This controller handles user registration requests from the frontend and interacts with the database.
//  This is where new users are created and stored in the MyAppUsers table.

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private MyAppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody MyAppUsers user) {
        // Log the received user data
        System.out.println("Received user data: " + user);

        // Validate if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            System.out.println("Username already exists!");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("message", "Username already exists!"));
        }

        // Validate password match
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            System.out.println("Passwords do not match!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Passwords do not match!"));
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Check for user type (Donor or Recipient)
        if ("donor".equalsIgnoreCase(user.getUserType())) {
            user.assignDonorId();
        } else if ("recipient".equalsIgnoreCase(user.getUserType())) {
            user.assignRecipientId();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "You must register as either a donor or recipient."));
        }

        // Save the user to the database
        userRepository.save(user);
        System.out.println("User registered successfully!");
        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully!"));
    }
}