package io.github.lefpap.briefly.news.api.model;

import lombok.Builder;

import java.net.URI;
import java.time.Instant;

@Builder(toBuilder = true)
public record ArticleMetadata(
    URI imageUrl,
    Instant publishedAt,
    String sourceLanguage
) {
}
