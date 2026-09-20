package io.github.lefpap.briefly.briefs.internal.domain;

import io.github.lefpap.briefly.news.api.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The rejection is reported at {@code DEBUG}: the Article count is already on the Article search
 * line at {@code INFO}, and the API exception handler already names the stable code, so this adds
 * the threshold that was missed rather than a second account of the same failure.
 */
@Slf4j
@Component
public class BriefGenerationContextValidator {

    private static final int MINIMUM_CONTEXT_ARTICLE_COUNT = 2;

    public void validate(List<Article> articles) {
        int articleCount = articles.size();
        if (articleCount < MINIMUM_CONTEXT_ARTICLE_COUNT) {
            log.debug(
                "Generation context insufficient articles={} minimum={}",
                articleCount,
                MINIMUM_CONTEXT_ARTICLE_COUNT
            );
            throw new InsufficientGenerationContextException(
                "Generation context requires at least %d Articles; article search returned %d"
                    .formatted(MINIMUM_CONTEXT_ARTICLE_COUNT, articleCount)
            );
        }
    }
}
