# AGENTS.md

## Overview

Briefly creates concise News Briefs by synthesizing Source Articles.

## Working principles

- Make the smallest complete change that satisfies the request.
- If ambiguity would materially change behavior or scope, ask before proceeding; otherwise, choose the smallest reasonable interpretation.
- Follow existing naming, structure, and conventions.
- Keep changes focused; avoid speculative features, abstractions, configuration, and unrelated cleanup.
- Report what changed, what was verified, and any remaining limitations.

## Task-specific guidance

- **Domain and implementation work:** Before exploring or changing the domain model, follow `docs/agents/domain.md`, including its instructions to read `docs/CONTEXT.md` and relevant ADRs.
- **GitHub issue work:** When reading, creating, commenting on, labeling, or closing an issue, follow `docs/agents/issue-tracker.md`.
- **Issue triage:** When classifying or triaging issues, also follow `docs/agents/triage-labels.md`.

## Notes

Tests are deferred until the project reaches a more complete shape. Leave existing tests untouched and do not create or run tests unless the user explicitly requests test work. For completed code changes, compile production code with `./mvnw -DskipTests compile` (`.\mvnw.cmd -DskipTests compile` on Windows).
