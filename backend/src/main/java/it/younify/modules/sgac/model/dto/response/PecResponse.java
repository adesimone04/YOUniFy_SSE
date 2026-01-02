package it.younify.modules.sgac.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * The {@code PecResponse} class is a Data Transfer Object (DTO) used for deserializing
 * the JSON response from an external service that provides PEC (Posta Elettronica Certificata) information.
 * <p>
 * This class and its nested classes map to the structure of the JSON response,
 * allowing for easy extraction of PEC data.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PecResponse {

    /**
     * A list of data items, each containing information about a PEC address.
     */
    @JsonProperty("data")
    private List<DataItem> data;

    /**
     * Represents a single data item in the PEC response, containing the PEC address and its history.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataItem {
        /**
         * The PEC address.
         */
        @JsonProperty("pec")
        private String pec;

        /**
         * A list of history items for the PEC address.
         */
        @JsonProperty("history")
        private List<HistoryItem> history;
    }

    /**
     * Represents a historical record for a PEC address, including the address and a timestamp.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HistoryItem {
        /**
         * The PEC address at a point in time.
         */
        @JsonProperty("pec")
        private String pec;

        /**
         * The timestamp of the historical record.
         */
        @JsonProperty("timestamp")
        private long timestamp;
    }
}
