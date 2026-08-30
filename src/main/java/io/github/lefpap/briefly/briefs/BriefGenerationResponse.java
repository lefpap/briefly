package io.github.lefpap.briefly.briefs;

import java.time.Instant;
import java.util.List;

public record BriefGenerationResponse(
    BriefQueryCriteria criteria,
    List<Article> articles,
    Instant generatedAt
) {
}
