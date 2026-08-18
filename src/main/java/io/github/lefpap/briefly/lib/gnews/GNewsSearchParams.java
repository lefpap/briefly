package io.github.lefpap.briefly.lib.gnews;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Objects;

@Builder
public record GNewsSearchParams(
    @NotBlank String q,
    String lang,
    String country,
    String searchIn,
    Instant from,
    Instant to,
    String sortBy,
    Integer max,
    String truncate
) {

    private static final String Q = "q";
    private static final String LANG = "lang";
    private static final String COUNTRY = "country";
    private static final String SEARCH_IN = "in";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String SORT_BY = "sortby";
    private static final String MAX = "max";
    private static final String TRUNCATE = "truncate";

    public static GNewsSearchParams q(String q) {
        return GNewsSearchParams.builder().q(q).build();
    }

    public MultiValueMap<String, String> toQueryParams() {
        var map = new LinkedHashMap<String, String>();
        map.put(Q, this.q);
        if (Objects.nonNull(this.lang)) {
            map.put(LANG, lang);
        }
        if (Objects.nonNull(this.country)) {
            map.put(COUNTRY, country);
        }
        if (Objects.nonNull(this.searchIn)) {
            map.put(SEARCH_IN, searchIn);
        }
        if (Objects.nonNull(this.from)) {
            map.put(FROM, formatInstant(from));
        }
        if (Objects.nonNull(this.to)) {
            map.put(TO, formatInstant(to));
        }
        if (Objects.nonNull(this.sortBy)) {
            map.put(SORT_BY, sortBy);
        }
        if (Objects.nonNull(this.max)) {
            map.put(MAX, max.toString());
        }
        if (Objects.nonNull(this.truncate)) {
            map.put(TRUNCATE, this.truncate);
        }

        return MultiValueMap.fromSingleValue(map);
    }

    private static String formatInstant(Instant instant) {
        return instant.truncatedTo(ChronoUnit.SECONDS).toString();
    }
}
