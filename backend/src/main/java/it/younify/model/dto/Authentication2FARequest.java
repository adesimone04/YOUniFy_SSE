package it.younify.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authentication2FARequest {

    @NotBlank
    private String ID;
    @NotBlank
    private String codeInserted;
}
