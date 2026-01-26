package it.younify.modules.sgac.service.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code GeoValidationService} is responsible for validating Italian geographical locations.
 * It ensures that a given city belongs to a specific province by checking against a preloaded dataset.
 * This service loads data from a JSON file at startup to perform efficient, in-memory validation.
 */
@Service
@RequiredArgsConstructor
public class GeoValidationService {

    private final Map<String, Set<String>> validCitiesMap = new HashMap<>();

    /**
     * Initializes the service by loading a list of Italian municipalities from the {@code /data/comuni.json} resource file.
     * This method runs automatically after the service is constructed, populating a map of valid city-province pairs.
     * The data is normalized to uppercase for case-insensitive validation.
     *
     * @throws RuntimeException if the {@code comuni.json} file cannot be found, read, or parsed, as this is a critical failure for the service's operation.
     */
    @PostConstruct
    public void init() {

        try {

            ObjectMapper mapper = new ObjectMapper();

            InputStream is = getClass().getResourceAsStream("/data/comuni.json");
            JsonNode rootNode = mapper.readTree(is);

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {

                    if (node.has("nome") && node.has("provincia")) {

                        String comune = node.get("nome").asText().trim().toUpperCase();
                        String province = node.get("provincia").get("nome").asText().trim().toUpperCase();

                        validCitiesMap.computeIfAbsent(comune, k -> new HashSet<>()).add(province);
                    }
                }
            }
        }
        catch(Exception e){

            throw new RuntimeException("File comuni.json not found or parsing failed", e);
        }
    }

    /**
     * Validates if a given city belongs to the specified province.
     * The validation is case-insensitive and checks against the in-memory map of Italian municipalities.
     *
     * @param province The name of the province to validate.
     * @param city The name of the city to validate.
     * @return {@code true} if the city exists and is part of the specified province, {@code false} otherwise.
     */
    public boolean validateGeo(String province, String city) {

        String normalizedProv = province.trim().toUpperCase();
        String normalizedCity = city.trim().toUpperCase();

        return validCitiesMap.containsKey(normalizedCity) &&
            validCitiesMap.get(normalizedCity).contains(normalizedProv);

    }

    public boolean isValidCityOnly(String city) {

        return validCitiesMap.containsKey(city.trim().toUpperCase());
    }
}
