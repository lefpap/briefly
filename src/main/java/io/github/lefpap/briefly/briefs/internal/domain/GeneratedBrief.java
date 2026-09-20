package io.github.lefpap.briefly.briefs.internal.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GeneratedBrief(

    @JsonProperty(required = true)
    @JsonPropertyDescription("A short, neutral title for the News Brief.")
    @NotBlank
    String title,

    @JsonProperty(required = true)
    @JsonPropertyDescription("An Overview of up to five paragraphs, separated by blank lines, synthesizing the Highlights, with no additional facts.")
    @NotBlank
    String overview,

    @JsonProperty(required = true)
    @JsonPropertyDescription("One to five narrative Highlights, ordered by importance and then recency.")
    @NotEmpty
    @Size(max = 5)
    List<@NotNull @Valid GeneratedHighlight> highlights
) {
    @Builder(toBuilder = true)
    public record GeneratedHighlight(

        @JsonProperty(required = true)
        @JsonPropertyDescription("A self-contained narrative about one important development or meaningful disagreement, supported by the cited Articles.")
        @NotBlank
        String text,

        @JsonProperty(required = true)
        @JsonPropertyDescription("The supplied Citation IDs of all Articles intended to support this Highlight; include at least one.")
        @NotEmpty
        List<@NotNull @Positive Integer> citationIds
    ) {
    }
}
