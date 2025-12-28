package it.younify.adapter.PIVAlidator;

import it.younify.model.dto.PecResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ITPECAdapter implements PIVAlidator {

    @Value("${integration.openapi.base-url}")
    private String baseUrl;

    @Value("${integration.openapi.token}")
    private String apiToken;

    private final RestClient.Builder restClientBuilder;

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
                            (request, response) -> new RuntimeException("Partita IVA non trovata nel registro")
                    )
                    .body(PecResponse.class)).getData().getFirst().getPec();

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RuntimeException("Errore configurazione API: Token non valido");
        } catch (Exception e) {

            System.err.println("Errore chiamata OpenAPI: " + e.getMessage());
            throw new RuntimeException("Impossibile verificare la Partita IVA al momento");

        }
    }
}