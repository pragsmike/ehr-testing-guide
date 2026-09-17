# Figures

Committed, hand-authored SVG only: `fig-NN-x-slug.svg`, where `NN` is the chapter and `x` the letter the chapter uses (Figure A, Figure B).
Chapters reference a figure with Markdown image syntax whose path is relative to the chapter file (`![…](../figures/fig-35-a-two-planes.svg)`), so it resolves wherever the chapter is read in place — GitHub, an editor preview. `make book` concatenates chapters into `build/book.md`, which loses the chapter's directory, so the Makefile passes a second `--resource-path=manuscript/figures`: pandoc resolves `../figures/x.svg` against that entry, landing back in this directory. `make lint-book` fails on any chapter image that does not resolve relative to its chapter.
No external fonts, no scripts, no raster; every label is a `<text>` element so the figure is searchable and diffable; width ≤ 900 px; self-contained light background so the figure reads on light and dark pages.
Figures carry only abstract labels — kinds, boxes, planes — never a project's names, paths, or ceremonies.
The composition glyph `⨟` (U+2A1F) in a figure relies on the reader's font fallback, exactly as it does in the chapter prose; the figures list math fonts that carry it.
