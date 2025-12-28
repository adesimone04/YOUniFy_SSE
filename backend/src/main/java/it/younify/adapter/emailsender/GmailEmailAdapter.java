package it.younify.adapter.emailsender;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GmailEmailAdapter implements EmailProvider {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendWelcomeEmail(String email, String text) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Benvenuto in YOUniFy!");
            message.setText(text);

            mailSender.send(message);
        }
        catch (Exception e) {

            System.err.println("Error during sending email: " + e.getMessage());
        }
    }


    @Override
    @Async
    public void send2FAEmail(String email, String text) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Code 2FA Request");
            message.setText(text);

            mailSender.send(message);
        }
        catch (Exception e) {

            System.err.println("Error during sending email: " + e.getMessage());
        }
    }
}
