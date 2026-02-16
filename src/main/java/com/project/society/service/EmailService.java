package com.project.society.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailService {

    private final WebClient webClient;

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    public EmailService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
    }

    public void sendOtpCode(String toEmail, String code) {

        try {
            Map<String, Object> requestBody = Map.of(
                    "from", "OneGate <onboarding@resend.dev>",
                    "to", new String[]{toEmail},
                    "subject", "Your OneGate Verification Code",
                    "html",
                    "<div style='font-family: Arial; padding:20px;'>"
                            + "<h2>OneGate Verification</h2>"
                            + "<p>Your OTP code:</p>"
                            + "<h1 style='letter-spacing:5px;'>" + code + "</h1>"
                            + "<p>Expires in 10 minutes.</p>"
                            + "</div>"
            );

            webClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(res -> System.out.println("✅ OTP Email sent via Resend"))
                    .block();

        } catch (Exception ex) {
            System.out.println("❌ Resend Email Error: " + ex.getMessage());
        }
    }

    public void sendInviteLink(String email) {

        String inviteLink =
                "https://onegate.onrender.com/onboarding?email=" + email;

        Map<String, Object> requestBody = Map.of(
                "from", "OneGate <onboarding@resend.dev>",
                "to", new String[]{email},
                "subject", "OneGate Invitation",
                "html",
                "<h2>You're Invited 🎉</h2>"
                        + "<p>Complete setup:</p>"
                        + "<a href='" + inviteLink + "'>" + inviteLink + "</a>"
        );

        webClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void sendResetCode(String email, String code) {

        Map<String, Object> requestBody = Map.of(
                "from", "OneGate <onboarding@resend.dev>",
                "to", new String[]{email},
                "subject", "Reset Your Password",
                "html",
                "<h2>Password Reset</h2>"
                        + "<p>Your reset code:</p>"
                        + "<h1>" + code + "</h1>"
        );

        webClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
