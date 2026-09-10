package io.github.lefpap.briefly.news.internal.gnews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.net.URI;
import java.time.Instant;

public final class GNews {

    private GNews() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder(toBuilder = true)
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
    @Builder(toBuilder = true)
    public record Source(
        String id,
        String name,
        URI url,
        String country
    ) {
    }
}
