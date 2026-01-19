package it.younify.modules.sgu.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HostProfileViewResponse {

    /**
     * The name of the host.
     * This field is mandatory.
     */
    private String hostName;

    /**
     * The host's VAT number (Partita IVA).
     * This field is mandatory.
     */
    private String PIVA;

    /**
     * The host's certified email address (PEC).
     * This field is mandatory.
     */
    private String PEC;

    /**
     * The province where the host is located.
     * This field is mandatory.
     */
    private String province;

    /**
     * The city where the host is located.
     * This field is mandatory.
     */
    private String city;

    /**
     * A list of universities nearest the host.
     */
    private String uniList;
}
