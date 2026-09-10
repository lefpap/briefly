package io.github.lefpap.briefly.briefs.internal.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GeneratedBrief(
    @JsonProperty(required = true)
    @JsonPropertyDescription("A short, neutral title for the News Brief.")
    String title,
    @JsonProperty(required = true)
    @JsonPropertyDescription("An Overview synthesizing the Highlights, with no additional facts.")
    String overview,
    @JsonProperty(required = true)
    @JsonPropertyDescription("One to five narrative Highlights, ordered by importance and then recency.")
    List<GeneratedHighlight> highlights
) {
    @Builder(toBuilder = true)
    public record GeneratedHighlight(
        @JsonProperty(required = true)
        @JsonPropertyDescription("A self-contained narrative about one important development or meaningful disagreement, supported by the cited Articles.")
        String text,
        @JsonProperty(required = true)
        @JsonPropertyDescription("The supplied Citation IDs of all Articles intended to support this Highlight; include at least one.")
        List<Integer> citationIds
    ) {
    }
}
