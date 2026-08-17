package com.chat.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("ajinkyakakde510@gmail.com");
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "NovaChat - Your OTP Code";
        String body = "Your OTP code is: " + otp + "\n\nThis code will expire in 5 minutes.\n\nIf you didn't request this, please ignore this email.";
        sendEmail(to, subject, body);
    }

    public void sendWelcomeEmail(String to, String name) {
        String subject = "Welcome to NovaChat! 🎉";
        String body = "Hi " + name + ",\n\nWelcome to NovaChat! Your account has been created successfully.\n\nStart chatting with your friends now!\n\nHappy chatting!";
        sendEmail(to, subject, body);
    }
}
