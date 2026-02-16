package com.project.society.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Slf4j
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

    // ✅ Generic method (reusable)
    private void sendEmail(String to, String subject, String html) {

        try {
            log.info("📨 Sending email to {}", to);

            Map<String, Object> body = Map.of(
                    // ✅ DOMAIN-FREE SAFE SENDER
                    "from", "OneGate <onboarding@resend.dev>",
                    "to", new String[]{to},
                    "subject", subject,
                    "html", html
            );

            String response = webClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("✅ Resend success → {}", response);

        } catch (WebClientResponseException ex) {
            log.error("❌ Resend API Error → Status: {}, Body: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString());

        } catch (Exception ex) {
            log.error("❌ Email sending failed → {}", ex.getMessage());
        }
    }

    // ✅ OTP Email
    public void sendOtpCode(String toEmail, String code) {

        String html =
                "<div style='font-family:Arial;padding:20px;'>"
                        + "<h2>OneGate Verification</h2>"
                        + "<p>Your OTP code:</p>"
                        + "<h1 style='letter-spacing:6px;color:#333;'>"
                        + code +
                        "</h1>"
                        + "<p>Expires in 10 minutes.</p>"
                        + "<br>"
                        + "<small>If you didn’t request this, ignore.</small>"
                        + "</div>";

        sendEmail(
                toEmail,
                "Your OneGate Verification Code",
                html
        );
    }

    // ✅ Invite Email
    public void sendInviteLink(String email) {

        String inviteLink =
                "https://onegate.onrender.com/onboarding?email=" + email;

        String html =
                "<h2>You're Invited 🎉</h2>"
                        + "<p>Complete setup:</p>"
                        + "<a href='" + inviteLink + "'>"
                        + inviteLink +
                        "</a>";

        sendEmail(
                email,
                "OneGate Invitation",
                html
        );
    }

    // ✅ Reset Email
    public void sendResetCode(String email, String code) {

        String html =
                "<h2>Password Reset</h2>"
                        + "<p>Your reset code:</p>"
                        + "<h1>" + code + "</h1>";

        sendEmail(
                email,
                "Reset Your OneGate Password",
                html
        );
    }
}
