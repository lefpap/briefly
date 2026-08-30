package io.github.lefpap.briefly.lib.gnews;

import io.github.lefpap.briefly.briefs.Article;
import io.github.lefpap.briefly.briefs.ArticleMetadata;
import io.github.lefpap.briefly.briefs.ArticleSearchService;
import io.github.lefpap.briefly.briefs.BriefQueryCriteria;
import io.github.lefpap.briefly.briefs.Source;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GNewsArticleSearchService implements ArticleSearchService {

    private static final String SEARCH_IN_FIELDS = "title,description,content";
    private static final String SORT_BY = "relevance";
    private static final int MAX_RESULTS = 10;
    private static final String TRUNCATE = "content";

    private final GNewsClient newsClient;

    public GNewsArticleSearchService(GNewsClient newsClient) {
        this.newsClient = newsClient;
    }

    @Override
    public List<Article> search(BriefQueryCriteria criteria) {
        var params = buildGnewsSearchParams(criteria);
        var response = newsClient.search(params);
        return response.articles().stream()
                .map(GNewsArticleSearchService::toArticle)
                .toList();
    }

    private static GNewsSearchParams buildGnewsSearchParams(BriefQueryCriteria criteria) {
        return GNewsSearchParams.builder()
                .q(criteria.topic().strip())
                .lang(criteria.sourceLang())
                .country(criteria.publicationCountry())
                .searchIn(SEARCH_IN_FIELDS)
                .sortBy(SORT_BY)
                .max(MAX_RESULTS)
                .truncate(TRUNCATE)
                .from(criteria.publishedFrom())
                .to(criteria.publishedTo())
                .build();
    }

    private static Article toArticle(GNews.Article article) {
        var source = new Source(
                article.source().name(),
                article.source().url(),
                article.source().country()
        );
        var metadata = new ArticleMetadata(
                article.url(),
                article.image(),
                article.publishedAt(),
                article.lang()
        );
        return new Article(
                article.title(),
                article.description(),
                article.content(),
                source,
                metadata
        );
    }
}
