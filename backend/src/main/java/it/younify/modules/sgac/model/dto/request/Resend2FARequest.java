package it.younify.modules.sgac.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Resend2FARequest {

    @NotBlank
    private String ID;
}
