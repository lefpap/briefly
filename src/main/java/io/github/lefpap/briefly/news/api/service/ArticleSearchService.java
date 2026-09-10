package io.github.lefpap.briefly.news.api.service;

import io.github.lefpap.briefly.news.api.model.Article;
import io.github.lefpap.briefly.news.api.model.ArticleSearchCriteria;

import java.util.List;

public interface ArticleSearchService {

    List<Article> search(ArticleSearchCriteria criteria);
}
