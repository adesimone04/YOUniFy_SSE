package it.younify.model.dto.request;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class HostSignUpRequest {
    @NotBlank
    private String hostName;
    @NotBlank
    private String PIVA;
    @NotBlank
    private String PEC;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    private String province;
    @NotBlank
    private String city;
    @NotBlank
    private String uniList;
}
