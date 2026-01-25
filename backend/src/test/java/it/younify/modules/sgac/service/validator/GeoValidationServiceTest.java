package it.younify.modules.sgac.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoValidationServiceTest {

    private GeoValidationService geoValidationService;

    @BeforeEach
    void setUp() {

        geoValidationService = new GeoValidationService();

        // 2. Dati di test simulati
        Map<String, String> mockCities = new HashMap<>();
        mockCities.put("FISCIANO", "SALERNO");
        mockCities.put("ROMA", "ROMA");

        ReflectionTestUtils.setField(geoValidationService, "validCitiesMap", mockCities);
    }

    // TC6_SGE: Luogo non presente
    @Test
    void validateGeo_TC6_LocationNotItalian() {
        String invalidCity = "Radiator Springs";
        String invalidProvince = "Route 66";

        boolean result = geoValidationService.validateGeo(invalidProvince, invalidCity);

        assertFalse(result, "Deve restituire false se la città non è nella mappa");
    }

}