package io.github.lefpap.briefly.briefs;

import java.net.URI;
import java.time.Instant;

public record ArticleMetadata(
    URI url,
    URI imageUrl,
    Instant publishedAt,
    String sourceLanguage
) {
}
