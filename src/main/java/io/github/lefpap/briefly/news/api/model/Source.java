package io.github.lefpap.briefly.news.api.model;

import lombok.Builder;

import java.net.URI;

@Builder(toBuilder = true)
public record Source(
    String name,
    URI url,
    String country
) {
}
