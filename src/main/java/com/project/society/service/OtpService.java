package com.project.society.service;

import com.project.society.model.OtpToken;
import com.project.society.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository repo;
    private final EmailService emailService;

    private final SecureRandom random = new SecureRandom();

    public String generateOtp(String email) {

        String code = String.format("%06d", random.nextInt(1_000_000));

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setCode(code);
        token.setExpiresAt(
                Instant.now().plus(10, ChronoUnit.MINUTES)
        );

        repo.save(token);

        System.out.println("📨 Sending OTP to: " + email);

        emailService.sendOtpCode(email, code);

        return code;
    }

    public boolean verifyOtp(String email, String code) {

        Optional<OtpToken> opt =
                repo.findTopByEmailOrderByCreatedAtDesc(email);

        if (opt.isEmpty()) return false;

        OtpToken token = opt.get();

        if (token.getExpiresAt() == null ||
                Instant.now().isAfter(token.getExpiresAt()))
            return false;

        return token.getCode().equals(code);
    }

    public void deleteOtpForEmail(String email) {
        repo.deleteByEmail(email);
    }
}
