package io.github.lefpap.briefly.news.api;

import java.util.List;

public interface ArticleSearchService {

    List<Article> search(ArticleSearchCriteria criteria);
}
