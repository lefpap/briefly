package io.github.lefpap.briefly.briefs.internal.domain.util;

import io.github.lefpap.briefly.briefs.internal.domain.exception.InsufficientGenerationContextException;
import io.github.lefpap.briefly.news.api.model.Article;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BriefGenerationContextValidator {

    private static final int MINIMUM_CONTEXT_ARTICLE_COUNT = 2;

    public void validate(List<Article> articles) {
        int articleCount = articles.size();
        if (articleCount < MINIMUM_CONTEXT_ARTICLE_COUNT) {
            throw new InsufficientGenerationContextException(
                "Generation context requires at least %d Articles; article search returned %d"
                    .formatted(MINIMUM_CONTEXT_ARTICLE_COUNT, articleCount)
            );
        }
    }
}
