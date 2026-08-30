package io.github.lefpap.briefly.briefs;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/briefs")
public class BriefController {

    private final ArticleSearchService articleSearchService;

    public BriefController(ArticleSearchService articleSearchService) {
        this.articleSearchService = articleSearchService;
    }

    @PostMapping("/generate")
    public BriefGenerationResponse generateBrief(@Valid @RequestBody BriefQueryCriteria criteria) {
        var articles = articleSearchService.search(criteria);
        return new BriefGenerationResponse(criteria, articles, Instant.now());
    }
}
