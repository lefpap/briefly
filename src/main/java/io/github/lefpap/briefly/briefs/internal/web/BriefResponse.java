package io.github.lefpap.briefly.briefs.internal.web;

import lombok.Builder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record BriefResponse(
    Criteria criteria,
    String title,
    String overview,
    List<Highlight> highlights,
    List<Article> sourceArticles,
    Instant generatedAt
) {

    @Builder(toBuilder = true)
    public record Criteria(
        String query,
        Instant from,
        Instant to
    ) {
    }

    @Builder(toBuilder = true)
    public record Highlight(
        String text,
        List<Integer> citationIds
    ) {
    }

    @Builder(toBuilder = true)
    public record Article(
        Integer citationId,
        String title,
        String description,
        URI url,
        Source source,
        ArticleMetadata metadata
    ) {
    }

    @Builder(toBuilder = true)
    public record Source(
        String name,
        URI url,
        String country
    ) {
    }

    @Builder(toBuilder = true)
    public record ArticleMetadata(
        URI imageUrl,
        Instant publishedAt,
        String sourceLanguage
    ) {
    }
}
