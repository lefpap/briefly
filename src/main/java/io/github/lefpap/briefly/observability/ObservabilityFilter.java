package io.github.lefpap.briefly.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Gives every News Brief request a correlation ID and reports its outcome and total duration.
 *
 * <p>The correlation ID is published to the response header and to the SLF4J {@link MDC} before the
 * request is handled, so it is present on error responses and prefixes every log line the request
 * produces. That is what ties these lines to the ones the GNews client, the Article search, the AI
 * call advisor, and the API exception handler write for the same request.
 *
 * <p>The arrival is reported as well as the outcome, so the two lines bracket everything a request
 * logs: a request that hangs or is killed mid-flight leaves the opening line and no closing one,
 * which is the only trace such a request leaves at all.
 */
@Slf4j
public class ObservabilityFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    static final String CORRELATION_ID_MDC_KEY = "correlation_id";

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        log.info("Request started method={} path={}", request.getMethod(), request.getRequestURI());

        long startedAt = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info(
                "Request completed method={} path={} status={} duration={}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                (System.nanoTime() - startedAt) / 1_000_000
            );
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }

    private static String resolveCorrelationId(HttpServletRequest request) {
        String supplied = request.getHeader(CORRELATION_ID_HEADER);
        return StringUtils.hasText(supplied) ? supplied : UUID.randomUUID().toString();
    }
}
