package it.younify.modules.sgac.model.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

/**
 * The {@code HostSignUpRequest} class is a Data Transfer Object (DTO) used for
 * capturing host information during the sign-up process.
 * <p>
 * This DTO contains all the necessary fields for creating a new host account.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostSignUpRequest {

    /**
     * The name of the host.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 30)
    private String hostName;

    /**
     * Reserved for university association.
     * This field is mandatory.
     */
    private boolean association;

    /**
     * The host's VAT number (Partita IVA).
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 11, max = 16)
    @JsonProperty("PIVA")
    private String PIVA;

    /**
     * The host's certified email address (PEC).
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 40)
    @JsonProperty("PEC")
    private String PEC;

    /**
     * The host's chosen password.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    /**
     * The confirmation of the host's password.
     * This should match the {@code password} field.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String confirmPassword;

    /**
     * The province where the host is located.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 30)
    private String province;

    /**
     * The city where the host is located.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 30)
    private String city;

    /**
     * A list of universities nearest the host.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 255)
    private String uniList;
}
