package com.project.society.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String FROM_EMAIL = "pawarnandkumaromved@gmail.com";

    public void sendEmail(String to, String subject, String body) {

        try {
            Email from = new Email(FROM_EMAIL);
            Email receiver = new Email(to);

            Content content = new Content("text/plain", body);
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

    public void sendOtpCode(String email, String code) {
        sendEmail(
                email,
                "OneGate — Verification Code",
                "Your OTP code is: " + code + "\n\nExpires in 10 minutes."
        );
    }

    public void sendInviteLink(String email) {

        String inviteLink =
                "https://onegate.onrender.com/onboarding?email=" + email;

        sendEmail(
                email,
                "OneGate Invitation",
                "You are invited to join OneGate.\n\nComplete setup:\n" + inviteLink
        );
    }

    public void sendResetCode(String email, String code) {
        sendEmail(
                email,
                "OneGate — Reset Password Code",
                "Your password reset code is: " + code
        );
    }
}
