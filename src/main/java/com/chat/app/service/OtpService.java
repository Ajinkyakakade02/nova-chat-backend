package com.chat.app.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OtpService {

    // In-memory OTP store (replace with Redis/DB in production)
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    // OTP valid for 5 minutes
    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000;

    /**
     * Generate a 6-digit OTP for the given phone number.
     * In development, the OTP is printed to the console.
     */
    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        otpStore.put(phoneNumber, new OtpEntry(otp, System.currentTimeMillis()));

        // For development: print OTP to console (remove in production)
        System.out.println("========================================");
        System.out.println("OTP for " + phoneNumber + ": " + otp);
        System.out.println("========================================");

        return otp;
    }

    /**
     * Verify the provided OTP against the stored one for the phone number.
     */
    public boolean verifyOtp(String phoneNumber, String otp) {
        OtpEntry entry = otpStore.get(phoneNumber);
        if (entry == null) {
            return false;
        }

        // Check expiry
        if (System.currentTimeMillis() - entry.createdAt > OTP_VALIDITY_MS) {
            otpStore.remove(phoneNumber);
            return false;
        }

        boolean isValid = entry.otp.equals(otp);
        if (isValid) {
            otpStore.remove(phoneNumber); // OTP used, remove it
        }
        return isValid;
    }

    // Internal class to hold OTP and its creation time
    private static class OtpEntry {
        String otp;
        long createdAt;

        OtpEntry(String otp, long createdAt) {
            this.otp = otp;
            this.createdAt = createdAt;
        }
    }
}