package io.github.lefpap.briefly.news.api;

import lombok.Builder;

import java.net.URI;

@Builder(toBuilder = true)
public record Article(
        String title,
        String description,
        String content,
        URI url,
        Publisher publisher,
        ArticleMetadata metadata
) {
}
