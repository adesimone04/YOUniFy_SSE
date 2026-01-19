package it.younify.modules.sgac.service.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * The {@code UniversityValidationService} is responsible for validating university names.
 * It ensures that a given university name exists in a pre-loaded list of valid Italian universities.
 * This service loads data from a JSON file at startup to perform efficient, in-memory validation.
 */
@Service
@RequiredArgsConstructor
public class UniversityValidationService {

    private final Set<String> allUniversitiesSet = new HashSet<>();

    /**
     * Initializes the service by loading a list of Italian universities from the {@code /data/universities.json} resource file.
     * This method runs automatically after the service is constructed, populating a set of valid university names.
     * The data is normalized to uppercase for case-insensitive validation.
     *
     * @throws RuntimeException if the {@code universities.json} file cannot be found, read, or parsed, as this is a critical failure for the service's operation.
     */
    @PostConstruct
    public void init() {

        try {

            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/data/universities.json");

            Map<String, List<String>> rawData = mapper.readValue(is, new TypeReference<Map<String, List<String>>>() {});

            for (List<String> cityUniversities : rawData.values()) {

                for (String uni : cityUniversities) {

                    allUniversitiesSet.add(uni.trim().toUpperCase());
                }
            }
        }
        catch(Exception e) {

            throw new RuntimeException("Error universities.json not found or parsing failed", e);
        }
    }

    /**
     * Validates if a given university name is present in the list of known universities.
     * The validation is case-insensitive.
     *
     * @param university The name of the university to validate.
     * @return {@code true} if the university is valid, {@code false} otherwise.
     */
    public boolean isValidUniversity(String university) {

        return allUniversitiesSet.contains(university.trim().toUpperCase());
    }

    /**
     * Validates if all university names in a given list are present in the list of known universities.
     *
     * @param universityNames A list of university names to validate.
     * @return {@code true} if all universities in the list are valid, {@code false} otherwise.
     */
    public boolean isValidUniversity(List<String> universityNames) {

        return universityNames.stream().allMatch(this::isValidUniversity);
    }
}
