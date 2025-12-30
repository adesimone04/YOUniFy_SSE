package it.younify.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogInRequest {

    @NotBlank
    String email;
    @NotBlank
    String password;
}
