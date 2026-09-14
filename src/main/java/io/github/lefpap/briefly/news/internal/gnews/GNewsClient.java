package io.github.lefpap.briefly.news.internal.gnews;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;

@Slf4j
@Service
@Validated
public class GNewsClient {

    private static final String GNEWS_API_KEY_HEADER = "X-Api-Key";

    private final RestClient restClient;

    public GNewsClient(RestClient.Builder restClientBuilder, GNewsClientProperties properties) {
        this.restClient = restClientBuilder
            .baseUrl(properties.baseUrl())
            .defaultHeader(GNEWS_API_KEY_HEADER, properties.apiKey())
            .requestInterceptor(GNewsClient::logExchange)
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

    private static ClientHttpResponse logExchange(
        HttpRequest request,
        byte[] body,
        ClientHttpRequestExecution execution
    ) throws IOException {
        long startedAt = System.nanoTime();
        try {
            ClientHttpResponse response = execution.execute(request, body);
            log.debug(
                "GNews request: {} {} -> {} ({} ms)",
                request.getMethod(),
                request.getURI(),
                response.getStatusCode(),
                elapsedMillis(startedAt)
            );
            return response;
        } catch (IOException | RuntimeException exception) {
            log.debug(
                "GNews request failed: {} {} ({} ms)",
                request.getMethod(),
                request.getURI(),
                elapsedMillis(startedAt),
                exception
            );
            throw exception;
        }
    }

    private static long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
