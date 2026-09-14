package io.github.lefpap.briefly.briefs.internal.web;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record BriefRequest(
    @NotBlank(message = "must not be blank")
    @Size(max = 200, message = "must contain at most 200 characters")
    String query,
    @PastOrPresent @Nullable Instant from,
    @PastOrPresent @Nullable Instant to
) {

    @AssertTrue(message = "from must be before to")
    public boolean isFromBeforeTo() {
        if (from == null || to == null) {
            return true;
        }
        return from.equals(to) || from.isBefore(to);
    }
}
