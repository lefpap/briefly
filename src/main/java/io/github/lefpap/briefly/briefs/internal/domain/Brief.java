package io.github.lefpap.briefly.briefs.internal.domain;

import io.github.lefpap.briefly.news.api.ArticleSearchCriteria;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record Brief(
    ArticleSearchCriteria criteria,
    String title,
    String overview,
    List<Highlight> highlights,
    List<SourceArticle> sourceArticles,
    Instant generatedAt
) {

    @Builder(toBuilder = true)
    public record Highlight(
        String text,
        List<Integer> citationIds
    ) {
    }
}
