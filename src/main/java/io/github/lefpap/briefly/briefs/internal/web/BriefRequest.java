package io.github.lefpap.briefly.briefs.internal.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
@Schema(
    name = "BriefRequest",
    description = """
        A Brief Request: the Query describing the news to cover, plus an optional Reporting Window.

        When `to` is omitted it defaults to the time the request is handled. When `from` is omitted \
        it defaults to 24 hours before the resolved `to`. The resolved Reporting Window is echoed \
        back in the response criteria."""
)
public record BriefRequest(
    @NotBlank(message = "must not be blank")
    @Size(max = 200, message = "must contain at most 200 characters")
    @Schema(
        description = """
            Free-form text describing the news you care about. It may be a subject, a question, or a \
            more detailed interest, and does not require news-provider query syntax. The Query is \
            passed through as written.""",
        example = "European Union artificial intelligence regulation",
        maxLength = 200,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String query,

    @PastOrPresent
    @Nullable
    @Schema(
        description = """
            Start of the Reporting Window as an ISO-8601 instant. Must not be in the future and must \
            not be after `to`. Defaults to 24 hours before the resolved `to`.""",
        example = "2025-05-01T00:00:00Z",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    Instant from,

    @PastOrPresent
    @Nullable
    @Schema(
        description = """
            End of the Reporting Window as an ISO-8601 instant. Must not be in the future. Defaults \
            to the time the request is handled.""",
        example = "2025-05-02T00:00:00Z",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    Instant to
) {

    @AssertTrue(message = "from must be before to")
    @Schema(hidden = true)
    public boolean isFromBeforeTo() {
        if (from == null || to == null) {
            return true;
        }
        return from.equals(to) || from.isBefore(to);
    }
}
