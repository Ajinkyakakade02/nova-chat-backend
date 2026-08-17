package com.chat.app.controller;

import com.chat.app.model.User;
import com.chat.app.service.OtpService;
import com.chat.app.service.UserService;
import com.chat.app.service.MailService;
import com.chat.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
                   RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserController {

    private final UserService userService;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final MailService mailService;

    // Register with Email
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            String fullName = request.get("fullName");
            String email = request.get("email");
            String password = request.get("password");

            // Check if email already exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already registered. Please login."));
            }

            User user = new User();
            user.setId(java.util.UUID.randomUUID().toString());
            user.setUsername(email.split("@")[0]);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);
            user.setAbout("Hey there! I am using NovaChat");
            user.setAvatar("https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+") + "&background=8B5CF6&color=fff&size=200");
            user.setStatus(User.UserStatus.ONLINE);
            user.setLastSeen(LocalDateTime.now());

            userRepository.save(user);
            user.setPassword(null);

            // Send welcome email
            try {
                mailService.sendWelcomeEmail(email, fullName);
            } catch (Exception mailEx) {
                System.out.println("Failed to send welcome email: " + mailEx.getMessage());
            }

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Login with Email
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email not found. Please register first."));
            }

            User user = userOpt.get();
            if (user.getPassword() == null || !user.getPassword().equals(password)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid password"));
            }

            user.setStatus(User.UserStatus.ONLINE);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
            user.setPassword(null);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Login failed"));
        }
    }

    // Forgot Password - Send OTP to email
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        
        try {
            Optional<User> userOpt = userRepository.findByEmail(email.trim());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email not found. Please register first."));
            }
            
            String otp = otpService.generateOtp(email);
            
            // Send OTP via email
            try {
                mailService.sendOtpEmail(email.trim(), otp);
            } catch (Exception mailEx) {
                System.out.println("Failed to send OTP email: " + mailEx.getMessage());
                return ResponseEntity.internalServerError().body(Map.of("error", "Failed to send email. Please try again."));
            }
            
            return ResponseEntity.ok(Map.of("message", "OTP sent to your email"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "An error occurred"));
        }
    }

    // Reset Password with OTP
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");
        
        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }
        
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
        }
        
        try {
            boolean valid = otpService.verifyOtp(email, otp);
            if (!valid) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
            }
            
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            User user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            e.printStackTrace();
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
            if (updates.containsKey("fullName")) {
                updatedUser.setFullName(updates.get("fullName"));
            }
            if (updates.containsKey("email")) {
                updatedUser.setEmail(updates.get("email"));
            }
            if (updates.containsKey("about")) {
                updatedUser.setAbout(updates.get("about"));
            }
            if (updates.containsKey("avatar")) {
                updatedUser.setAvatar(updates.get("avatar"));
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
