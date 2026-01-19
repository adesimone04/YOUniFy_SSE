package it.younify.modules.sgac.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * The {@code AccountSummary} class is a Data Transfer Object (DTO) that provides
 * a summary of a user or host account.
 * <p>
 * This DTO is typically returned after a successful authentication or account operation,
 * providing essential, non-sensitive information about the account.
 */
@Data
@Builder
public class AccountSummary {

    /**
     * The unique identifier of the account attempting to authenticate.
     */
    private String ID;

    /**
     * The name associated with the account (e.g., user's first name or host's name).
     */
    private String accountName;

    /**
     * The email address/PEC associated with the account.
     */
    private String accountEmail;

    /**
     * The expiry date of 2FA code.
     */
    private Instant expiryDate;

    /**
     * The user's university.
     */
    private String university;

    /**
     * The user's course of study (Corso di Laurea).
     */
    private String cdl;
}
