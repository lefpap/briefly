package io.github.lefpap.briefly.briefs.internal.web;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
@Schema(
    name = "BriefResponse",
    description = """
        A generated News Brief: a concise synthesis of at least two Source Articles, written in \
        English."""
)
public record BriefResponse(
    @Schema(description = "The Brief Criteria actually used, with the Reporting Window resolved.")
    Criteria criteria,

    @Schema(description = "Short, neutral title for the News Brief.",
        example = "EU moves to tighten AI oversight")
    String title,

    @Schema(description = """
        Narrative synthesis of the Highlights, in up to five paragraphs separated by blank lines. It \
        introduces no facts that are absent from the Highlights.""")
    String overview,

    @ArraySchema(
        arraySchema = @Schema(description = """
            One to five Highlights, ordered by importance and then recency. Each Highlight is a \
            self-contained narrative about one important development or meaningful \
            disagreement."""),
        minItems = 1, maxItems = 5)
    List<Highlight> highlights,

    @ArraySchema(
        arraySchema = @Schema(description = """
            The Source Articles the News Brief was synthesized from, each carrying the Citation ID \
            used by the Highlights."""),
        minItems = 2)
    List<Article> sourceArticles,

    @Schema(description = "When the News Brief was generated, as an ISO-8601 instant.",
        example = "2025-05-02T09:15:30Z")
    Instant generatedAt
) {

    @Builder(toBuilder = true)
    @Schema(
        name = "BriefCriteria",
        description = "The Query and the resolved Reporting Window used to produce the News Brief."
    )
    public record Criteria(
        @Schema(description = "The Query as submitted.",
            example = "European Union artificial intelligence regulation")
        String query,

        @Schema(description = "Resolved start of the Reporting Window.",
            example = "2025-05-01T09:15:30Z")
        Instant from,

        @Schema(description = "Resolved end of the Reporting Window.",
            example = "2025-05-02T09:15:30Z")
        Instant to
    ) {
    }

    @Builder(toBuilder = true)
    @Schema(name = "BriefHighlight", description = """
        A focused narrative about one important development or meaningful disagreement, together \
        with the Citations identifying the Source Articles intended to support it.""")
    public record Highlight(
        @Schema(description = "The Highlight text, written in English.")
        String text,

        @ArraySchema(
            arraySchema = @Schema(description = """
                Citation IDs of the Source Articles intended to support this Highlight. A Citation \
                is an intended support relationship only; Briefly does not prove semantic \
                grounding or independently verify facts."""),
            schema = @Schema(example = "1"),
            minItems = 1)
        List<Integer> citationIds
    ) {
    }

    @Builder(toBuilder = true)
    @Schema(name = "SourceArticle", description = "A published Article used as a source of information.")
    public record Article(
        @Schema(description = "Citation ID referenced by the Highlights.", example = "1")
        Integer citationId,

        @Schema(description = "Headline of the Article as published.")
        String title,

        @Schema(description = "Short description of the Article as published.")
        String description,

        @Schema(description = "Canonical URL of the Article.",
            example = "https://example.com/news/eu-ai-oversight")
        URI url,

        @Schema(description = "The Publisher the Article originates from.")
        Publisher publisher,

        @Schema(description = "Publication metadata for the Article.")
        ArticleMetadata metadata
    ) {
    }

    @Builder(toBuilder = true)
    @Schema(name = "Publisher", description = "The organization or publication an Article originates from.")
    public record Publisher(
        @Schema(description = "Name of the Publisher.", example = "Example Times")
        String name,

        @Schema(description = "Home page of the Publisher.", example = "https://example.com")
        URI url,

        @Schema(description = """
            Publication Country: the country the Publisher is based in. It does not identify where
            the reported event took place. May be null when the provider does not report it.""",
            example = "be")
        String country
    ) {
    }

    @Builder(toBuilder = true)
    @Schema(name = "ArticleMetadata", description = "Publication metadata for a Source Article.")
    public record ArticleMetadata(
        @Schema(description = "Lead image of the Article, when the provider reports one.",
            example = "https://example.com/images/eu-ai-oversight.jpg")
        URI imageUrl,

        @Schema(description = "When the Article was published, as an ISO-8601 instant.",
            example = "2025-05-01T18:42:00Z")
        Instant publishedAt,

        @Schema(description = """
            Source Language of the Article. Article search is restricted to English, so this is
            `en`. The News Brief is written in English independently of this value.""",
            example = "en")
        String sourceLanguage
    ) {
    }
}
