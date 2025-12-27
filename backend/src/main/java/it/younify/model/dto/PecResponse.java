package it.younify.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PecResponse {


    @JsonProperty("pec")
    private String pec;

    @JsonProperty("denomination")
    private String companyName;

    @JsonProperty("vat_code")
    private String vatCode;
}