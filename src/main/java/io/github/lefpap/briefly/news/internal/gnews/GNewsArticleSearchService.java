package io.github.lefpap.briefly.news.internal.gnews;

import io.github.lefpap.briefly.news.api.exception.ArticleSearchException;
import io.github.lefpap.briefly.news.api.model.Article;
import io.github.lefpap.briefly.news.api.model.ArticleMetadata;
import io.github.lefpap.briefly.news.api.model.ArticleSearchCriteria;
import io.github.lefpap.briefly.news.api.model.Publisher;
import io.github.lefpap.briefly.news.api.service.ArticleSearchService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class GNewsArticleSearchService implements ArticleSearchService {

    private static final String SOURCE_LANG = "en";
    private static final String SEARCH_IN_FIELDS = "title,description,content";
    private static final String SORT_BY = "relevance";
    private static final int MAX_RESULTS = 10;
    private static final String TRUNCATE = "content";

    private final GNewsClient newsClient;

    public GNewsArticleSearchService(GNewsClient newsClient) {
        this.newsClient = newsClient;
    }

    @Override
    public List<Article> search(ArticleSearchCriteria criteria) {
        var params = buildGnewsSearchParams(criteria);
        try {
            GNewsResponse response = newsClient.search(params);
            return response.articles().stream()
                .map(GNewsArticleSearchService::toArticle)
                .toList();
        } catch (GNewsClientException | RestClientException ex) {
            throw new ArticleSearchException("Article search provider request failed", ex);
        }
    }

    private static GNewsSearchParams buildGnewsSearchParams(ArticleSearchCriteria criteria) {
        return GNewsSearchParams.builder()
            .q(criteria.query().strip())
            .lang(SOURCE_LANG)
            .searchIn(SEARCH_IN_FIELDS)
            .sortBy(SORT_BY)
            .max(MAX_RESULTS)
            .truncate(TRUNCATE)
            .from(criteria.from())
            .to(criteria.to())
            .build();
    }

    private static Article toArticle(GNews.Article article) {
        var publisher = Publisher.builder()
            .name(article.source().name())
            .url(article.source().url())
            .country(article.source().country())
            .build();
        var metadata = ArticleMetadata.builder()
            .imageUrl(article.image())
            .publishedAt(article.publishedAt())
            .sourceLanguage(article.lang())
            .build();
        return Article.builder()
            .title(article.title())
            .description(article.description())
            .content(article.content())
            .url(article.url())
            .publisher(publisher)
            .metadata(metadata)
            .build();
    }
}
