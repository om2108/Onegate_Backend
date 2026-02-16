package com.project.society.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String FROM_EMAIL = "yourgmail@gmail.com";

    public void sendOtpCode(String toEmail, String code) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(FROM_EMAIL);
            message.setTo(toEmail);
            message.setSubject("OneGate — Verification Code");
            message.setText(
                    "Hello,\n\n" +
                            "Your OneGate OTP code is: " + code + "\n\n" +
                            "This code expires in 10 minutes.\n\n" +
                            "If you didn’t request this, please ignore."
            );

            mailSender.send(message);

            System.out.println("✅ OTP Email sent via Gmail SMTP");

        } catch (Exception ex) {
            System.out.println("❌ Gmail Email Error: " + ex.getMessage());
        }
    }

    public void sendInviteLink(String email) {

        String inviteLink =
                "https://onegate.onrender.com/onboarding?email=" + email;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject("OneGate Invitation");
        message.setText(
                "You are invited to join OneGate.\n\n" +
                        "Complete setup:\n" + inviteLink
        );

        mailSender.send(message);
    }

    public void sendResetCode(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject("OneGate — Reset Password");
        message.setText(
                "Your password reset code is: " + code
        );

        mailSender.send(message);
    }
}
