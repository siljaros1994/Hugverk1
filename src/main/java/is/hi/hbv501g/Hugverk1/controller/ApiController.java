package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.LoginRequest;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.LoginResponse;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private MyAppUserService myAppUserService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<MyAppUsers> user = myAppUserService.findByUsername(loginRequest.getUsername());

        if (user.isPresent() && myAppUserService.matchPassword(loginRequest.getPassword(), user.get().getPassword())) {
            MyAppUsers loggedInUser = user.get();
            LoginResponse response = new LoginResponse(
                    "success", // Token or session ID
                    loggedInUser.getId(),
                    loggedInUser.getUserType()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(new LoginResponse("Invalid credentials", -1, null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody MyAppUsers user) {
        if (myAppUserService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new LoginResponse("Username already exists", -1, null));
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new LoginResponse("Passwords do not match", -1, null));
        }

        if (user.getUserType() == null || user.getUserType().isEmpty()) {
            return ResponseEntity.badRequest().body(new LoginResponse("Please select a valid user type", -1, null));
        }

        myAppUserService.saveUser(user);
        return ResponseEntity.ok(new LoginResponse("User registered successfully", user.getId(), user.getUserType()));
    }
}