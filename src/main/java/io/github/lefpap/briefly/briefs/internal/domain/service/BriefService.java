package io.github.lefpap.briefly.briefs.internal.domain.service;

import io.github.lefpap.briefly.briefs.internal.domain.model.Brief;
import io.github.lefpap.briefly.briefs.internal.domain.model.BriefGenerationResult;
import io.github.lefpap.briefly.news.api.model.Article;
import io.github.lefpap.briefly.news.api.model.ArticleSearchCriteria;
import io.github.lefpap.briefly.news.api.service.ArticleSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Reports what a News Brief was made of: the Articles the search retrieved, the ones the generated
 * News Brief actually cited, and how many Highlights it came to. This is the one place holding both
 * the retrieved Articles and the generation result.
 *
 * <p>The line carries counts but no duration. The request filter already times the whole request,
 * and the GNews interceptor and the AI advisors time the two calls that account for that time, so a
 * timer here would measure the request over again from inside the flow.
 */
@Slf4j
@Service
public class BriefService {

    private static final Duration DEFAULT_REPORTING_WINDOW_DURATION = Duration.ofDays(1);

    private final ArticleSearchService articleSearchService;
    private final BriefGenerationService briefGenerationService;

    public BriefService(ArticleSearchService articleSearchService, BriefGenerationService briefGenerationService) {
        this.articleSearchService = articleSearchService;
        this.briefGenerationService = briefGenerationService;
    }

    public Brief createBrief(ArticleSearchCriteria criteria) {
        ArticleSearchCriteria resolvedCriteria = resolveSearchCriteria(criteria);
        List<Article> articles = articleSearchService.search(resolvedCriteria);
        BriefGenerationResult result = briefGenerationService.generateBrief(resolvedCriteria.query(), articles);

        log.info(
            "Brief generated articlesRetrieved={}, articlesUsed={}, highlights={}",
            articles.size(),
            result.sourceArticles().size(),
            result.highlights().size()
        );

        return Brief.builder()
            .criteria(resolvedCriteria)
            .title(result.title())
            .overview(result.overview())
            .highlights(result.highlights())
            .sourceArticles(result.sourceArticles())
            .generatedAt(Instant.now())
            .build();
    }

    private static ArticleSearchCriteria resolveSearchCriteria(ArticleSearchCriteria criteria) {
        Instant to = Objects.requireNonNullElseGet(criteria.to(), Instant::now);
        Instant from = Objects.requireNonNullElseGet(criteria.from(), () -> to.minus(DEFAULT_REPORTING_WINDOW_DURATION));
        return criteria.toBuilder()
            .from(from)
            .to(to)
            .build();
    }
}
