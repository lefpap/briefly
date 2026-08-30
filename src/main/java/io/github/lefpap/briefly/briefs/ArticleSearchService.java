package io.github.lefpap.briefly.briefs;

import java.util.List;

public interface ArticleSearchService {

    List<Article> search(BriefQueryCriteria criteria);
}
