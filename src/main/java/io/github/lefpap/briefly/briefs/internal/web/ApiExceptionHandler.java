package io.github.lefpap.briefly.briefs.internal.web;

import io.github.lefpap.briefly.briefs.internal.domain.BriefGenerationException;
import io.github.lefpap.briefly.briefs.internal.domain.InsufficientGenerationContextException;
import io.github.lefpap.briefly.news.api.ArticleSearchException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.*;

/**
 * An unexpected failure is logged at {@code ERROR}: it is a Briefly defect, and a defect nobody can
 * see is worse than the risk of logging it.
 *
 * <p>An insufficient generation context is logged at {@code DEBUG} too, so its cause is recorded at
 * all: its message carries only Article counts, so the level is about volume rather than content.
 *
 * <p>The two provider failures are logged at {@code DEBUG} instead, because their cause chains carry
 * what normal operational logs must not: a failed GNews call unwraps to a RestClientResponseException
 * whose message embeds the raw response body. Their stable code is still logged at {@code WARN}, and
 * the GNews call logs its upstream status, so the failure stays visible without its content.
 *
 * <p>Spring MVC rejects some requests before they reach a controller: an unknown path, or a method or
 * media type an endpoint doesn't support. Those exceptions carry their own 4xx status, so they are
 * reported as invalid requests with that status rather than as unexpected failures.
 */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String INVALID_REQUEST = "INVALID_REQUEST";
    private static final String ARTICLE_SEARCH_FAILED = "ARTICLE_SEARCH_FAILED";
    private static final String BRIEF_GENERATION_FAILED = "BRIEF_GENERATION_FAILED";
    private static final String INSUFFICIENT_GENERATION_CONTEXT = "INSUFFICIENT_GENERATION_CONTEXT";
    private static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .sorted(Comparator.comparing(FieldError::getField))
            .map(fieldError -> Map.of(
                "field", fieldError.getField(),
                "message", Objects.requireNonNullElse(fieldError.getDefaultMessage(), "")
            ))
            .toList();

        log.warn("Validation failed for request: {}", errors);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail("The request is invalid.");
        problemDetail.setProperty("code", INVALID_REQUEST);
        problemDetail.setProperty("errors", errors);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        logDetails(problemDetail);

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleUnreadableRequest(HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail("The request is invalid.");
        problemDetail.setProperty("code", INVALID_REQUEST);
        problemDetail.setProperty("errors", List.of(Map.of(
            "field", "request",
            "message", "must contain a valid JSON request body"
        )));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        logDetails(problemDetail);

        return problemDetail;
    }

    @ExceptionHandler(ArticleSearchException.class)
    ProblemDetail handleArticleSearch(ArticleSearchException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problemDetail.setDetail("Article search failed.");
        problemDetail.setProperty("code", ARTICLE_SEARCH_FAILED);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        logDetails(problemDetail);

        log.debug("Article search failed", ex);
        return problemDetail;
    }

    @ExceptionHandler(BriefGenerationException.class)
    ProblemDetail handleBriefGeneration(BriefGenerationException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problemDetail.setDetail("Brief generation failed.");
        problemDetail.setProperty("code", BRIEF_GENERATION_FAILED);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        logDetails(problemDetail);

        log.debug("Brief generation failed", ex);
        return problemDetail;
    }

    @ExceptionHandler(InsufficientGenerationContextException.class)
    ProblemDetail handleInsufficientGenerationContext(
        InsufficientGenerationContextException ex,
        HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        problemDetail.setDetail("Not enough Articles were found to generate a News Brief.");
        problemDetail.setProperty("code", INSUFFICIENT_GENERATION_CONTEXT);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        logDetails(problemDetail);

        log.debug("Generation context insufficient", ex);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> handleUnexpected(Exception exception, HttpServletRequest request) {
        if (exception instanceof ErrorResponse rejection && rejection.getStatusCode().is4xxClientError()) {
            return handleRejectedRequest(rejection, request);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("An unexpected error occurred.");
        problemDetail.setProperty("code", INTERNAL_ERROR);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        logDetails(problemDetail);

        log.error("Unexpected failure", exception);
        return ResponseEntity.internalServerError().body(problemDetail);
    }

    private ResponseEntity<ProblemDetail> handleRejectedRequest(ErrorResponse rejection, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(rejection.getStatusCode());
        problemDetail.setDetail("The request is invalid.");
        problemDetail.setProperty("code", INVALID_REQUEST);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        logDetails(problemDetail);

        // Keeps headers such as Allow on a 405, which tell the client what the endpoint accepts.
        return ResponseEntity.status(rejection.getStatusCode())
            .headers(rejection.getHeaders())
            .body(problemDetail);
    }

    private void logDetails(ProblemDetail problemDetail) {
        Object code = Optional.ofNullable(problemDetail.getProperties())
            .map(props -> props.get("code"))
            .orElse("UNKNOWN");

        log.warn("Brief request rejected status={} code={}", problemDetail.getStatus(), code);
    }
}
