package it.younify.modules.sgac.service.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The {@code CdlValidationService} is responsible for validating the names of courses of study (Corsi di Laurea).
 * It ensures that a given course name exists in a preloaded list of valid courses.
 * This service loads data from a JSON file at startup to perform efficient, in-memory validation.
 */
@Service
@RequiredArgsConstructor
public class CdlValidationService {

    private final Set<String> validCdlSet = new HashSet<>();

    /**
     * Initializes the service by loading a list of courses from the {@code /data/cdl.json} resource file.
     * This method runs automatically after the service is constructed, populating a set of valid course names.
     * The data is normalized to uppercase for case-insensitive validation.
     *
     * @throws RuntimeException if the {@code cdl.json} file cannot be found, read, or parsed, as this is a critical failure for the service's operation.
     */
    @PostConstruct
    public void init() {

        try {

            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/data/cdl.json");

            Map<String, List<String>> rawData = mapper.readValue(is, new TypeReference<>() {});

            for (List<String> courseList : rawData.values()) {

                for (String cdl : courseList) {

                    validCdlSet.add(cdl.trim().toUpperCase());
                }
            }
        }
        catch(Exception e) {

            throw new RuntimeException("Error cdl.json not found or parsing failed", e);
        }
    }

    /**
     * Validates if a given course name is present in the list of known courses.
     * The validation is case-insensitive.
     *
     * @param cdlName The name of the course to validate.
     * @return {@code true} if the course is valid, {@code false} otherwise.
     */
    public boolean isValidCdl(String cdlName) {

        return validCdlSet.contains(cdlName.trim().toUpperCase());
    }
}