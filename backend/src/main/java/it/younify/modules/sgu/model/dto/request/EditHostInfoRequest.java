package it.younify.modules.sgu.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents the request body for editing a host's profile information.
 * This DTO is used to transfer updated host details from the client to the server.
 */
@Data
public class EditHostInfoRequest {

    /**
     * The name of the host.
     * This field is mandatory.
     */
    @NotBlank
    @Size(max = 30)
    private String hostName;

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
