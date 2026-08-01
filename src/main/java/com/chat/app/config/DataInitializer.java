package com.chat.app.config;

import com.chat.app.model.User;
import com.chat.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // Delete any old demo users by phone
        List<String> phonesToDelete = List.of("1111111111", "2222222222", "3333333333", "4444444444");
        for (String phone : phonesToDelete) {
            userRepository.findByPhoneNumber(phone).ifPresent(userRepository::delete);
        }

        // Create fresh Nova AI
        User nova = new User();
        nova.setId("local-1");
        nova.setUsername("nova_ai");
        nova.setFullName("Nova AI");
        nova.setEmail(null);
        nova.setPhoneNumber("1111111111");   // dummy phone, never shown
        nova.setPhone("1111111111");
        nova.setPassword("ai_no_password");  // dummy password – never used
        nova.setAbout("Your friendly chat companion ✨");
        nova.setAvatar("/favicon.ico");
        nova.setStatus(User.UserStatus.ONLINE);
        nova.setLastSeen(LocalDateTime.now());
        userRepository.save(nova);
    }
}