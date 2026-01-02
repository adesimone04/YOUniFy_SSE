package it.younify.modules.sgac.adapter.PIVAlidator;

import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceNotFoundException;
import it.younify.modules.sgac.model.dto.response.PecResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

/**
 * The {@code ITPECAdapter} class is an implementation of the {@link PIVAlidator} interface.
 * It uses an external API to validate an Italian "Partita IVA" (VAT number) and retrieve
 * the associated certified email address (PEC).
 * <p>
 * This adapter uses Spring's {@link RestClient} to make the API call.
 */
@Service
@RequiredArgsConstructor
public class ITPECAdapter implements PIVAlidator {

    @Value("${integration.openapi.base-url}")
    private String baseUrl;

    @Value("${integration.openapi.token}")
    private String apiToken;

    private final RestClient.Builder restClientBuilder;

    /**
     * Validates a given "Partita IVA" by calling an external API and returns the corresponding PEC.
     *
     * @param vatCode The "Partita IVA" to validate.
     * @return The certified email address (PEC) associated with the given "Partita IVA".
     * @throws BadRequestException if the "Partita IVA" is not found in the registry.
     * @throws ResourceNotFoundException if the API token is invalid or there is a general error during the API call.
     */
    @Override
    public String PIVAlidatePEC(String vatCode) {

        RestClient client = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .build();

        try {
            return Objects.requireNonNull(client.get()
                    .uri("/IT-pec/{vatCode}", vatCode)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            (request, response) -> new BadRequestException("Partita IVA non trovata nel registro")
                    )
                    .body(PecResponse.class)).getData().getFirst().getPec();

        }
        catch (HttpClientErrorException.Unauthorized e) {

            throw new ResourceNotFoundException("Errore configurazione API: Token non valido");
        }
        catch (Exception e) {

            throw new ResourceNotFoundException("Impossibile verificare la Partita IVA al momento");
        }
    }
}