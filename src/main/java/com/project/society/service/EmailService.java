package com.project.society.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String FROM_EMAIL = "pawarnandkumaromved@gmail.com";

    // ✅ Generic SendGrid sender
    public void sendEmail(String to, String subject, Content content) {

        try {
            Email from = new Email(FROM_EMAIL, "OneGate");
            Email receiver = new Email(to);

            Mail mail = new Mail(from, subject, receiver, content);

            SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("✅ Email sent → Status: " + response.getStatusCode());

        } catch (Exception ex) {
            System.out.println("❌ Email error: " + ex.getMessage());
        }
    }

    // ✅ Reusable HTML Template Builder
    private Content buildHtmlContent(String title, String message, String highlight) {

        String html =
                "<div style='font-family: Arial, sans-serif; padding:20px; background:#f4f6f8;'>"
                        + "  <div style='max-width:420px; margin:auto; background:white; padding:25px; border-radius:12px; box-shadow:0 5px 15px rgba(0,0,0,0.08);'>"
                        + "      <h2 style='color:#2E86C1; text-align:center; margin-bottom:20px;'>" + title + "</h2>"
                        + "      <p style='font-size:15px; color:#333;'>" + message + "</p>"
                        + (highlight != null
                        ? "  <h1 style='text-align:center; letter-spacing:4px; color:#222; margin:20px 0;'>" + highlight + "</h1>"
                        : "")
                        + "      <hr style='margin:20px 0;'>"
                        + "      <small style='color:gray;'>If you didn’t request this, you can safely ignore this email.</small>"
                        + "  </div>"
                        + "</div>";

        return new Content("text/html", html);
    }

    // =========================
    // ✅ OTP EMAIL
    // =========================
    public void sendOtpCode(String email, String code) {

        Content content = buildHtmlContent(
                "OneGate Verification 🔐",
                "Use the verification code below to continue:",
                code
        );

        sendEmail(email, "Complete your OneGate verification", content);
    }

    // =========================
    // ✅ INVITE EMAIL
    // =========================
    public void sendInviteLink(String email) {

        String inviteLink = "https://onegate.onrender.com/onboarding?email=" + email;

        Content content = buildHtmlContent(
                "You're Invited to OneGate 🎉",
                "Click the link below to complete your account setup:",
                "<a href='" + inviteLink + "' style='color:#2E86C1;'>" + inviteLink + "</a>"
        );

        sendEmail(email, "OneGate Invitation", content);
    }

    // =========================
    // ✅ RESET PASSWORD EMAIL
    // =========================
    public void sendResetCode(String email, String code) {

        Content content = buildHtmlContent(
                "Reset Your Password 🔑",
                "Use the code below to reset your password:",
                code
        );

        sendEmail(email, "OneGate Password Reset", content);
    }
}
