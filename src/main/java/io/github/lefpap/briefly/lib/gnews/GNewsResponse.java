package io.github.lefpap.briefly.lib.gnews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GNewsResponse(
    int totalArticles,
    List<GNews.Article> articles
) {
}
