package io.github.lefpap.briefly.briefs.internal.domain.util;

import io.github.lefpap.briefly.briefs.internal.domain.exception.BriefGenerationException;
import io.github.lefpap.briefly.briefs.internal.domain.model.GeneratedBrief;
import io.github.lefpap.briefly.briefs.internal.domain.model.SourceArticle;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class GeneratedBriefValidator {

    private static final int MINIMUM_CITED_ARTICLE_COUNT = 2;

    private final Validator validator;

    public GeneratedBriefValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(GeneratedBrief generatedBrief, List<SourceArticle> sourceArticles) {
        if (generatedBrief == null) {
            throw new BriefGenerationException("AI provider returned no generated News Brief");
        }

        validateStructure(generatedBrief);
        validateCitations(generatedBrief, sourceArticles);
    }

    private void validateStructure(GeneratedBrief generatedBrief) {
        Set<ConstraintViolation<GeneratedBrief>> violations = validator.validate(generatedBrief);
        if (violations.isEmpty()) {
            return;
        }

        String details = violations.stream()
            .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
            .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
            .collect(Collectors.joining("; "));
        throw new BriefGenerationException(
            "Generated News Brief violates output constraints: " + details
        );
    }

    private static void validateCitations(GeneratedBrief generatedBrief, List<SourceArticle> sourceArticles) {
        Set<Integer> citationIds = generatedBrief.highlights().stream()
            .map(GeneratedBrief.GeneratedHighlight::citationIds)
            .flatMap(List::stream)
            .collect(Collectors.toSet());
        Set<Integer> availableCitationIds = sourceArticles.stream()
            .map(SourceArticle::citationId)
            .collect(Collectors.toSet());

        Set<Integer> unknownCitationIds = new TreeSet<>(citationIds);
        unknownCitationIds.removeAll(availableCitationIds);
        if (!unknownCitationIds.isEmpty()) {
            throw new BriefGenerationException(
                "Generated News Brief contains unknown Citation IDs: " + unknownCitationIds
            );
        }

        if (citationIds.size() < MINIMUM_CITED_ARTICLE_COUNT) {
            throw new BriefGenerationException(
                "Generated News Brief must cite at least %d Source Articles but cited %d"
                    .formatted(MINIMUM_CITED_ARTICLE_COUNT, citationIds.size())
            );
        }
    }
}
