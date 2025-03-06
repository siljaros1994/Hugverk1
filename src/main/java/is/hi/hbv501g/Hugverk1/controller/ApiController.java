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
import org.springframework.beans.factory.annotation.Value;
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
import java.io.File;
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

    // Inject the upload path from application.properties
    @Value("${upload.path}")
    private String uploadPath;

    private final String BASE_URL = "http://192.168.101.4:8080";

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
            // return an empty profile DTO.
            dto = new RecipientProfileDTO();
            dto.setUserId(userId);
        }
        // Format the image URL
        String imagePath = dto.getImagePath();
        if (imagePath != null && !imagePath.startsWith("http")) {
            dto.setImagePath(BASE_URL + (imagePath.startsWith("/") ? "" : "/") + imagePath);
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
            // return an empty profile DTO.
            dto = new DonorProfileDTO();
            dto.setUserId(userId);
        }
        // Format the image URL
        String imagePath = dto.getImagePath();
        if (imagePath != null && !imagePath.startsWith("http")) {
            dto.setImagePath(BASE_URL + (imagePath.startsWith("/") ? "" : "/") + imagePath);
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
                .map(profile -> {
                    DonorProfileDTO dto = DonorProfileConverter.convertToDTO(profile);
                    String imagePath = dto.getImagePath();
                    if (imagePath != null && !imagePath.startsWith("http")) {
                        dto.setImagePath(BASE_URL + (imagePath.startsWith("/") ? "" : "/") + imagePath);
                    }
                    return dto;
                })
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
        if (dto.getImagePath() != null && !dto.getImagePath().startsWith("http")) {
            dto.setImagePath(BASE_URL + (dto.getImagePath().startsWith("/") ? "" : "/") + dto.getImagePath());
        }
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
        String imagePath = dto.getImagePath();
        if (imagePath != null && !imagePath.startsWith("http")) {
            dto.setImagePath(BASE_URL + (imagePath.startsWith("/") ? "" : "/") + imagePath);
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "File is empty"));
        }
        try {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            File destinationFile = new File(uploadDir, uniqueFilename);
            file.transferTo(destinationFile);

            String fileUrl = BASE_URL + "/uploads/" + uniqueFilename;
            // Here we return as a JSON object
            return ResponseEntity.ok(Collections.singletonMap("fileUrl", fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error saving file"));
        }
    }
}