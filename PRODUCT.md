# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

The person running Briefly locally, who sends a Query and reads the resulting News Brief, and the people they share the rendered News Brief page with, who don't run Briefly themselves.

## Product Purpose

Briefly turns recent news into a concise News Brief. Given a Query and an optional Reporting Window, it searches GNews for matching English Articles, and an AI model writes a title, an Overview, and one to five Highlights. Each Highlight cites the Source Articles intended to support it. Success means a reader understands the state of a story quickly and can trace each Highlight back to its Source Articles.

## Positioning

Every Highlight carries Citations to specific Source Articles, so the reader can see which Publisher reporting stands behind each development, not just a summary.

## Operating Context

- Briefly is a local API (Spring Boot). The News Brief page is a standalone HTML document returned by `POST /api/briefs/generate/html`. It may be saved and opened offline or sent to others.
- The page has no app around it: there is no home, saved briefs, history, topics, settings, sharing, or related topics.

## Capabilities and Constraints

- Domain vocabulary is defined in `docs/CONTEXT.md` (News Brief, Query, Reporting Window, Overview, Highlight, Source Article, Publisher, Citation). Use those terms in UI copy.
- A News Brief has a title, an Overview of up to five paragraphs, one to five Highlights, at least two Source Articles, Brief Criteria (Query, Reporting Window), and a generation time.
- Source Articles carry title, description, URL, Publisher (name, URL, country), publication time and an optional image URL. Any field except the title may be missing.
- Articles and News Briefs are English only for now.

## Brand Commitments

- Name: Briefly, written as the wordmark "briefly." with the period.

## Evidence on Hand

- No testimonials, customers, or usage figures exist. Don't invent any.

## Product Principles

- Citations identify the Source Articles behind each Highlight. They aren't proof of factual correctness, and the product never claims independent fact verification.
- Show only what works. Don't add UI for features Briefly doesn't have.
- Brevity is the product. The page gets the reader to the point, then to the sources.
