# Editorial Review Rubric — EHR Testing Guide

<!-- FROZEN. This rubric is the fixed measuring stick for the
     editorial-review skill; do not regenerate, reweight, or add
     dimensions during a review. Scores are only comparable across
     reviews because the instrument does not move. Changes to this
     file are project decisions: propose them in a plan or ADR, land
     them in their own commit, and note the version bump in the
     scores log so pre- and post-change scores are not naively
     compared. Version: 1.0 (2026-07-22). -->

## Scoring conventions

- Each dimension scores **1–5** (5 = excellent). Half points allowed.
- Two scores are recorded per review:
  - **Current**: the repository as it stands, stubs and all. This is
    the fitness-for-purpose-today number.
  - **Drafted-only**: considering only completed material (chapters
    at `review`/`stable`, implemented companion code). This is the
    quality ceiling the finished book is tracking toward.
  - Dimension 8 (completeness) has no drafted-only score by
    definition — mark it "—".
- **Weighted score** = Σ(weight × score) / Σ(weights), reported to
  one decimal place, computed separately for Current and
  Drafted-only (dimension 8 excluded from the drafted-only
  denominator).
- Every score below 4 must cite at least one piece of concrete
  evidence (file plus grep-able phrase — not line numbers, which
  drift). Scores of 4+ should cite evidence where it is not obvious.

## Dimensions

| # | Dimension | Weight | What it measures |
|---|---|---|---|
| 1 | Intellectual coherence / thesis strength | 5 | Is the central argument (observational correctness, purpose sets, lattice, residue, oracle circularity) sharp, original, and carried consistently from design-rationale through ADRs, claims register, chapters, and code? Contradictions in the spine are scored here. |
| 2 | Fitness for stated purpose | 5 | Can the two stated uses succeed today: (a) learn the method from the drafted material; (b) clone `companion/` and adapt it to a real transformation? Missing load-bearing pieces (e.g. unimplemented halves of the specimen) score here. |
| 3 | Dual-audience execution | 4 | Is the introduce-twice rule actually applied — every categorical term and every healthcare term introduced operationally before it is named, in reading order? Does the glossary genuinely serve both directions? |
| 4 | Manuscript ↔ code consistency | 5 | Does every listing, identifier, tolerance, and claim in the manuscript match what `companion/` actually contains (ADR-0007)? Elided listings must not contradict the real code. This dimension is where drift between §94, chapter prose, and the property tables is scored. |
| 5 | Code quality & test integrity | 4 | Is the companion clean, honestly scoped, and tested? Do tests test what they claim (golden oracles independent of production rules, mutations that break their tagged targets)? Stale docstrings score here. |
| 6 | Reference accuracy & currency | 4 | Part IV entry schema conformance and `Entry last verified` dates; bibliography entries verified vs TODO; factual accuracy of domain claims (LOINC codes, HL7 field semantics, tool maintenance statuses). |
| 7 | Internal consistency / freedom from drift | 4 | Status tables vs actual file states; names consistent across filename/H1/tables; claims-register statuses vs where claims actually landed; agent-surface files (AGENTS.md, CLAUDE.md) agreeing with each other and with reality; sync-comment debts either paid or tracked. |
| 8 | Completeness / coverage | 4 | Fraction and importance of chapters drafted; frontmatter; companion obligations implemented; claims still transcript-only. Current-state only. |
| 9 | Navigability & consultability | 3 | Can the consult-mode reader find things: file-order-as-TOC integrity, failure-mode index usefulness, worksheet usability, cross-references and anchors that resolve. |
| 10 | Process & maintainability scaffolding | 3 | ADRs, handoffs, claims register, anti-drift checklist, build/lint gates: do the mechanisms exist that would catch the problems dimensions 4–7 look for, and are they being used? |

## Score anchors (apply per dimension)

- **5** — No findings above nit severity; the dimension exemplifies
  its own standard.
- **4** — Minor findings only; nothing misleads a reader or user.
- **3** — At least one error or several inconsistencies a careful
  reader would notice; fitness not blocked.
- **2** — Errors or gaps that would mislead the target audience or
  break a stated use.
- **1** — The dimension's promise is not kept.

## Reporting

Record each review as one row appended to
`.agents/reviews/scores-log.md` (date, pack/commit identity, both
weighted scores, one-line delta note), plus the full per-dimension
table inside the dated review report in `.agents/reviews/`. Never
edit prior rows.
