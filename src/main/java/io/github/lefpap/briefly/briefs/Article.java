package io.github.lefpap.briefly.briefs;

public record Article(
    String title,
    String description,
    String content,
    Source source,
    ArticleMetadata metadata
) {
}
