package is.hi.hbv501g.Hugverk1.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.*;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.MessageService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import is.hi.hbv501g.Hugverk1.Persistence.forms.MessageForm;
import java.time.LocalDateTime;
import is.hi.hbv501g.Hugverk1.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private MessageService messageService;

    @PostMapping("/users/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Force creation of session and store the security.
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            MyAppUsers user = myAppUserService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            LoginResponse response = new LoginResponse("success", user.getId(), user.getUserType(), user.getUsername());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(401).body(new LoginResponse("Invalid credentials", -1, null, null));

        }
    }

    @PostMapping("/users/register")
    public ResponseEntity<LoginResponse> register(@RequestBody MyAppUsers user) {
        if (myAppUserService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new LoginResponse("Username already exists", -1, null, null));
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new LoginResponse("Passwords do not match", -1, null, null));
        }

        if (user.getUserType() == null || user.getUserType().isEmpty()) {
            return ResponseEntity.badRequest().body(new LoginResponse("Please select a valid user type", -1, null, null));
        }

        myAppUserService.saveUser(user);
        return ResponseEntity.ok(new LoginResponse("User registered successfully", user.getId(), user.getUserType(),user.getUsername()));
    }

    @GetMapping("/recipient/profile/{userId}")
    public ResponseEntity<?> getRecipientProfile(@PathVariable Long userId) {
        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null || !loggedInUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }
        Optional<RecipientProfile> profileOpt = recipientProfileService.findByUserId(userId);

        RecipientProfileDTO dto;
        if (profileOpt.isPresent()) {
            dto = RecipientProfileConverter.convertToDTO(profileOpt.get());
        } else {
            // Return an empty profile DTO.
            dto = new RecipientProfileDTO();
            dto.setUserId(userId);
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/donor/profile/{userId}")
    public ResponseEntity<?> getDonorProfile(@PathVariable Long userId) {
        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null || !loggedInUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }
        Optional<DonorProfile> profileOpt = donorProfileService.findByUserId(userId);

        DonorProfileDTO dto;
        if (profileOpt.isPresent()) {
            dto = DonorProfileConverter.convertToDTO(profileOpt.get());
        } else {
            // Return an empty profile DTO.
            dto = new DonorProfileDTO();
            dto.setUserId(userId);
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/donor/all")
    public ResponseEntity<List<DonorProfileDTO>> getAllDonors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("age").ascending());
        Page<DonorProfile> donorPage = donorProfileService.findAll(pageable);
        List<DonorProfileDTO> dtoList = donorPage.getContent().stream()
                .map(profile -> DonorProfileConverter.convertToDTO(profile))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/recipient/profile/saveOrEdit")
    public ResponseEntity<RecipientProfileDTO> saveOrEditRecipientProfile(@RequestBody RecipientProfile profile,
                                                                          HttpServletRequest request) {
        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null || (profile.getUser() != null && !loggedInUser.getId().equals(profile.getUser().getId()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RecipientProfile profileToSave;
        Optional<RecipientProfile> existingOpt = recipientProfileService.findByUserId(loggedInUser.getId());
        if (existingOpt.isPresent()) {
            RecipientProfile existing = existingOpt.get();
            BeanUtils.copyProperties(profile, existing, "recipientProfileId", "user");
            profileToSave = existing;
        } else {
            profile.setUser(loggedInUser);
            profileToSave = profile;
        }

        RecipientProfile updatedProfile = recipientProfileService.saveOrUpdateProfile(profileToSave);
        loggedInUser.setRecipientProfile(updatedProfile);
        myAppUserRepository.save(loggedInUser);
        RecipientProfileDTO dto = RecipientProfileConverter.convertToDTO(updatedProfile);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/donor/profile/saveOrEdit")
    public ResponseEntity<DonorProfileDTO> saveOrEditDonorProfile(@RequestBody DonorProfile profile,
                                                                  HttpServletRequest request) {
        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null || (profile.getUser() != null && !loggedInUser.getId().equals(profile.getUser().getId()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        DonorProfile profileToSave;
        Optional<DonorProfile> existingOpt = donorProfileService.findByUserId(loggedInUser.getId());
        if (existingOpt.isPresent()) {
            DonorProfile existing = existingOpt.get();
            // Copy all matching properties, ignoring the ID and user fields.
            BeanUtils.copyProperties(profile, existing, "donorProfileId", "user");
            profileToSave = existing;
        } else {
            profile.setUser(loggedInUser);
            profileToSave = profile;
        }

        DonorProfile updatedProfile = donorProfileService.saveOrUpdateProfile(profileToSave);
        loggedInUser.setDonorProfile(updatedProfile);
        myAppUserRepository.save(loggedInUser);
        DonorProfileDTO dto = DonorProfileConverter.convertToDTO(updatedProfile);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/donor/view/{donorProfileId}")
    public ResponseEntity<?> viewDonorProfile(@PathVariable Long donorProfileId) {
        Optional<DonorProfile> profileOpt = donorProfileService.findByProfileId(donorProfileId);
        if (!profileOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donor profile not found.");
        }
        // Convert to DTO – imagePath is already a full URL.
        DonorProfileDTO dto = DonorProfileConverter.convertToDTO(profileOpt.get());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "File is empty"));
        }
        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "dlfi65u4o",
                    "api_key", "753673256719356",
                    "api_secret", "JAex3aiR466-p62KFPM3WQ15Z_I"));

            // Here we upload the file
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String fileUrl = (String) uploadResult.get("secure_url");

            // Here we return the URL in our response
            return ResponseEntity.ok(Collections.singletonMap("fileUrl", fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error uploading file"));
        }
    }


    @PostMapping("/users/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        //Get the session if it exists
        HttpSession session = request.getSession(false);

        if (session != null) {
            //Invalidate the session
            session.invalidate();
        }

        //Clear the security context
        SecurityContextHolder.clearContext();

        //Return logout success response
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Logged out successfully"));
    }



    @GetMapping("/users/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<MyAppUsers> users = myAppUserService.findAllUsers();
        List<UserDTO> userDTOs = users.stream().map(user -> new UserDTO(user.getId(), user.getUsername(), user.getUserType())).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }


    @GetMapping("/messages/{userType}/{id}")
    public ResponseEntity<List<MessageDTO>> getMessages(
            @PathVariable String userType,
            @PathVariable Long id) {

        MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedInUser == null || !loggedInUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Message> messages = messageService.getConversationBetween(loggedInUser.getId(), id);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(MessageConverter::convertToDTO)
                .toList();

        return ResponseEntity.ok(messageDTOs);
    }
}
