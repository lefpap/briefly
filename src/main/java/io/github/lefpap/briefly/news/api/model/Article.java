package io.github.lefpap.briefly.news.api.model;

import lombok.Builder;

import java.net.URI;

@Builder(toBuilder = true)
public record Article(
        String title,
        String description,
        String content,
        URI url,
        Source source,
        ArticleMetadata metadata
) {
}
