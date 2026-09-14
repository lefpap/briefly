package io.github.lefpap.briefly.briefs.internal.web;

import io.github.lefpap.briefly.briefs.internal.domain.exception.BriefGenerationException;
import io.github.lefpap.briefly.briefs.internal.domain.exception.InsufficientGenerationContextException;
import io.github.lefpap.briefly.news.api.exception.ArticleSearchException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String INVALID_REQUEST = "INVALID_REQUEST";
    private static final String ARTICLE_SEARCH_FAILED = "ARTICLE_SEARCH_FAILED";
    private static final String BRIEF_GENERATION_FAILED = "BRIEF_GENERATION_FAILED";
    private static final String INSUFFICIENT_GENERATION_CONTEXT = "INSUFFICIENT_GENERATION_CONTEXT";
    private static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<Map<String, String>> errors = exception.getBindingResult().getFieldErrors()
            .stream()
            .sorted(Comparator.comparing(FieldError::getField))
            .map(fieldError -> Map.of(
                "field", fieldError.getField(),
                "message", Objects.requireNonNullElse(fieldError.getDefaultMessage(), "")
            ))
            .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail("The request is invalid.");
        problemDetail.setProperty("code", INVALID_REQUEST);
        problemDetail.setProperty("errors", errors);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
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
        return problemDetail;
    }

    @ExceptionHandler(ArticleSearchException.class)
    ProblemDetail handleArticleSearch(HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problemDetail.setDetail("Article search failed.");
        problemDetail.setProperty("code", ARTICLE_SEARCH_FAILED);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ExceptionHandler(BriefGenerationException.class)
    ProblemDetail handleBriefGeneration(HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problemDetail.setDetail("Brief generation failed.");
        problemDetail.setProperty("code", BRIEF_GENERATION_FAILED);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ExceptionHandler(InsufficientGenerationContextException.class)
    ProblemDetail handleInsufficientGenerationContext(HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        problemDetail.setDetail("Not enough Articles were found to generate a News Brief.");
        problemDetail.setProperty("code", INSUFFICIENT_GENERATION_CONTEXT);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("An unexpected error occurred.");
        problemDetail.setProperty("code", INTERNAL_ERROR);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }
}
