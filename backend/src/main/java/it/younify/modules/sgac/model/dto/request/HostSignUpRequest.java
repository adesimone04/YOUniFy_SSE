package it.younify.modules.sgac.model.dto.request;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * The {@code HostSignUpRequest} class is a Data Transfer Object (DTO) used for
 * capturing host information during the sign-up process.
 * <p>
 * This DTO contains all the necessary fields for creating a new host account.
 */
@Data
public class HostSignUpRequest {

    /**
     * The name of the host.
     * This field is mandatory.
     */
    @NotBlank
    private String hostName;

    /**
     * The host's VAT number (Partita IVA).
     * This field is mandatory.
     */
    @NotBlank
    private String PIVA;

    /**
     * The host's certified email address (PEC).
     * This field is mandatory.
     */
    @NotBlank
    private String PEC;

    /**
     * The host's chosen password.
     * This field is mandatory.
     */
    @NotBlank
    private String password;

    /**
     * The confirmation of the host's password.
     * This should match the {@code password} field.
     * This field is mandatory.
     */
    @NotBlank
    private String confirmPassword;

    /**
     * The province where the host is located.
     * This field is mandatory.
     */
    @NotBlank
    private String province;

    /**
     * The city where the host is located.
     * This field is mandatory.
     */
    @NotBlank
    private String city;

    /**
     * A list of universities nearest the host.
     * This field is mandatory.
     */
    @NotBlank
    private String uniList;
}
