package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.LoginRequest;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private MyAppUserService myAppUserService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.GET)
    public ResponseEntity<String> authenticateUser(@RequestHeader HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    return new ResponseEntity<>("User authenticated successfully!", HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("Invalid username or password.", HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<String> authenticatePostUser(@RequestBody LoginRequest loginRequest) {
        Optional<MyAppUsers> user = myAppUserService.findByUsername(loginRequest.getUsername());

        if (user.isPresent() && myAppUserService.matchPassword(loginRequest.getPassword(), user.get().getPassword())) {
            return new ResponseEntity<>("User authenticated successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid username or password.", HttpStatus.UNAUTHORIZED);
        }
    }
}