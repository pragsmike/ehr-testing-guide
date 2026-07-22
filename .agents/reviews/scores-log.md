# Editorial Review — 2026-07-22 (follow-up to baseline)

<!-- Produced by .agents/skills/editorial-review, static mode, from
     a chat session working against an uploaded pack. Second review;
     the baseline (same date, earlier) predates the skill and exists
     only as the first row of scores-log.md plus the remediation
     plan it produced (now archived). -->

## Input identity and mode

- **Mode:** static (pack only; no build/test execution — all code
  findings are static-analysis findings).
- **Input:** pack uploaded 2026-07-22, after the archive commit.
  Freshness evidence: contains the archived plan at
  `.agents/plans/archive/2026-07-22-editorial-remediation.md`, the
  AGENTS.md archive-directory rule, and the post-hand-edit state of
  the editorial-review SKILL.md — all from the most recent work.
  82 files.
- **Prior review state:** baseline row in
  `.agents/reviews/scores-log.md` (Current 3.6 / Drafted-only 4.3,
  rubric 1.0). No prior report file exists — the baseline predates
  this skill; its findings are reconstructed from the archived
  remediation plan, which enumerated them as items A1–D3.

## Disposition of prior findings

All baseline findings are **fixed**, verified in this pack:

- value-vs-threshold tolerance contradiction (A1): §94 row and ch 22
  listing now read same-classification; pinning assertion present in
  `properties_test.clj`.
- §94 tolerance vocabulary and date-row (A2, A3): applied; "Set-equal"
  absent from §94.
- Chapter-12 naming (B1): status table matches the H1.
- Backmatter status truth (B2): 91/92/93/95 drafting, 94 review,
  96 stable — all match file reality.
- Agent-surface pointers (B3): AGENTS.md lists the seven skills that
  actually exist in `.agents/skills/`; CLAUDE.md names
  `.agents/skills/` as canonical; the `.agents/` record directories
  are documented.
- smoke_test docstring (B4): "only `observation->oru` remains
  stubbed".
- Term debt (C1): Naturality glossary entry present and correctly
  slotted; ST, ORM, CQRS acronym rows at their placements.
- Bibliography (D1): every cited entry TODO-free with verified
  fields; STU2 URL pinned and page-confirmed.
- Built-output lint (D2 + D4): lint-book wired into check, including
  the cited-entry TODO check at the raw-.bib layer.
- Pack dotfile coverage (D3): this pack itself is the proof —
  `.agents/`, `.githooks/pre-commit`, and `.gitattributes` are all
  present and the hook matches every documented claim about it.

## Findings

**F1 — stale (manuscript prose).** Chapter 21, "The specimen,
worked": the sentence "The shape — currently stubbed in
`ehr-testing.properties` and promoted to a real test as this
chapter lands — is:" is false — the property landed with ch 23's
companion work and is live and tested. (The similar wording in the
chapter's HTML sync comment is excluded: comments are
leave-after-payment by documented convention; the prose sentence is
not covered by that convention.) Evidence: grep "currently stubbed
in `ehr-testing.properties`" in
manuscript/20-part-2-the-method/21-what-correct-means.md, outside
the comment block. New-to-report: this existed at baseline but the
baseline review failed to surface it — a miss in that review, not a
regression in the repo.

**F2 — inconsistency (manuscript ↔ companion, ADR-0007).** The same
chapter-21 listing binds `[oru gen-oru]`, but the companion defines
only `gen-parsed-oru` (grep "def gen-" in
companion/src/ehr_testing/properties.clj). A reader transcribing
the listing hits an unresolved symbol. Same locus and same fix
window as F1. Also new-to-report, same caveat.

**F3 — nit.** `.agents/skills/editorial-review/SKILL.md` step 2:
the recent hand-edit restored the space before "(skip" but not the
one after it — the line reads "(skip`archive/`" where it should
read "(skip `archive/`". Evidence: grep "(skip\`" in that file.
Origin documented: prompt-formatting defect in the archive session
(nested backticks in an inline replacement spec), half-corrected by
hand.

**Excluded as documented-deliberate** (with the documenting
source): ch 22's HTML comment saying OBR-4 is "deferred to ch 23"
(handoff-2026-07-22, open threads); the four uncited bibliography
TODO entries (archived plan, D4); the baseline scores-log row's
pre-archive plan path (archive-session prompt notes; the log is
append-only); `.agents/handoffs/` named but not yet populated
(AGENTS.md documents it as populated by the handoff skill, whose
own SKILL.md confirms the target path — and whose archive
convention matches the new AGENTS.md rule); all chapter HTML sync
comments (leave-after-payment convention).

**Light pass, other skills (check family h):** the six
non-editorial skills in `.agents/skills/` were scanned for
contradictions with repo rules only, not content-reviewed.
`wsl-windows-git-hygiene` complements ADR-0012 (troubleshooting for
the failure mode the rule prevents) and does not contradict it;
`handoff` targets `.agents/handoffs/` with its own `archive/`
subdirectory, consistent with the AGENTS.md rule. No contradictions
found; a content-level review of these skills has still never been
performed and remains open if the author wants one.

**Static-mode limits:** `make check`, `make book`, and the lint
targets were not executed; the most recent Code session reports
them green (21 tests / 56 assertions) and the built-output checks
are now enforced by lint-book on every `make check`, which lowers
the cost of this limitation.

## Scores (rubric 1.0, unmodified)

| # | Dimension | W | Current | Drafted-only | Evidence / rationale |
|---|---|---|---|---|---|
| 1 | Intellectual coherence | 5 | 5 | 5 | Spine unchanged and consistent end-to-end; no findings. |
| 2 | Fitness for purpose | 5 | 3 | 4.5 | Unchanged: teach-the-method works from drafted material; clone-and-adapt still awaits ch 24–34 companion obligations (`observation->oru` stub is scoped to ch 31). |
| 3 | Dual-audience execution | 4 | 4.5 | 4.5 | Introduce-twice holds in drafted chapters; Naturality entry closes the last known thesis-term gap. |
| 4 | Manuscript ↔ code | 5 | 4 | 4.5 | A1–A3 fixed and pinned by test; residual F1+F2 (one stale sentence, one phantom identifier, same locus). |
| 5 | Code quality & tests | 4 | 4.5 | 4.5 | Docstring fixed; pinning assertion added; golden/mutation/property layers intact. |
| 6 | Reference accuracy | 4 | 4 | 4.5 | Cited bib verified with per-field sources; Part IV dates current (2026-07-21); uncited TODOs deliberate and lint-tolerated by construction. |
| 7 | Internal consistency | 4 | 4 | 4.5 | Status truth, naming, and agent surfaces all reconciled; residual F1–F3. |
| 8 | Completeness | 4 | 2.5 | — | No chapters landed since baseline (correctly out of the remediation plan's scope); 7 narrative chapters + frontmatter remain stubs. |
| 9 | Navigability | 3 | 4 | 4.5 | Reader-facing navigation unchanged; agent-facing wayfinding improved (archive convention, named record dirs) but that scores under 10. |
| 10 | Process scaffolding | 3 | 5 | 5 | Strengthened: lint-book incl. cited-entry check, pack dotfile coverage, plans/reviews/log all operational and demonstrably used. |

**Weighted Current: 4.0** (165/41). **Weighted Drafted-only: 4.6**
(170.5/37).

Delta vs baseline: Current +0.4, Drafted-only +0.3 — driven by
dimensions 4, 6, and 7 exactly as the remediation plan's Completion
clause predicted, and capped by dimension 8, which only chapter
work (the handoff queue's business) can move.

## Remediation

Two-item follow-up plan drafted at
`.agents/plans/2026-07-22-review-followup.md` (F1+F2 as one item
with verbatim replacements; F3 as a one-space edit). No fixes were
applied in this review.
