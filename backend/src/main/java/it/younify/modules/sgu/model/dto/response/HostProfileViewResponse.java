package it.younify.modules.sgu.model.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * Represents the response body for viewing a host's profile.
 * This DTO is used to transfer a subset of a host's details from the server to the client for display.
 */
@Data
@Builder
public class HostProfileViewResponse {

    /**
     * The URL or reference to the user's avatar image.
     */
    private String avatar;

    /**
     * The name of the host.
     */
    private String hostName;

    /**
     * The host's VAT number (Partita IVA).
     */
    private String PIVA;

    /**
     * The host's certified email address (PEC).
     */
    private String PEC;

    /**
     * The province where the host is located.
     */
    private String province;

    /**
     * The city where the host is located.
     */
    private String city;

    /**
     * A comma-separated list of universities near the host.
     */
    private String uniList;

    /**
     * The total count of CFU (credits or reputation points) accumulated by the user.
     */
    private int totalCfu;
}
