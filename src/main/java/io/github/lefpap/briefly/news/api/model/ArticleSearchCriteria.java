package io.github.lefpap.briefly.news.api.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record ArticleSearchCriteria(
    @NotBlank String query,
    @Nullable Instant from,
    @Nullable Instant to
) {
}
