package io.github.lefpap.briefly.briefs.internal.web;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record BriefRequest(
    @NotBlank @Max(200) String query,
    @Nullable Instant from,
    @Nullable Instant to
) {
}
