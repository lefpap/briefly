package io.github.lefpap.briefly.briefs.internal.domain;

import io.github.lefpap.briefly.news.api.Article;
import lombok.Builder;

@Builder(toBuilder = true)
public record SourceArticle(
    Integer citationId,
    Article article
) {
}
