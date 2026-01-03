package it.younify.modules.sgac.adapter.emailsender;

import it.younify.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * The {@code GmailEmailAdapter} class is an implementation of the {@link EmailProvider} interface
 * that uses Spring's {@link JavaMailSender} to send emails via Gmail's SMTP server.
 * <p>
 * This adapter is responsible for constructing and sending welcome and 2FA emails.
 * The email sending operations are performed asynchronously.
 */
@Service
@RequiredArgsConstructor
public class GmailEmailAdapter implements EmailProvider {

    private final JavaMailSender mailSender;

    /**
     * Sends a welcome email asynchronously.
     *
     * @param email The recipient's email address.
     * @param text The body of the welcome message.
     * @throws ResourceNotFoundException if an error occurs during the email sending process.
     */
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

            throw new ResourceNotFoundException("Error during sending email: " + e.getMessage());
        }
    }


    /**
     * Sends a two-factor authentication (2FA) email asynchronously.
     *
     * @param email The recipient's email address.
     * @param text The body of the email, containing the 2FA code.
     * @throws ResourceNotFoundException if an error occurs during the email sending process.
     */
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

            throw new ResourceNotFoundException("Error during sending 2FA email: " + e.getMessage());
        }
    }
}
