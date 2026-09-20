package io.github.lefpap.briefly.news.internal.gnews;

import io.github.lefpap.briefly.news.api.Article;
import io.github.lefpap.briefly.news.api.ArticleMetadata;
import io.github.lefpap.briefly.news.api.ArticleSearchCriteria;
import io.github.lefpap.briefly.news.api.ArticleSearchException;
import io.github.lefpap.briefly.news.api.ArticleSearchService;
import io.github.lefpap.briefly.news.api.Publisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Reports what was searched and how many Articles it yielded, so a quick search is told apart from
 * one that found nothing and an empty result can be acted on.
 *
 * <p>The whole of the criteria is logged: the Reporting Window because a request that omits it has
 * it resolved server-side, and the Query because it is the rest of what makes a search reproducible.
 * Both are already echoed back to the caller in the response criteria.
 */
@Slf4j
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
            List<Article> articles = response.articles().stream()
                .map(GNewsArticleSearchService::toArticle)
                .toList();

            log.info(
                "Article search completed criteria={} articles={}",
                criteria,
                articles.size()
            );
            return articles;
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
