package io.github.lefpap.briefly.briefs.internal.web;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class BriefTemplateSupport {

    private static final List<String> SUPPORTED_SCHEMES = List.of("http", "https");

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter
        .ofPattern("d MMM uuuu, HH:mm:ss 'UTC'", Locale.ENGLISH)
        .withZone(ZoneOffset.UTC);

    private static final DateTimeFormatter DATE = DateTimeFormatter
        .ofPattern("d MMM uuuu", Locale.ENGLISH)
        .withZone(ZoneOffset.UTC);

    private static final Pattern LINE_BREAK = Pattern.compile("\\R");

    private static final Pattern PARAGRAPH_BREAK = Pattern.compile("\\n\\s*\\n");

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public String timestamp(Instant instant) {
        return instant == null ? null : TIMESTAMP.format(instant);
    }

    public String date(Instant instant) {
        return instant == null ? null : DATE.format(instant);
    }

    /**
     * Splits text into paragraphs at blank lines and joins the hard line breaks inside each
     * paragraph, so wrapped model output reflows to the page width.
     */
    public List<String> paragraphs(String text) {
        if (text == null) {
            return List.of();
        }

        String normalized = LINE_BREAK.matcher(text.strip()).replaceAll("\n");

        return PARAGRAPH_BREAK.splitAsStream(normalized)
            .map(paragraph -> WHITESPACE.matcher(paragraph).replaceAll(" ").strip())
            .filter(paragraph -> !paragraph.isEmpty())
            .toList();
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
