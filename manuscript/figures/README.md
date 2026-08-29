# Figures

Committed, hand-authored SVG only: `fig-NN-x-slug.svg`, where `NN` is the chapter and `x` the letter the chapter uses (Figure A, Figure B).
Chapters reference a figure with Markdown image syntax whose path is relative to the repo root (`![…](manuscript/figures/fig-35-a-two-planes.svg)`); `make book` concatenates from the root, so pandoc resolves it unchanged.
No external fonts, no scripts, no raster; every label is a `<text>` element so the figure is searchable and diffable; width ≤ 900 px; self-contained light background so the figure reads on light and dark pages.
Figures carry only abstract labels — kinds, boxes, planes — never a project's names, paths, or ceremonies.
The composition glyph `⨟` (U+2A1F) in a figure relies on the reader's font fallback, exactly as it does in the chapter prose; the figures list math fonts that carry it.
