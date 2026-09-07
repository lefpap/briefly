package io.github.lefpap.briefly.briefs.internal.domain.model;

import io.github.lefpap.briefly.news.api.model.Article;
import lombok.Builder;

@Builder(toBuilder = true)
public record SourceArticle(
    Integer citationId,
    Article article
) {
}
