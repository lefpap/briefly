# Domain Docs

How engineering agents should consume this repository's domain documentation when exploring the codebase.

## Before exploring, read these

- **`docs/CONTEXT.md`** — the domain glossary and shared context.
- **`docs/adr/`** — ADRs that affect the area you're about to work in.

If these files don't exist, proceed silently. The domain documentation can be created lazily when terminology or architectural decisions are resolved.

## Use the glossary's vocabulary

When output names a domain concept—in an issue title, refactor proposal, hypothesis, or test name—use the term defined in `docs/CONTEXT.md`. Don't drift to synonyms the glossary explicitly avoids.

If the concept isn't in the glossary, either reconsider whether it belongs to the project vocabulary or note the gap for domain modeling.

## Flag ADR conflicts

If output contradicts an existing ADR, surface it explicitly rather than silently overriding it:

> _Contradicts ADR-0001 (OpenAI-compatible Chat Completions)—but worth reopening because…_
