package it.younify.modules.sgac.adapter.emailsender;

/**
 * The {@code EmailProvider} interface defines a contract for sending emails.
 * This interface is part of an adapter pattern, allowing for different email
 * service implementations to be used interchangeably.
 * <p>
 * Implementations of this interface are responsible for the actual sending of emails,
 * whether through SMTP, a third-party API.
 */
public interface EmailProvider {

    /**
     * Sends a welcome email to a new user or host.
     * This email is typically sent upon successful registration and 2FA confirmation.
     *
     * @param email The recipient's email address.
     * @param text The body of the welcome message.
     */
    void sendWelcomeEmail(String email, String text);

    /**
     * Sends a two-factor authentication (2FA) code to a user or host.
     * This email is sent when a 2FA code is requested for login or other sensitive operations.
     *
     * @param email The recipient's email address.
     * @param text The body of the email, containing the 2FA code.
     */
    void send2FAEmail(String email, String text);
}
