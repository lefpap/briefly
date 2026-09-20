package io.github.lefpap.briefly.briefs.internal.domain;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record BriefGenerationResult(
    String title,
    String overview,
    List<Brief.Highlight> highlights,
    List<SourceArticle> sourceArticles
) {
}
