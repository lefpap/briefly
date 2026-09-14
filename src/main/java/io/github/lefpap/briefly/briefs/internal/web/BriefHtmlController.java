package io.github.lefpap.briefly.briefs.internal.web;

import io.github.lefpap.briefly.briefs.internal.domain.model.Brief;
import io.github.lefpap.briefly.briefs.internal.domain.service.BriefService;
import io.github.lefpap.briefly.news.api.model.ArticleSearchCriteria;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/briefs")
public class BriefHtmlController {

    private final BriefService briefService;

    public BriefHtmlController(BriefService briefService) {
        this.briefService = briefService;
    }

    @PostMapping(value = "/generate/html", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.TEXT_HTML_VALUE)
    public String generateBriefHtml(@Valid @RequestBody BriefRequest request, Model model) {
        ArticleSearchCriteria criteria = BriefMapper.toArticleSearchCriteria(request);
        Brief brief = briefService.createBrief(criteria);
        model.addAttribute("brief", brief);
        return "brief";
    }
}
