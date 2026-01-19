package it.younify.modules.sgu.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Confirm2FAUniversity {

    /**
     * The unique identifier of the account attempting to authenticate.
     * This field is mandatory.
     */
    @NotBlank
    private String ID;

    /**
     * The 2FA code entered by the user.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 6, max = 6)
    private String codeInserted;

    /**
     * The user's email address.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 40)
    private String email;

    /**
     * The user's university.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 50)
    private String university;
}
