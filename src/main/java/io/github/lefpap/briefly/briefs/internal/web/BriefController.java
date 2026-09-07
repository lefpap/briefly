package io.github.lefpap.briefly.briefs.internal.web;

import io.github.lefpap.briefly.briefs.internal.domain.model.Brief;
import io.github.lefpap.briefly.briefs.internal.domain.service.BriefService;
import io.github.lefpap.briefly.news.api.model.ArticleSearchCriteria;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/briefs")
public class BriefController {

    private final BriefService briefService;

    public BriefController(BriefService briefService) {
        this.briefService = briefService;
    }

    @PostMapping("/generate")
    public BriefResponse generateBrief(@Valid @RequestBody BriefRequest request) {
        ArticleSearchCriteria criteria = BriefMapper.toArticleSearchCriteria(request);
        Brief brief = briefService.createBrief(criteria);
        return BriefMapper.toBriefResponse(brief);
    }
}
