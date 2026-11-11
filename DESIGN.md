---
name: briefly.
description: A dark masthead over a cream reading desk, where every Highlight is a numbered lime tile and every Source Article shows which Highlights cite it.
colors:
  ground: "#ecebe4"
  panel: "#f6f5f0"
  ink: "#141414"
  muted: "#5c5a53"
  line: "#d3d0c6"
  line-soft: "#e2dfd6"
  masthead: "#141414"
  lime: "#d4f25a"
  lime-wash: "#eef8c6"
  tone-0: "#d4f25a"
  tone-1: "#f6a05c"
  tone-2: "#7ea9f2"
  tone-3: "#f5b4c3"
  tone-4: "#b6d4f7"
typography:
  display:
    fontFamily: "JetBrains Mono, ui-monospace, Cascadia Mono, SF Mono, Consolas, Menlo, monospace"
    fontSize: "clamp(26px, 3.2vw, 38px)"
    fontWeight: 800
    lineHeight: 1.2
    letterSpacing: "-0.035em"
  wordmark:
    fontFamily: "JetBrains Mono, ui-monospace, Cascadia Mono, SF Mono, Consolas, Menlo, monospace"
    fontSize: "22px"
    fontWeight: 700
    letterSpacing: "-0.03em"
  title:
    fontFamily: "JetBrains Mono, ui-monospace, Cascadia Mono, SF Mono, Consolas, Menlo, monospace"
    fontSize: "18px"
    fontWeight: 700
    lineHeight: 1.35
    letterSpacing: "-0.02em"
  body:
    fontFamily: "JetBrains Mono, ui-monospace, Cascadia Mono, SF Mono, Consolas, Menlo, monospace"
    fontSize: "14.5px"
    fontWeight: 400
    lineHeight: 1.7
    fontFeature: "liga 0, calt 0"
  label:
    fontFamily: "JetBrains Mono, ui-monospace, Cascadia Mono, SF Mono, Consolas, Menlo, monospace"
    fontSize: "12px"
    fontWeight: 500
  numeral:
    fontFamily: "JetBrains Mono, ui-monospace, Cascadia Mono, SF Mono, Consolas, Menlo, monospace"
    fontSize: "13px"
    fontWeight: 700
    lineHeight: 1
rounded:
  xs: "3px"
  sm: "4px"
  md: "6px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "12px"
  lg: "16px"
  gutter: "20px"
  panel: "22px"
  section: "28px"
  xl: "36px"
  xxl: "48px"
components:
  masthead:
    backgroundColor: "{colors.masthead}"
    textColor: "{colors.panel}"
    typography: "{typography.wordmark}"
    height: "60px"
  query-bar:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.ink}"
    rounded: "{rounded.md}"
    padding: "12px 18px"
    height: "52px"
  panel:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.ink}"
    rounded: "{rounded.md}"
    padding: "16px 22px 22px"
  highlight-tile:
    backgroundColor: "{colors.lime}"
    textColor: "{colors.ink}"
    typography: "{typography.numeral}"
    rounded: "{rounded.sm}"
    size: "28px"
  highlight-target:
    backgroundColor: "{colors.lime-wash}"
    rounded: "{rounded.md}"
    padding: "14px 12px"
  publisher-badge:
    backgroundColor: "{colors.tone-0}"
    textColor: "{colors.ink}"
    rounded: "{rounded.sm}"
    size: "34px"
  publisher-badge-small:
    rounded: "{rounded.xs}"
    size: "20px"
  publisher-badge-none:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.ink}"
  citation-chip:
    backgroundColor: "#fbfaf7"
    textColor: "{colors.ink}"
    padding: "3px 9px 3px 3px"
---

# Design System: briefly.

## Overview

**Creative North Star: "The Reading Desk"**

Briefly is a working desk, not a magazine. A near-black masthead carries only the wordmark; below it lies a warm grey-cream desk with paler cream panels laid on it, each edged by a hairline warm-grey border and small radius. Everything is set in one monospace face, so hierarchy comes from weight and size alone, and the page reads like a well-kept research log: dense, orderly, scannable.

One colour does the pointing. Lime fills the numbered tiles beside each Highlight, dots the wordmark, and lights whatever the reader has just jumped to. Publishers are identified by coloured initial badges in five fixed tones, so the same Source Article wears the same colour in the Citation chips and the Sources list. The world was pinned by the user from reference mockups; it refuses the soft editorial article with image cards, and Source Article images are replaced by publisher badges.

Depth is almost absent. Panels sit flat on the desk; the only lift in the system is the ring and soft shadow that appear on a linked target, so the eye lands where the link went.

**Key Characteristics:**
- Dark masthead over a cream desk; no navigation, because the page is one document.
- JetBrains Mono for every role, ligatures off, bold to extra-bold for headings with tight negative tracking.
- Lime as the single accent, carried mostly by filled tiles behind black numerals.
- Five-tone publisher-initial badges keyed to the Citation number.
- Flat panels with 1px warm-grey borders and 4-6px corners; depth only on target state.

## Colors

A warm, low-chroma paper palette under a near-black masthead, with one acid lime accent and five pastel publisher tones.

### Primary
- **Signal Lime** (lime): fills the Highlight number tiles, colours the wordmark's period, and is the text-selection colour.
- **Lime Wash** (lime-wash): the pale tint behind a targeted Highlight or Sources row, so cross-references read as one family.

### Secondary
- **Publisher Tones** (tone-0 lime, tone-1 apricot, tone-2 cornflower, tone-3 blush, tone-4 sky): background fills for publisher-initial badges only, always with ink text and a near-ink 1px border.

### Neutral
- **Desk Cream** (ground): the page ground behind panels; also the scrollbar track.
- **Panel Paper** (panel): every panel, the Query bar, the wordmark text on the masthead, and the no-Publisher badge.
- **Press Black** (ink): body text, headings, the Query bar's 1.5px border, focus outlines, and the targeted card's border.
- **Pencil Grey** (muted): dates, counts, labels, the footer, secondary metadata.
- **Hairline** (line): panel borders, the title block's rule, the footer rule, Citation chip borders.
- **Soft Hairline** (line-soft): dividers between Highlights and between Sources list rows; icon-link hover fill.
- **Masthead Black** (masthead): the masthead's ground.

### Named Rules
**The Numbered Lime Rule.** A filled lime tile with a black numeral means a Highlight number and nothing else. Lime may also mark the wordmark period, selection, and target rings, but never another numbered or counted thing.

**The Publisher Tone Rule.** A Publisher badge takes tone `(citationId - 1) % 5`, so one Source Article keeps one colour everywhere on the page. A Source Article with no Publisher gets the neutral panel-paper badge showing its Citation number instead of an initial. The tone-0 badge shares the lime hue but always carries a letter and an ink border, which keeps it apart from a Highlight tile.

## Typography

**Display Font:** JetBrains Mono (with ui-monospace, Cascadia Mono, SF Mono, Consolas, Menlo, monospace)
**Body Font:** JetBrains Mono (same stack)

**Character:** One monospace face, loaded from Google Fonts at 400/500/700/800 with a system monospace fallback by user choice. Weight and size carry all hierarchy; headings tighten their tracking as they grow.

### Hierarchy
- **Display** (800, clamp 26-38px, 1.2, -0.035em): the News Brief title only, capped at 30ch and balanced.
- **Wordmark** (700, 22px, -0.03em; 20px on mobile): "briefly." in the masthead, its period in lime.
- **Title** (700, 18px, 1.35, -0.02em): panel headings (Highlights, Overview, Sources).
- **Body** (400, 14.5px, 1.7): Highlight text and the Overview, capped at 72ch, split into paragraphs at blank lines; single line breaks reflow to the column.
- **Label** (500-700, 11-12px): dates, counts, Citation chips, Reporting Window values, Sources list titles (12px, 1.5, clamped to two lines), the footer.
- **Numeral** (700, 13px, line-height 1): numbers inside Highlight tiles.

### Named Rules
**The One Face Rule.** JetBrains Mono is the only typeface, with ligatures turned off. New roles are made from weight, size, and tracking, never a second family.

## Layout

A full-width dark masthead (60px tall, 52px on mobile) whose wordmark aligns with the content column, then a desk (padding 28px 40px 56px) whose content is capped at 1180px and centred. The Query bar is capped at 980px. Below the title block, a two-column grid places the Overview over the Highlights on the left and a 340px sticky Sources panel on the right (20px gap); the Sources panel scrolls internally within the viewport height.

At 1080px and below the brief grid becomes one column and the Sources panel stops sticking. At 760px and below the masthead shrinks to 52px with a 20px wordmark, the desk padding drops to 20px 16px 40px, and panel padding tightens to 16px.

Spacing steps observed in use: 4, 8, 12, 16, 20, 22, 28, 36, 48px. 20px is the gutter between panels; 22px is panel inset; 28px and 48px separate major blocks. Linked targets (Highlights, Sources rows, the Sources panel) carry a 24px scroll margin, and in-page navigation scrolls smoothly unless reduced motion is requested.

## Elevation & Depth

The system is flat. Panels are separated from the desk by tone and a 1px hairline, not by shadow. Depth appears only as a response to navigation: a targeted Highlight or Sources row gets a lime-wash fill with a 2px ink ring around its tile or badge.

### Shadow Vocabulary
- **Tile ring** (`box-shadow: 0 0 0 2px #141414`): the number tile of a targeted Highlight, or the badge of a targeted Sources row.

### Named Rules
**The Flat Desk Rule.** Nothing is lifted at rest. A shadow means "you arrived here" and appears only on `:target`.

## Shapes

Small, consistent corners: 6px on panels, the Query bar, and the target fills; 4px on Highlight tiles, standard badges, and icon buttons; 3px on small badges and focus outlines. Borders are 1px hairlines everywhere except the Query bar, whose 1.5px ink border marks it as the page's input record. Square tiles and badges are the recurring silhouette: numbers and initials always sit centred in a square.

## Components

### Masthead
A full-width near-black bar, 60px tall (52px on mobile), holding only the "briefly." wordmark in panel paper with a lime period. Its inner row is capped at 1260px with 40px side padding (16px on mobile), so the wordmark lines up with the content column. It is not sticky and holds no links.

### Query Bar
A read-only record of the Brief Criteria: panel paper, 1.5px ink border, 6px corners, min-height 52px, 12px 18px padding. A 20px search icon leads the Query in 15px/500; the Reporting Window follows as bold "From"/"To" terms with muted 12px values, wrapping below on narrow widths.

### Panels
Panel paper, 1px hairline border, 6px corners, no shadow. The head row (18px 22px 0) holds a Title heading and an optional 20px stroke icon at the right; the body is inset 16px 22px 22px.

### Highlight Tile (signature)
A 28px lime square with 4px corners and a centred black numeral (700, 13px). It sits in a 28px column beside the Highlight text with a 16px gap; Highlights are divided by soft hairlines.

### Publisher Badge (signature)
A square holding the Publisher's uppercase initial (800 weight) on a tone fill, with a 1px near-ink border. Two sizes: small 20px/11px (inside Citation chips) and standard 34px/16px (Sources list).

### Citation Chip
A small link to a Source Article's row in the Sources panel: near-white paper, 1px hairline border, 5px corners, 3px 9px 3px 3px padding, a small badge followed by the Publisher name (11.5px/500, ellipsised). Hover darkens the border to ink and lightens the fill to white. Chips follow a muted "Sources" label.

### Sources List Row
A three-column row (34px badge, text, 28px icon link) with 12px padding and soft hairline dividers: bold Publisher and muted date on one line, a two-line clamped title linking to the original article, and an external-link icon button (4px corners, soft hairline fill on hover) that repeats the title link for pointer users and is hidden from assistive tech. A row reached by a Citation chip gets the lime-wash target fill and a 2px ink ring on its badge.

## Do's and Don'ts

### Do:
- **Do** reserve the filled lime tile with a black numeral for Highlight numbers.
- **Do** colour Publisher badges by `(citationId - 1) % 5`, and give a Source Article without a Publisher the neutral panel-paper badge showing its Citation number.
- **Do** set every role in JetBrains Mono with ligatures off, building hierarchy from weight (400-800), size, and negative tracking on headings.
- **Do** keep panels flat on the desk cream with a 1px hairline (#d3d0c6) and 6px corners.
- **Do** mark linked targets with lime: a lime-wash fill for a targeted Highlight or Sources row.
- **Do** keep transitions to colour, border, and shadow at 0.15s ease-out, and drop them under reduced motion.

### Don't:
- **Don't** use image cards or Source Article photos; Publisher initial badges stand in for them.
- **Don't** introduce a second typeface for display or body.
- **Don't** put a lime tile behind counts, dates, or any number that is not a Highlight number.
- **Don't** add shadows to panels at rest.
- **Don't** add navigation to the page; it is one standalone document, and Briefly has no other screens.
