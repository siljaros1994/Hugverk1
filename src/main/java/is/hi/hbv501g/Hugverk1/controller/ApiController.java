package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.*;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import is.hi.hbv501g.Hugverk1.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RecipientProfileService recipientProfileService;

    @Autowired
    private DonorProfileService donorProfileService;

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @PostMapping("/users/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Force creation of session and store the security context
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            MyAppUsers user = myAppUserService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            LoginResponse response = new LoginResponse("success", user.getId(), user.getUserType());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(401).body(new LoginResponse("Invalid credentials", -1, null));
        }
    }

    @PostMapping("/users/register")
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

    @GetMapping("/recipient/profile/{userId}")
    public ResponseEntity<?> getRecipientProfile(@PathVariable Long userId) {
        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null || !loggedInUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }
        Optional<RecipientProfile> profileOpt = recipientProfileService.findByUserId(userId);
        if (profileOpt.isPresent()) {
            RecipientProfileDTO dto = RecipientProfileConverter.convertToDTO(profileOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found.");
        }
    }

    @GetMapping("/donor/profile/{userId}")
    public ResponseEntity<?> getDonorProfile(@PathVariable Long userId) {
        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null || !loggedInUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }
        Optional<DonorProfile> profileOpt = donorProfileService.findByUserId(userId);
        if (profileOpt.isPresent()) {
            DonorProfileDTO dto = DonorProfileConverter.convertToDTO(profileOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found.");
        }
    }

    // Here we accepts a JSON recipient profile, that updates or saves the profile.
    @PostMapping("/recipient/profile/saveOrEdit")
    public ResponseEntity<RecipientProfileDTO> saveOrEditRecipientProfile(@RequestBody RecipientProfile profile,
                                                                          HttpServletRequest request) {
        // Retrieve the logged-in user from the security context
        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null || (profile.getUser() != null && !loggedInUser.getId().equals(profile.getUser().getId()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RecipientProfile updatedProfile;
        Optional<RecipientProfile> existingProfile = recipientProfileService.findByUserId(loggedInUser.getId());
        if (existingProfile.isPresent()) {
            RecipientProfile profileToUpdate = existingProfile.get();
            // Copy properties from the incoming profile to the existing one, ignoring recipientProfileId and user.
            BeanUtils.copyProperties(profile, profileToUpdate, "recipientProfileId", "user");
            updatedProfile = recipientProfileService.saveOrUpdateProfile(profileToUpdate);
        } else {
            profile.setUser(loggedInUser);
            updatedProfile = recipientProfileService.saveOrUpdateProfile(profile);
        }
        loggedInUser.setRecipientId(updatedProfile.getRecipientProfileId());
        myAppUserRepository.save(loggedInUser);

        // Convert the updated entity to a DTO to break any cyclic references.
        RecipientProfileDTO dto = RecipientProfileConverter.convertToDTO(updatedProfile);
        return ResponseEntity.ok(dto);
    }
}