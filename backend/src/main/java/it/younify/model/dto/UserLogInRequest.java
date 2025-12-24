package it.younify.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLogInRequest {

    @NotBlank
    String email;
    @NotBlank
    String password;
}
