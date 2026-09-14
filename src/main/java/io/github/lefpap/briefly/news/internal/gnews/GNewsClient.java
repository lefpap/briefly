package io.github.lefpap.briefly.news.internal.gnews;

import io.github.lefpap.briefly.observability.HttpCallTimingInterceptor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
@Validated
public class GNewsClient {

    private static final String GNEWS_API_KEY_HEADER = "X-Api-Key";

    private final RestClient restClient;

    public GNewsClient(RestClient.Builder restClientBuilder, GNewsClientProperties properties) {
        this.restClient = restClientBuilder
            .baseUrl(properties.baseUrl())
            .defaultHeader(GNEWS_API_KEY_HEADER, properties.apiKey())
            .defaultRequest(HttpCallTimingInterceptor.named("GNews"))
            .build();
    }

    public GNewsResponse search(@NotNull @Valid GNewsSearchParams params) {
        try {
            GNewsResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/search")
                    .queryParams(params.toQueryParams())
                    .build()
                )
                .retrieve()
                .requiredBody(GNewsResponse.class);

            if (response.articles() == null) {
                throw new GNewsClientException("GNews response did not contain an Article collection");
            }

            return response;
        } catch (RestClientResponseException ex) {
            throw new GNewsClientException(
                "GNews returned an error response [%s (%d)]".formatted(
                    ex.getStatusCode(),
                    ex.getStatusCode().value()),
                ex
            );
        } catch (IllegalStateException ex) {
            throw new GNewsClientException("GNews returned a response without the required body", ex);
        }
    }
}
