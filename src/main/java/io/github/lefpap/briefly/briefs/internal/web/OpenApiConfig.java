package io.github.lefpap.briefly.briefs.internal.web;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Declares the Problem Details schemas and the reusable error responses that both documented
 * operations refer to. springdoc offers no annotation for {@code #/components/responses}, so they
 * are built here instead of being repeated on every operation. Example payloads live under
 * {@code src/main/resources/openapi/examples}.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
    title = "Briefly API",
    version = "v1",
    description = """
        Briefly creates concise News Briefs by synthesizing Source Articles.

        A Brief Request carries a free-form Query and an optional Reporting Window. Briefly \
        searches published Articles, then generates a News Brief whose Highlights cite the Source \
        Articles intended to support them.

        Article search is restricted to English-language Articles, and every News Brief is written \
        in English regardless of the Source Languages of its Source Articles.

        Generation is synchronous and calls an external news provider and an external AI provider, \
        so a single request can take a while to complete.

        Errors are returned as RFC 9457 Problem Details with an additional `code` property."""
))
public class OpenApiConfig {

    static final String NEWS_BRIEF_EXAMPLE = "#/components/examples/NewsBrief";

    static final String INVALID_REQUEST_RESPONSE = "#/components/responses/InvalidRequest";
    static final String INSUFFICIENT_GENERATION_CONTEXT_RESPONSE = "#/components/responses/InsufficientGenerationContext";
    static final String UPSTREAM_FAILURE_RESPONSE = "#/components/responses/UpstreamFailure";
    static final String INTERNAL_ERROR_RESPONSE = "#/components/responses/InternalError";

    private static final String PROBLEM_DETAIL = "ProblemDetail";
    private static final String VALIDATION_PROBLEM_DETAIL = "ValidationProblemDetail";
    private static final String EXAMPLES_LOCATION = "openapi/examples/";

    static {
        // BriefHtmlController renders a Thymeleaf view, so it is a plain @Controller that springdoc
        // would otherwise skip.
        SpringDocUtils.getConfig().addRestControllers(BriefHtmlController.class);
    }

    @Bean
    public OpenAPI brieflyOpenApi() {
        return new OpenAPI().components(new Components()
            .addSchemas(PROBLEM_DETAIL, problemDetailSchema())
            .addSchemas(VALIDATION_PROBLEM_DETAIL, validationProblemDetailSchema())
            .addExamples("NewsBrief",
                example("A News Brief synthesized from two Source Articles", "news-brief.json"))
            .addResponses("InvalidRequest", invalidRequestResponse())
            .addResponses("InsufficientGenerationContext", insufficientGenerationContextResponse())
            .addResponses("UpstreamFailure", upstreamFailureResponse())
            .addResponses("InternalError", internalErrorResponse()));
    }

    private static Schema<?> problemDetailSchema() {
        return new ObjectSchema()
            .description("RFC 9457 Problem Details, extended with a stable machine-readable `code`.")
            .addProperty("type", new StringSchema().format("uri").example("about:blank"))
            .addProperty("title", new StringSchema().example("Bad Gateway"))
            .addProperty("status", new IntegerSchema().format("int32").example(502))
            .addProperty("detail", new StringSchema().example("Article search failed."))
            .addProperty("instance", new StringSchema().format("uri")
                .description("Path of the request that produced the problem.")
                .example("/api/briefs/generate"))
            .addProperty("code", new StringSchema()
                .description("Stable machine-readable error code.")
                .example("ARTICLE_SEARCH_FAILED"));
    }

    private static Schema<?> validationProblemDetailSchema() {
        Schema<?> error = new ObjectSchema()
            .addProperty("field", new StringSchema()
                .description("Rejected request property, or `request` when the body could not be read.")
                .example("query"))
            .addProperty("message", new StringSchema()
                .description("Reason the property was rejected.")
                .example("must not be blank"));

        return new ObjectSchema()
            .description("Problem Details for a rejected Brief Request, including each validation failure.")
            .addAllOfItem(new Schema<>().$ref(schemaRef(PROBLEM_DETAIL)))
            .addProperty("errors", new ArraySchema()
                .items(error)
                .description("Validation failures, sorted by field name."));
    }

    private static ApiResponse invalidRequestResponse() {
        Map<String, Example> examples = new LinkedHashMap<>();
        examples.put("validationFailed",
            example("A request property failed validation", "validation-failed.json"));
        examples.put("unreadableBody",
            example("The request body was not valid JSON", "unreadable-body.json"));

        return response("The Brief Request was rejected because it is invalid or unreadable.",
            VALIDATION_PROBLEM_DETAIL, examples);
    }

    private static ApiResponse insufficientGenerationContextResponse() {
        Map<String, Example> examples = Map.of("insufficientGenerationContext",
            example("Fewer than two Articles were available", "insufficient-generation-context.json"));

        return response("""
            Fewer than two Articles matched the Brief Criteria, so no News Brief could be \
            synthesized. Widening the Reporting Window or broadening the Query may help.""",
            PROBLEM_DETAIL, examples);
    }

    private static ApiResponse upstreamFailureResponse() {
        Map<String, Example> examples = new LinkedHashMap<>();
        examples.put("articleSearchFailed",
            example("The news provider could not be queried", "article-search-failed.json"));
        examples.put("briefGenerationFailed",
            example("The AI provider failed or returned an unusable News Brief", "brief-generation-failed.json"));

        return response("""
            An upstream provider failed. `ARTICLE_SEARCH_FAILED` means Articles could not be \
            searched. `BRIEF_GENERATION_FAILED` means the AI provider failed or returned a News \
            Brief that violated the output constraints, such as unknown Citation IDs or fewer than \
            two cited Source Articles.""",
            PROBLEM_DETAIL, examples);
    }

    private static ApiResponse internalErrorResponse() {
        Map<String, Example> examples = Map.of("internalError",
            example("Unexpected failure", "internal-error.json"));

        return response("An unexpected error occurred while handling the Brief Request.",
            PROBLEM_DETAIL, examples);
    }

    private static ApiResponse response(String description, String schemaName, Map<String, Example> examples) {
        MediaType mediaType = new MediaType().schema(new Schema<>().$ref(schemaRef(schemaName)));
        examples.forEach(mediaType::addExamples);
        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType("application/problem+json", mediaType));
    }

    /**
     * Loads an example payload from the classpath. Parsing with the Swagger mapper keeps the example
     * a JSON document in the published contract rather than an escaped string.
     */
    private static Example example(String summary, String fileName) {
        String location = EXAMPLES_LOCATION + fileName;
        try (InputStream payload = new ClassPathResource(location).getInputStream()) {
            return new Example()
                .summary(summary)
                .value(Json.mapper().readTree(payload));
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read OpenAPI example " + location, exception);
        }
    }

    private static String schemaRef(String schemaName) {
        return "#/components/schemas/" + schemaName;
    }
}
