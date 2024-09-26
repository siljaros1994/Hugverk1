package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Model.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Model.MyAppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    private final MyAppUserRepository myAppUserRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public RegisterController(MyAppUserRepository myAppUserRepository, PasswordEncoder passwordEncoder) {
        this.myAppUserRepository = myAppUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public MyAppUsers createUser(@RequestBody MyAppUsers user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return myAppUserRepository.save(user);
    }
}
