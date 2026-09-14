package io.github.lefpap.briefly.briefs.internal.web;

import io.github.lefpap.briefly.briefs.internal.domain.model.Brief;
import io.github.lefpap.briefly.briefs.internal.domain.service.BriefService;
import io.github.lefpap.briefly.news.api.model.ArticleSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/briefs")
@Tag(name = "News Briefs", description = "Generate a News Brief from a Brief Request.")
public class BriefController {

    private final BriefService briefService;

    public BriefController(BriefService briefService) {
        this.briefService = briefService;
    }

    @Operation(
        summary = "Generate a News Brief as JSON",
        description = """
            Searches English-language Articles published within the Reporting Window and synthesizes \
            them into a News Brief written in English.

            The Reporting Window is resolved per request: a missing `to` becomes the time the \
            request is handled, and a missing `from` becomes 24 hours before the resolved `to`. The \
            resolved window is returned in `criteria`.

            The response carries a title, a narrative Overview, one to five Highlights, and the \
            Source Articles those Highlights cite. A Citation identifies the Source Article intended \
            to support a Highlight; it is not proof of factual correctness or semantic grounding.

            At least two Articles must be available, otherwise the request fails with \
            `INSUFFICIENT_GENERATION_CONTEXT`."""
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "The News Brief was generated.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BriefResponse.class),
                examples = @ExampleObject(name = "NewsBrief", ref = OpenApiConfig.NEWS_BRIEF_EXAMPLE))),
        @ApiResponse(responseCode = "400", ref = OpenApiConfig.INVALID_REQUEST_RESPONSE),
        @ApiResponse(responseCode = "422", ref = OpenApiConfig.INSUFFICIENT_GENERATION_CONTEXT_RESPONSE),
        @ApiResponse(responseCode = "500", ref = OpenApiConfig.INTERNAL_ERROR_RESPONSE),
        @ApiResponse(responseCode = "502", ref = OpenApiConfig.UPSTREAM_FAILURE_RESPONSE)
    })
    @PostMapping("/generate")
    public BriefResponse generateBrief(@Valid @RequestBody BriefRequest request) {
        ArticleSearchCriteria criteria = BriefMapper.toArticleSearchCriteria(request);
        Brief brief = briefService.createBrief(criteria);
        return BriefMapper.toBriefResponse(brief);
    }
}
