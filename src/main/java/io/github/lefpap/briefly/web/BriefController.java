package io.github.lefpap.briefly.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.lefpap.briefly.lib.gnews.GNews;
import io.github.lefpap.briefly.lib.gnews.GNewsClient;
import io.github.lefpap.briefly.lib.gnews.GNewsResponse;
import io.github.lefpap.briefly.lib.gnews.GNewsSearchParams;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/briefs")
public class BriefController {

    private final GNewsClient newsClient;

    public BriefController(GNewsClient newsClient) {
        this.newsClient = newsClient;
    }

    @PostMapping("/generate")
    public BriefGenerationResponse generateBrief(@Valid @RequestBody BriefController.BriefQueryCriteria criteria) {
        var params = buildGnewsSearchParams(criteria);
        GNewsResponse response = newsClient.search(params);
        return new BriefGenerationResponse(criteria, response.articles(), Instant.now());
    }

    private static GNewsSearchParams buildGnewsSearchParams(BriefQueryCriteria criteria) {
        return GNewsSearchParams.builder()
            .q(criteria.topic().strip())
            .lang(criteria.sourceLang())
            .country(criteria.publicationCountry())
            .searchIn("title,description,content")
            .sortBy("relevance")
            .max(10)
            .truncate("content")
            .from(criteria.publishedFrom())
            .to(criteria.publishedTo())
            .build();
    }

    public record BriefGenerationResponse(
        BriefQueryCriteria criteria,
        // TODO: replace these with the source articles (currently showing genews raw articles)
        List<GNews.Article> articles,
        Instant generatedAt
    ) {
    }

    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BriefQueryCriteria(
        @NotBlank String topic,
        @Nullable String sourceLang,
        @Nullable String outputLang,
        @Nullable String publicationCountry,
        @Nullable Instant publishedFrom,
        @Nullable Instant publishedTo
    ) {

        private static final String DEFAULT_OUTPUT_LANGUAGE = "en";
        private static final Duration DEFAULT_REPORTING_WINDOW_DURATION = Duration.ofDays(1);

        public BriefQueryCriteria {
            if (Objects.isNull(outputLang)) {
                outputLang = DEFAULT_OUTPUT_LANGUAGE;
            }
            if (Objects.isNull(publishedTo)) {
                publishedTo = Instant.now();
            }
            if (Objects.isNull(publishedFrom)) {
                publishedFrom = publishedTo.minus(DEFAULT_REPORTING_WINDOW_DURATION);
            }
        }
    }
}
