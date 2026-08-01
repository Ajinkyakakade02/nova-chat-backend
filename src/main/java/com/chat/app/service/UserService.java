package com.chat.app.service;

import com.chat.app.model.User;
import com.chat.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User registerUser(String fullName, String email, String phoneNumber, String password) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RuntimeException("Phone number already registered");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setFullName(fullName);
        user.setUsername(fullName != null ? fullName.toLowerCase().replace(" ", "_") : "user");
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPhone(phoneNumber);
        user.setPassword(password); // <-- password provided here
        user.setAbout("Hey there! I am using Chat App");
        user.setStatus(User.UserStatus.OFFLINE);
        user.setLastSeen(LocalDateTime.now());
        user.setAvatar("https://ui-avatars.com/api/?name=" + fullName + "&background=8B5CF6&color=fff&size=200");

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(String userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedUser.getFullName() != null) {
            existingUser.setFullName(updatedUser.getFullName());
            existingUser.setUsername(updatedUser.getFullName().toLowerCase().replace(" ", "_"));
        }
        if (updatedUser.getEmail() != null) {
            Optional<User> emailUser = userRepository.findByEmail(updatedUser.getEmail());
            if (emailUser.isPresent() && !emailUser.get().getId().equals(userId)) {
                throw new RuntimeException("Email already in use");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhoneNumber() != null) {
            Optional<User> phoneUser = userRepository.findByPhoneNumber(updatedUser.getPhoneNumber());
            if (phoneUser.isPresent() && !phoneUser.get().getId().equals(userId)) {
                throw new RuntimeException("Phone number already in use");
            }
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setPhone(updatedUser.getPhoneNumber());
        }
        if (updatedUser.getAbout() != null) {
            existingUser.setAbout(updatedUser.getAbout());
        }
        if (updatedUser.getAvatar() != null) {
            existingUser.setAvatar(updatedUser.getAvatar());
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public User login(String phoneNumber, String password) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        user.setStatus(User.UserStatus.ONLINE);
        user.setLastSeen(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void logout(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.UserStatus.OFFLINE);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    /**
     * Finds user by phone number or creates a new one (for OTP login).
     * Sets a dummy password because the password column is NOT NULL.
     */
    @Transactional
    public User findOrCreateUser(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        // Auto-register for new OTP users
        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        newUser.setPhoneNumber(phoneNumber);
        newUser.setPhone(phoneNumber);
        newUser.setUsername("user_" + phoneNumber.substring(Math.max(0, phoneNumber.length() - 4)));
        newUser.setFullName("User " + phoneNumber.substring(Math.max(0, phoneNumber.length() - 4)));

        // Set a dummy password to satisfy NOT NULL constraint (OTP users never use it)
        newUser.setPassword(UUID.randomUUID().toString());

        newUser.setStatus(User.UserStatus.OFFLINE);
        newUser.setLastSeen(LocalDateTime.now());
        newUser.setAvatar("https://ui-avatars.com/api/?name=" + newUser.getFullName() + "&background=8B5CF6&color=fff&size=200");
        return userRepository.save(newUser);
    }
}