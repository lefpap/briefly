---
version: 1
slug: "src-main-resources-templates-brief-html"
primary_target: "src/main/resources/templates/brief.html"
related_targets: []
---

# News Brief page

Scope: the rendered News Brief (`brief.html`), returned by `POST /api/briefs/generate/html`. Mode: Read.

Audience and job: the person who ran the Query, or someone they shared the page with, wants the state of a story fast, and wants to see which Source Articles stand behind each Highlight.

Constraints: standalone document, may be opened offline; no app features exist (no saved, history, topics, share, related topics); nav holds only in-page links; Source Article images dropped for publisher letter badges; JetBrains Mono from Google Fonts with system mono fallback (user-chosen). Domain terms from docs/CONTEXT.md.

## Direction contract

THESIS: A user-pinned direction: Briefly's own app world from the reference mockups. A dark masthead tops a cream reading desk where Highlights are numbered lime tiles and every Source Article shows which Highlights it supports. Refuses the soft editorial article with image cards.

OWN-WORLD: Near-black masthead (#141414), warm grey-cream ground, paler cream panels with 1px warm-grey borders and small radii, lime (#d4f25a) as a filled tile behind black numerals and the wordmark period, coloured publisher-initial badges (lime, orange, blue, pink, sky). JetBrains Mono everywhere, bold for headings.

STORY: The reader sees the Query and the title, reads the Overview, then the numbered Highlights, follows a Highlight's source chips to the matching rows in the Sources panel beside them, then reads the original. The separate Source Article detail section was removed at the user's request; the Sources panel is the only source listing.

FIRST VIEWPORT: Desktop: a full-width dark masthead with only the wordmark (the side rail and its nav were removed at the user's request). Below it: a read-only Query bar with the Reporting Window, the title and generated time, then two columns: Overview panel over Highlights panel (left, about 2/3; Overview first at the user's request); Sources panel (right) listing badge, Publisher, date, title, external link icon. Mobile: one column, masthead 52px.

FORM: The reference mockups' brief screen plus the source detail screen, pinned by the user; new-work.md: "a user- or brief-pinned direction beats the roll, always", so concept-seed did not run. seed: none (user-pinned direction)

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance
