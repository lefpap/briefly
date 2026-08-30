package io.github.lefpap.briefly.briefs;

import java.net.URI;

public record Source(
    String name,
    URI url,
    String country
) {
}
