package it.younify.modules.sgu.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePassRequest {

    /**
     * The user's chosen password.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    /**
     * The confirmation of the user's password.
     * This should match the {@code password} field.
     * This field is mandatory.
     */
    @NotBlank
    @Size(min = 8, max = 100)
    private String confirmPassword;

}
