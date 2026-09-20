package io.github.lefpap.briefly.news.api;

import lombok.Builder;

import java.net.URI;

@Builder(toBuilder = true)
public record Publisher(
    String name,
    URI url,
    String country
) {
}
