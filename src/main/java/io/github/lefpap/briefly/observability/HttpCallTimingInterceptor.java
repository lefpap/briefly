package io.github.lefpap.briefly.observability;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Logs how long an outbound HTTP call takes, under the correlation ID of the request that made it.
 *
 * <p>Registered on every {@code RestClient.Builder} at once, so calls are told apart per request: a
 * client names itself with {@link #named(String)} and reads as {@code "GNews call completed ..."},
 * and one that does not is reported by host.
 *
 * <p>The advice deliberately reads nothing out of the request or the response beyond that name and
 * the response status: the request URI carries the query parameters, and those can be prompt or user
 * content that must not reach the log. A failure is reported with its exception type for the same
 * reason: the message can embed the URI or the raw response body.
 */
@Slf4j
public class HttpCallTimingInterceptor implements ClientHttpRequestInterceptor {

    private static final String CALL_NAME_ATTRIBUTE = "briefly.observability.callName";

    /**
     * Names the calls a {@code RestClient} makes, for {@code RestClient.Builder#defaultRequest}.
     */
    public static Consumer<RestClient.RequestHeadersSpec<?>> named(String callName) {
        return spec -> spec.attribute(CALL_NAME_ATTRIBUTE, callName);
    }

    @Override
    public @NonNull ClientHttpResponse intercept(
        @NonNull HttpRequest request,
        byte @NonNull [] body,
        @NonNull ClientHttpRequestExecution execution
    ) throws IOException {
        String callName = resolveCallName(request);

        long startedAt = System.nanoTime();
        try {
            ClientHttpResponse response = execution.execute(request, body);
            log.info(
                "[{}] call completed status={} duration={}ms",
                callName,
                response.getStatusCode(),
                elapsedMillis(startedAt)
            );
            return response;
        } catch (IOException | RuntimeException exception) {
            // Only the type: the message can carry the request URI or the raw response body.
            log.warn(
                "[{}] call failed duration={}ms exception={}",
                callName,
                elapsedMillis(startedAt),
                exception.getClass().getName()
            );
            throw exception;
        }
    }

    private static String resolveCallName(HttpRequest request) {
        Object callName = request.getAttributes().get(CALL_NAME_ATTRIBUTE);
        if (callName instanceof String name && StringUtils.hasText(name)) {
            return name;
        }
        return request.getURI().getHost();
    }

    private static long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
