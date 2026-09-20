package io.github.lefpap.briefly.briefs.internal.web;

import io.github.lefpap.briefly.briefs.internal.domain.Brief;
import io.github.lefpap.briefly.briefs.internal.domain.BriefService;
import io.github.lefpap.briefly.news.api.ArticleSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/briefs")
@Tag(name = "News Briefs", description = "Generate a News Brief from a Brief Request.")
public class BriefHtmlController {

    private final BriefService briefService;

    public BriefHtmlController(BriefService briefService) {
        this.briefService = briefService;
    }

    @Operation(
        summary = "Generate a News Brief as a rendered HTML page",
        description = """
            Accepts the same JSON Brief Request as `POST /api/briefs/generate` and returns the same \
            News Brief rendered as a standalone HTML page instead of JSON.

            The Reporting Window is resolved the same way: a missing `to` becomes the time the \
            request is handled, and a missing `from` becomes 24 hours before the resolved `to`. \
            Article search is restricted to English-language Articles and the News Brief is written \
            in English.

            Errors are still returned as `application/problem+json`, not HTML."""
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "The News Brief was generated and rendered as an HTML document.",
            content = @Content(
                mediaType = MediaType.TEXT_HTML_VALUE,
                schema = @Schema(type = "string", description = "A standalone HTML document."))),
        @ApiResponse(responseCode = "400", ref = OpenApiConfig.INVALID_REQUEST_RESPONSE),
        @ApiResponse(responseCode = "422", ref = OpenApiConfig.INSUFFICIENT_GENERATION_CONTEXT_RESPONSE),
        @ApiResponse(responseCode = "500", ref = OpenApiConfig.INTERNAL_ERROR_RESPONSE),
        @ApiResponse(responseCode = "502", ref = OpenApiConfig.UPSTREAM_FAILURE_RESPONSE)
    })
    @PostMapping(value = "/generate/html", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.TEXT_HTML_VALUE)
    public String generateBriefHtml(@Valid @RequestBody BriefRequest request,
                                    @Parameter(hidden = true) Model model) {
        ArticleSearchCriteria criteria = BriefMapper.toArticleSearchCriteria(request);
        Brief brief = briefService.createBrief(criteria);
        model.addAttribute("brief", brief);
        return "brief";
    }
}
