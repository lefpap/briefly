# Briefly

Briefly creates concise news briefs from published news articles.

## Language

**Brief Request**:
A request for an on-demand News Brief about a topic. It expresses what reporting the user wants understood, not how an external news provider should perform its search.
_Avoid_: Search request, GNews query

**Topic**:
The plain-text subject or question that a Brief Request asks Briefly to explain. It is not interpreted as external news-provider query syntax.
_Avoid_: Query, prompt

**Publication Country**:
The country in which a Source Article's publisher is based. It does not identify the location of the reported event.
_Avoid_: Country, event country

**Reporting Window**:
The publication-time interval within which Source Articles are eligible for a News Brief.
_Avoid_: Date filter, search window

**Source Language**:
The language in which an eligible Source Article is published.
_Avoid_: Language, input language

**Output Language**:
The language in which a News Brief is written. It is independent of the Source Languages used as evidence.
_Avoid_: Language, response language

**Brief Criteria**:
The normalized Topic, Publication Country, Source Language, Output Language, and Reporting Window used to produce a News Brief.
_Avoid_: Search parameters, provider options

**News Brief**:
A concise synthesis of at least two non-duplicate source articles from different publishers. Its citations identify the source articles intended to support each highlight, but Briefly does not prove semantic grounding or claim independent fact verification.
_Avoid_: Summary, report

**Source Article**:
A published news article used as evidence for a news brief.
_Avoid_: Source

**Overview**:
A synthesis of a news brief's highlights. It can contain multiple paragraphs but introduces no facts that are absent from the highlights.
_Avoid_: Summary

**Highlight**:
An important development or meaningful disagreement in a news brief. A highlight identifies the source articles that support it.
_Avoid_: Key point, bullet

**Citation**:
A relationship from a Highlight to a Source Article intended to support it. It is not proof of factual correctness or semantic grounding.
_Avoid_: Proof, verification
