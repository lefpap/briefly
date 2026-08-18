package io.github.lefpap.briefly.lib.gnews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;
import java.time.Instant;

public final class GNews {

    private GNews() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Article(
        String id,
        String title,
        String description,
        String content,
        URI url,
        URI image,
        Instant publishedAt,
        String lang,
        Source source
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Source(
        String id,
        String name,
        URI url,
        String country
    ) {
    }
}
