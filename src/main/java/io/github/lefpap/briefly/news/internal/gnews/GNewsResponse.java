package io.github.lefpap.briefly.news.internal.gnews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record GNewsResponse(
    int totalArticles,
    List<GNews.Article> articles
) {
}
