package it.younify.modules.sgac.model.dto.request;

import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;


@Data
public class UserSignUpRequest {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    private String cdl;
    @NotBlank
    private String university;
    private LocalDate dateOfBirth;
    @NotBlank
    private String contactsInfo;
}
