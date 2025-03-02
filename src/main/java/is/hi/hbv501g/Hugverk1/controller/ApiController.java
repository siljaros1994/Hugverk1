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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @GetMapping("/donor/all")
    public ResponseEntity<List<DonorProfileDTO>> getAllDonors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("age").ascending());
        Page<DonorProfile> donorPage = donorProfileService.findAll(pageable);
        List<DonorProfileDTO> dtoList = donorPage.getContent().stream()
                .map(DonorProfileConverter::convertToDTO)
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

        RecipientProfile updatedProfile;
        Optional<RecipientProfile> existingProfile = recipientProfileService.findByUserId(loggedInUser.getId());
        if (existingProfile.isPresent()) {
            RecipientProfile profileToUpdate = existingProfile.get();
            profileToUpdate.setHairColor(profile.getHairColor());
            profileToUpdate.setEyeColor(profile.getEyeColor());
            profileToUpdate.setBloodType(profile.getBloodType());
            BeanUtils.copyProperties(profile, profileToUpdate, "recipientProfileId", "user");
            updatedProfile = recipientProfileService.saveOrUpdateProfile(profileToUpdate);
        } else {
            profile.setUser(loggedInUser);
            updatedProfile = recipientProfileService.saveOrUpdateProfile(profile);
        }
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
        Optional<DonorProfile> existingProfile = donorProfileService.findByUserId(loggedInUser.getId());
        if (existingProfile.isPresent()) {
            DonorProfile existing = existingProfile.get();
            existing.setEyeColor(profile.getEyeColor());
            existing.setHairColor(profile.getHairColor());
            existing.setEducationLevel(profile.getEducationLevel());
            existing.setRace(profile.getRace());
            existing.setEthnicity(profile.getEthnicity());
            existing.setBloodType(profile.getBloodType());
            existing.setMedicalHistory(profile.getMedicalHistory());
            existing.setHeight(profile.getHeight());
            existing.setWeight(profile.getWeight());
            existing.setAge(profile.getAge());
            existing.setGetToKnow(profile.getGetToKnow());

            if (profile.getImagePath() != null && !profile.getImagePath().isEmpty()) {
                System.out.println("Updating imagePath to: " + profile.getImagePath());
                existing.setImagePath(profile.getImagePath());
            }
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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to create upload directory");
                }
            }
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            File destinationFile = new File(uploadDir, uniqueFilename);
            file.transferTo(destinationFile);
            String filePath = "/uploads/" + uniqueFilename;
            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file");
        }
    }
}
