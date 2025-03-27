package is.hi.hbv501g.Hugverk1.controller;
//package is.hi.hbv501g.Hugverk1.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.*;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MyAppUserRepository;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.DonorProfileRepository;
import is.hi.hbv501g.Hugverk1.Services.DonorProfileService;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Services.MessageService;
import is.hi.hbv501g.Hugverk1.Services.RecipientProfileService;
import is.hi.hbv501g.Hugverk1.Services.BookingService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import is.hi.hbv501g.Hugverk1.dto.BookingDTO;
import org.springframework.security.core.Authentication;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


@RestController
//private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
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
    private DonorProfileRepository donorProfileRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private BookingService bookingService;


    @PostMapping("/users/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Here we create the HTTP session
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            // Get the user from the database
            MyAppUsers user = myAppUserService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // For donors, we need to ensure donorId is not null
            if ("donor".equalsIgnoreCase(user.getUserType())) {
                if (user.getDonorId() == null) {
                    // Here we create a default donor profile for the donor user.
                    DonorProfile defaultProfile = new DonorProfile();
                    defaultProfile.setUser(user);
                    defaultProfile = donorProfileService.saveOrUpdateProfile(defaultProfile);
                    user.setDonorId(defaultProfile.getDonorProfileId());
                    myAppUserRepository.save(user);
                }
                LoginResponse response = new LoginResponse("success", user.getId(), user.getUserType(),
                        user.getUsername(), user.getDonorId());
                return ResponseEntity.ok(response);
            }
            // For recipients we also need to ensure recipientId is not null
            else if ("recipient".equalsIgnoreCase(user.getUserType())) {
                if (user.getRecipientId() == null) {
                    // Here we create a default recipient profile for the recipient user.
                    RecipientProfile defaultProfile = new RecipientProfile();
                    defaultProfile.setUser(user);
                    defaultProfile = recipientProfileService.saveOrUpdateProfile(defaultProfile);
                    user.setRecipientId(defaultProfile.getRecipientProfileId());
                    myAppUserRepository.save(user);
                }
                LoginResponse response = new LoginResponse("success", user.getId(), user.getUserType(),
                        user.getUsername(), user.getRecipientId(), true);
                return ResponseEntity.ok(response);
            } else {
                LoginResponse response = new LoginResponse("success", user.getId(), user.getUserType(),
                        user.getUsername(), null, false);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401)
                    .body(new LoginResponse("Invalid credentials", -1, null, null, null, false));
        }
    }

    @PostMapping("/users/register")
    public ResponseEntity<LoginResponse> register(@RequestBody MyAppUsers user) {
        if (myAppUserService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Username already exists", -1, null, null, null, false));
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Passwords do not match", -1, null, null, null, false));
        }

        if (user.getUserType() == null || user.getUserType().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Please select a valid user type", -1, null, null, null, false));
        }

        myAppUserService.saveUser(user);
        if ("donor".equalsIgnoreCase(user.getUserType())) {
            return ResponseEntity.ok(new LoginResponse("User registered successfully", user.getId(), user.getUserType(), user.getUsername(), user.getDonorId()));
        } else if ("recipient".equalsIgnoreCase(user.getUserType())) {
            return ResponseEntity.ok(new LoginResponse("User registered successfully", user.getId(), user.getUserType(), user.getUsername(), user.getRecipientId(), true));
        } else {
            return ResponseEntity.ok(new LoginResponse("User registered successfully", user.getId(), user.getUserType(), user.getUsername(), null, false));
        }
    }

    @PostMapping("/migrateRecipients")
    @Transactional
    public ResponseEntity<String> migrateRecipients() {
        List<MyAppUsers> recipients = myAppUserService.findAllUsers().stream()
                .filter(user -> "recipient".equalsIgnoreCase(user.getUserType()) && user.getRecipientId() == null)
                .collect(Collectors.toList());

        for (MyAppUsers recipient : recipients) {
            try {
                RecipientProfile defaultProfile = new RecipientProfile();
                defaultProfile.setUser(recipient);
                defaultProfile = recipientProfileService.saveOrUpdateProfile(defaultProfile);
                recipient.setRecipientId(defaultProfile.getRecipientProfileId());
                myAppUserRepository.save(recipient);
            } catch (Exception e) {
                System.err.println("Error migrating user " + recipient.getUsername() + ": " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Migration completed: Updated " + recipients.size() + " recipient(s).");
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
            // Here we return an empty profile DTO.
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
            // Here we return an empty profile DTO, like before.
            dto = new DonorProfileDTO();
            dto.setUserId(userId);
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/donor/all")
    public ResponseEntity<List<DonorProfileDTO>> getAllDonors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String location){
        Pageable pageable = PageRequest.of(page, size, Sort.by("age").ascending());
        Page<DonorProfile> donorPage;
        if (location != null && !location.isEmpty()) {
            donorPage = donorProfileService.findByLocationContainingIgnoreCase(location, pageable);
        } else {
            donorPage = donorProfileService.findAll(pageable);
        }
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
            // Here we copy all matching properties, while ignoring the ID and user fields.
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
        // Here we convert to DTO
        DonorProfileDTO dto = DonorProfileConverter.convertToDTO(profileOpt.get());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/recipient/view/{recipientProfileId}")
    public ResponseEntity<?> viewRecipientProfile(@PathVariable Long recipientProfileId) {
        Optional<RecipientProfile> profileOpt = recipientProfileService.findByProfileId(recipientProfileId);
        if (!profileOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donor profile not found.");
        }
        // Here we convert to DTO
        RecipientProfileDTO dto = RecipientProfileConverter.convertToDTO(profileOpt.get());
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

    @GetMapping("/recipient/favorites")
    public ResponseEntity<List<DonorProfileDTO>> getFavoriteDonorsForRecipient() {
        MyAppUsers user = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || !"recipient".equalsIgnoreCase(user.getUserType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long recipientId = user.getRecipientId();
        List<Long> favoriteIds = myAppUserService.getFavoriteDonors(recipientId);
        List<DonorProfile> favoriteDonors = donorProfileService.getProfilesByIds(favoriteIds);
        List<DonorProfileDTO> dtoList = favoriteDonors.stream()
                .map(DonorProfileConverter::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/recipient/favoritedByDonor/{donorId}")
    public ResponseEntity<List<RecipientProfileDTO>> getRecipientsWhoFavoritedDonor(
            @PathVariable Long donorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        // Here we get the list of recipients who favored the donor
        List<MyAppUsers> recipients = myAppUserService.getRecipientsWhoFavoritedTheDonor(donorId);
        if (recipients == null) {
            recipients = new ArrayList<>();
        }

        int fromIndex = page * size;
        if (fromIndex >= recipients.size()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        int toIndex = Math.min(fromIndex + size, recipients.size());
        List<MyAppUsers> pageRecipients = recipients.subList(fromIndex, toIndex);
        List<RecipientProfileDTO> dtoList = pageRecipients.stream()
                .map(user -> {
                    RecipientProfile profile = user.getRecipientProfile();
                    return (profile != null)
                            ? RecipientProfileConverter.convertToDTO(profile)
                            : new RecipientProfileDTO();
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/recipient/favorite/{donorProfileId}")
    public ResponseEntity<?> addFavoriteDonor(@PathVariable Long donorProfileId) {
        MyAppUsers user = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || !"recipient".equalsIgnoreCase(user.getUserType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Please log in.");
        }
        Long recipientId = user.getRecipientId();
        if(recipientId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please complete your recipient profile before adding favorites.");
        }
        myAppUserService.addFavoriteDonor(recipientId, donorProfileId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Donor added to favorites"));
    }

    @PostMapping("/recipient/unfavorite/{donorProfileId}")
    public ResponseEntity<?> unfavoriteDonor(@PathVariable Long donorProfileId) {
        MyAppUsers user = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || !"recipient".equalsIgnoreCase(user.getUserType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long recipientId = user.getRecipientId();
        myAppUserService.removeFavoriteDonor(recipientId, donorProfileId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Donor removed from favorites"));
    }

    @GetMapping("/match/donor/matches")
    public ResponseEntity<List<RecipientProfileDTO>> getDonorMatches() {
        // Here we get the user from the database using their ID.
        MyAppUsers sessionUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MyAppUsers donor = myAppUserService.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        if (donor == null || !"donor".equalsIgnoreCase(donor.getUserType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Here we get the IDs of matched recipients
        List<Long> matchedRecipientIds = donor.getMatchRecipients();
        System.out.println("Donor " + donor.getId() + " matched recipients: " + matchedRecipientIds);

        // Here we retrieve the corresponding recipient profiles for the donors match page.
        List<RecipientProfile> matchedRecipients = recipientProfileService.getProfilesByUserIds(matchedRecipientIds);

        // Here we convert profiles to DTOs
        List<RecipientProfileDTO> dtoList = matchedRecipients.stream()
                .map(RecipientProfileConverter::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/match/recipient/matches")
    public ResponseEntity<List<DonorProfileDTO>> getRecipientMatches() {
        MyAppUsers sessionUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (sessionUser == null || !"recipient".equalsIgnoreCase(sessionUser.getUserType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Here we get the list of matched donor IDs from the recipient's record.
        List<Long> matchedDonorIds = sessionUser.getMatchDonorsList();
        System.out.println("Recipient " + sessionUser.getId() + " matched donors: " + matchedDonorIds);

        // Here we retrieve the corresponding donor profiles.
        List<DonorProfile> matchedDonors = donorProfileService.getProfilesByIds(matchedDonorIds);

        // Here we convert donor profiles to DTOs.
        List<DonorProfileDTO> dtoList = matchedDonors.stream()
                .map(DonorProfileConverter::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/match/approveMatch")
    public ResponseEntity<?> approveMatch(@RequestParam("donorId") Long donorId,
                                          @RequestParam("recipientId") Long recipientId) {
        try {
            myAppUserService.approveFavoriteAsMatch(donorId, recipientId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Match approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/match/unmatch")
    public ResponseEntity<?> unmatch(@RequestParam("donorId") Long donorId,
                                     @RequestParam("recipientId") Long recipientId) {
        try {
            // Here we remove match from both donor and recipient match lists
            myAppUserService.removeMatch(donorId, recipientId);

            // Here we get the logged-in user to determine if it's a donor
            MyAppUsers loggedInUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (loggedInUser != null && "donor".equalsIgnoreCase(loggedInUser.getUserType())) {
                // Here we also remove the donor's profile ID from the recipient's favorites list.
                myAppUserService.removeFavoriteDonor(recipientId, loggedInUser.getDonorId());
            }
            return ResponseEntity.ok(Collections.singletonMap("message", "Unmatched successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/messages/conversation/{receiverId}")
    public ResponseEntity<List<MessageDTO>> getConversationWith(@PathVariable Long receiverId) {
        MyAppUsers sender = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (sender == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Message> messages = messageService.getConversationBetween(sender.getId(), receiverId);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(MessageConverter::convertToDTO)
                .toList();

        return ResponseEntity.ok(messageDTOs);
    }


    @PostMapping("/messages/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageForm messageForm) {
        MyAppUsers sender = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (sender == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Long senderId = sender.getId();
        Long receiverId = messageForm.getReceiverId();
        String content = messageForm.getText();

        if (receiverId == null || content == null || content.isBlank()) {
            return ResponseEntity.badRequest().body("Invalid message data");
        }

        Message newMessage = new Message(senderId, receiverId, content, LocalDateTime.now());
        messageService.saveMessage(newMessage);

        return ResponseEntity.ok("Message sent successfully");
    }

    //Booking appointments
    //Url: POST /api/apointments/book
    @PostMapping("/bookings/book")
    public ResponseEntity<String> bookAppointment(@RequestBody BookingDTO request) {
        //Log the request details
        System.out.println("Incoming booking request: " + request);
        // Retrieve the authenticated user from Spring Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("ERROR: No authenticated user in SecurityContext");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }
        //Extract authenticated user
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof MyAppUsers sessionUser)) {
            System.out.println("ERROR: Principal is not a valid user.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid session.");
        }

        //Ensure only recipients can book
        if (!"recipient".equalsIgnoreCase(sessionUser.getUserType())) {
            System.out.println("ERROR: User is not a recipient.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only recipients can book an appointment.");
        }
        //Override request recipient ID with the authenticated user's ID
        request.setRecipientId(sessionUser.getId());
        //Log recipient details
        System.out.println("Booking for recipient ID: " + request.getRecipientId() + " with donor ID: " + request.getDonorId());
        try {
        //Create the booking
        Booking booking = bookingService.createBooking(
                request.getDonorId(),
                request.getRecipientId(), // Now using session user ID
                request.getDate(),
                request.getTime()
        );

        System.out.println("SUCCESS: Appointment booked with ID: " + booking.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body("Appointment booked successfully with ID: " + booking.getId());
        } catch (Exception e) {
            System.out.println("ERROR: Booking failed - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to book appointment: " + e.getMessage());
        }

    }



    //Get Recipient's Current Appointments
    //Allows a recipient to view their booked appointments
    //URL:GET /api/appointments/recipient
    @GetMapping("/recipient/{recipientId}")
    @ResponseBody
    public ResponseEntity<?> getRecipientAppointments(@PathVariable Long recipientId) {
        // Ensure the user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }

        MyAppUsers sessionUser = (MyAppUsers) authentication.getPrincipal();
        if (sessionUser == null || !sessionUser.getId().equals(recipientId) || !"recipient".equalsIgnoreCase(sessionUser.getUserType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

            List<BookingDTO> bookingDTOs = bookingService.getBookingsByRecipientId(recipientId)
                    .stream().map(booking ->
            new BookingDTO(
                booking.getId(),
                booking.getDonorId(),
                booking.getRecipientId(),
                booking.getDate(),
                booking.getTime(),
                booking.isConfirmed(),
                booking.getStatus()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(bookingDTOs);

    }



    //Donor Confirms or Cancels an Appointment
    @PostMapping("/confirm/{appointmentId}")
    @ResponseBody
    public ResponseEntity<?> confirmAppointment(@PathVariable Long appointmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }
        MyAppUsers sessionUser = (MyAppUsers) authentication.getPrincipal();
        if (!"donor".equalsIgnoreCase(sessionUser.getUserType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only donors can confirm appointments.");
        }

        try {
            bookingService.confirmBooking(appointmentId);
            return ResponseEntity.ok("Appointment confirmed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to confirm appointment:" + e.getMessage());
        }

    }

    //If donor cancels an appointment
    //URl to confirm: POST /api/appointments/confirm
    //URL to cancel: POST /api/appointments/cancel
    @PostMapping("/cancel/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }

        try {
            bookingService.cancelBooking(appointmentId);
            return ResponseEntity.ok("Appointment canceled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel appointment: " + e.getMessage());
        }
    }


    //API to see appointments waiting for confirmation
    //URL: GET /api/bookings/donor/{donorId}/pending
    @GetMapping("/bookings/donor/{donorId}/pending")
    @ResponseBody
    public ResponseEntity<?> getPendingAppointments(@PathVariable Long donorId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }
        MyAppUsers sessionUser = (MyAppUsers) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (sessionUser == null || !sessionUser.getId().equals(donorId) || !"donor".equalsIgnoreCase(sessionUser.getUserType())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        List<BookingDTO> bookingDTOs = bookingService.getPendingBookingsForDonor(donorId).stream().map(booking ->
                new BookingDTO(
                booking.getId(),
                booking.getDonorId(),
                booking.getRecipientId(),
                booking.getDate(),
                booking.getTime(),
                booking.isConfirmed(),
                booking.getStatus()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(bookingDTOs);

    }


}
