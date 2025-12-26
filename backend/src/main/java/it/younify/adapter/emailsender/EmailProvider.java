package it.younify.adapter.emailsender;

public interface EmailProvider {

    void sendWelcomeEmail(String email, String text);
    void send2FAEmail(String email, String text);
}
