package com.chat.app.controller;

import com.chat.app.model.User;
import com.chat.app.service.OtpService;
import com.chat.app.service.UserService;
import com.chat.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
                RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserController {

    private final UserService userService;
    private final OtpService otpService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            String fullName = request.get("fullName");
            String email = request.get("email");
            String phoneNumber = request.get("phoneNumber");
            String password = request.get("password");

            User user = userService.registerUser(fullName, email, phoneNumber, password);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String password = request.get("password");

            User user = userService.login(phoneNumber, password);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== OTP Endpoints =====
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
        }
        try {
            String otp = otpService.generateOtp(phoneNumber);
            return ResponseEntity.ok(Map.of(
                    "message", "OTP sent successfully",
                    "otp", otp  // TEMPORARY: for testing only
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to send OTP"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");
        if (phoneNumber == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number and OTP are required"));
        }
        try {
            boolean valid = otpService.verifyOtp(phoneNumber, otp);
            if (!valid) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
            }

            User user = userService.findOrCreateUser(phoneNumber);
            user.setStatus(User.UserStatus.ONLINE);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            return userService.getUserById(userId)
                    .map(user -> {
                        user.setPassword(null);
                        return ResponseEntity.ok(user);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId,
                                        @RequestBody Map<String, String> updates) {
        try {
            User updatedUser = new User();

            // Name – accept both "fullName" and "username"
            if (updates.containsKey("fullName")) {
                updatedUser.setFullName(updates.get("fullName"));
            } else if (updates.containsKey("username")) {
                updatedUser.setFullName(updates.get("username"));
            }

            if (updates.containsKey("email")) {
                updatedUser.setEmail(updates.get("email"));
            }
            if (updates.containsKey("phoneNumber")) {
                updatedUser.setPhoneNumber(updates.get("phoneNumber"));
            }
            if (updates.containsKey("about")) {
                updatedUser.setAbout(updates.get("about"));
            }
            if (updates.containsKey("avatar")) {
                updatedUser.setAvatar(updates.get("avatar"));
            }

            // ✅ Automatically generate an initial‑based avatar from the new name
            String nameForAvatar = updatedUser.getFullName();
            if (nameForAvatar != null && !nameForAvatar.isBlank()) {
                String encodedName = nameForAvatar.replaceAll(" ", "+");
                String avatarUrl = "https://ui-avatars.com/api/?name=" + encodedName
                        + "&background=8B5CF6&color=fff&size=200";
                updatedUser.setAvatar(avatarUrl);
            }

            User user = userService.updateUser(userId, updatedUser);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<?> uploadAvatar(@PathVariable String userId,
                                          @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = "https://ui-avatars.com/api/?name=User&background=8B5CF6&color=fff&size=200";
            User updatedUser = new User();
            updatedUser.setAvatar(avatarUrl);

            User user = userService.updateUser(userId, updatedUser);
            user.setPassword(null);

            Map<String, String> response = new HashMap<>();
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<?> logoutUser(@PathVariable String userId) {
        try {
            userService.logout(userId);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }
}