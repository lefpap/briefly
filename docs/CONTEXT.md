# Briefly

Briefly creates concise news briefs from published news articles.

## Language

**Brief Request**:
A request for an on-demand News Brief, expressed through a Query and an optional Reporting Window.
_Avoid_: Search request, GNews query

**Query**:
Free-form text describing the news the user cares about, including subjects, questions, or more detailed interests. It does not require external news-provider query syntax.
_Avoid_: Topic, prompt, GNews query

**Publication Country**:
The country in which an Article's Publisher is based. It does not identify the location of the reported event.
_Avoid_: Country, event country

**Reporting Window**:
The publication-time interval within which Articles are eligible for a News Brief.
_Avoid_: Date filter, search window

**Source Language**:
The language in which an eligible Article is published.
_Avoid_: Language, input language

**Output Language**:
The language in which a News Brief is written. It is independent of the Source Languages of its Source Articles.
_Avoid_: Language, response language

**Brief Criteria**:
The normalized Query and resolved Reporting Window used to produce a News Brief.
_Avoid_: Search parameters, provider options

**News Brief**:
A concise synthesis of at least two Source Articles. Its citations identify the Source Articles intended to support each highlight, but Briefly does not prove semantic grounding or claim independent fact verification.
_Avoid_: Summary, report

**Article**:
A published news article eligible to inform a News Brief.

**Source Article**:
An Article selected as a source of information for a News Brief and assigned a Citation ID.
_Avoid_: Evidence

**Publisher**:
The organization or publication from which an Article originates.
_Avoid_: Source, provider

**Overview**:
A synthesis of a news brief's highlights. It can contain multiple paragraphs but introduces no facts that are absent from the highlights.
_Avoid_: Summary

**Highlight**:
A focused narrative section about an important development or meaningful disagreement in a news brief. It provides enough context to stand on its own and identifies the Articles intended to support it.
_Avoid_: Key point, bullet

**Citation**:
A relationship from a Highlight to a Source Article intended to support it. It is not proof of factual correctness or semantic grounding.
_Avoid_: Proof, verification
