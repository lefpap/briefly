package io.github.lefpap.briefly.briefs;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BriefQueryCriteria(
    @NotBlank String topic,
    @Nullable String sourceLang,
    @Nullable String outputLang,
    @Nullable String publicationCountry,
    @Nullable Instant publishedFrom,
    @Nullable Instant publishedTo
) {

    private static final String DEFAULT_OUTPUT_LANGUAGE = "en";
    private static final Duration DEFAULT_REPORTING_WINDOW_DURATION = Duration.ofDays(1);

    public BriefQueryCriteria {
        if (Objects.isNull(outputLang)) {
            outputLang = DEFAULT_OUTPUT_LANGUAGE;
        }
        if (Objects.isNull(publishedTo)) {
            publishedTo = Instant.now();
        }
        if (Objects.isNull(publishedFrom)) {
            publishedFrom = publishedTo.minus(DEFAULT_REPORTING_WINDOW_DURATION);
        }
    }
}
