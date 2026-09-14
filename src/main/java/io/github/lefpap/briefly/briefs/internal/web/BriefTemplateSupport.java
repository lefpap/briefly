package io.github.lefpap.briefly.briefs.internal.web;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class BriefTemplateSupport {

    private static final List<String> SUPPORTED_SCHEMES = List.of("http", "https");

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter
        .ofPattern("d MMM uuuu, HH:mm:ss 'UTC'", Locale.ENGLISH)
        .withZone(ZoneOffset.UTC);

    public String timestamp(Instant instant) {
        return instant == null ? null : TIMESTAMP.format(instant);
    }

    public URI httpUri(URI uri) {
        if (uri == null) {
            return null;
        }

        if (!SUPPORTED_SCHEMES.contains(uri.getScheme().toLowerCase(Locale.ROOT))) {
            return null;
        }

        return uri;
    }
}
