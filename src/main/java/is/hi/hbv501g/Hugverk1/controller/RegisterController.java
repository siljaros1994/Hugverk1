package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Model.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Model.MyAppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// This controller handles user registration requests from the frontend and interacts with the database.
//  This is where new users are created and stored in the MyAppUsers table.

@RestController
public class RegisterController {

    private final MyAppUserRepository myAppUserRepository;
    private final PasswordEncoder passwordEncoder;

    // The myAppUserRepository allows the controller to save new users to the database and the PasswordEncoder is for hashing passwords.
    public RegisterController(MyAppUserRepository myAppUserRepository, PasswordEncoder passwordEncoder) {
        this.myAppUserRepository = myAppUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // this is the /register endpoint, which handles HTTP POST requests with JSON data for the users registration.
    @PostMapping(value = "/register", consumes = "application/json")
    public MyAppUsers createUser(@RequestBody MyAppUsers user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return myAppUserRepository.save(user); //  saves the new user to the MyAppUsers table in the database.
    }
}
