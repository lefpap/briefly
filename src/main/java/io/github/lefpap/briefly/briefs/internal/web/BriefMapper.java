package io.github.lefpap.briefly.briefs.internal.web;

import io.github.lefpap.briefly.briefs.internal.domain.Brief;
import io.github.lefpap.briefly.briefs.internal.domain.SourceArticle;
import io.github.lefpap.briefly.news.api.ArticleSearchCriteria;

import java.util.List;

final class BriefMapper {

    private BriefMapper() {
    }

    static ArticleSearchCriteria toArticleSearchCriteria(BriefRequest request) {
        return ArticleSearchCriteria.builder()
            .query(request.query())
            .from(request.from())
            .to(request.to())
            .build();
    }

    static BriefResponse toBriefResponse(Brief brief) {
        BriefResponse.Criteria criteria = BriefResponse.Criteria.builder()
            .query(brief.criteria().query())
            .from(brief.criteria().from())
            .to(brief.criteria().to())
            .build();

        List<BriefResponse.Highlight> highlights = brief.highlights().stream()
            .map(BriefMapper::toResponseHighlight)
            .toList();

        List<BriefResponse.Article> sourceArticles = brief.sourceArticles().stream()
            .map(BriefMapper::toResponseArticle)
            .toList();

        return BriefResponse.builder()
            .criteria(criteria)
            .title(brief.title())
            .overview(brief.overview())
            .highlights(highlights)
            .sourceArticles(sourceArticles)
            .generatedAt(brief.generatedAt())
            .build();
    }

    private static BriefResponse.Highlight toResponseHighlight(Brief.Highlight highlight) {
        return BriefResponse.Highlight.builder()
            .text(highlight.text())
            .citationIds(highlight.citationIds())
            .build();
    }

    private static BriefResponse.Article toResponseArticle(SourceArticle sourceArticle) {
        var article = sourceArticle.article();
        var publisher = article.publisher();
        var metadata = article.metadata();
        return BriefResponse.Article.builder()
            .citationId(sourceArticle.citationId())
            .title(article.title())
            .description(article.description())
            .url(article.url())
            .publisher(BriefResponse.Publisher.builder()
                .name(publisher.name())
                .url(publisher.url())
                .country(publisher.country())
                .build())
            .metadata(BriefResponse.ArticleMetadata.builder()
                .imageUrl(metadata.imageUrl())
                .publishedAt(metadata.publishedAt())
                .sourceLanguage(metadata.sourceLanguage())
                .build())
            .build();
    }
}
