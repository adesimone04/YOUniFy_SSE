package it.younify.modules.sgu.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EditUserInfoRequest {

    /**
     * The user's first name.
     * This field is mandatory.
     */
    @Size(max = 30)
    private String firstName;

    /**
     * The user's last name.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 30)
    private String lastName;

    /**
     * The user's date of birth.
     */
    @NotNull
    private LocalDate dateOfBirth;

    /**
     * The user's contact information.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 50)
    private String contactsInfo;


}
