package com.chat.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("ajinkyakakde510@gmail.com");
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendOtpEmail(String to, String otp) {
        String subject = "NovaChat - Your OTP Code";
        String body = "Your OTP code is: " + otp + "\n\n" +
                     "This code will expire in 5 minutes.\n\n" +
                     "If you didn't request this, please ignore this email.";
        return sendEmail(to, subject, body);
    }

    public boolean sendWelcomeEmail(String to, String name) {
        String subject = "Welcome to NovaChat!";
        String body = "Hi " + name + ",\n\n" +
                     "Welcome to NovaChat! Your account has been created successfully.\n\n" +
                     "Start chatting with your friends now!\n\n" +
                     "Happy chatting!";
        return sendEmail(to, subject, body);
    }
}
