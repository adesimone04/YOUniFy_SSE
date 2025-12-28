package it.younify.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PecResponse {

    @JsonProperty("data")
    private List<DataItem> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataItem {
        @JsonProperty("pec")
        private String pec;

        @JsonProperty("history")
        private List<HistoryItem> history;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HistoryItem {
        @JsonProperty("pec")
        private String pec;

        @JsonProperty("timestamp")
        private long timestamp;
    }
}