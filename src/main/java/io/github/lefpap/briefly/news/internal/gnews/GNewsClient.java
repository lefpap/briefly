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
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/search")
                .queryParams(params.toQueryParams())
                .build()
            )
            .retrieve()
            .body(GNewsResponse.class);
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
