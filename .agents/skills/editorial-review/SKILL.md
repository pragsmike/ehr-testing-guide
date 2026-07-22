---
name: editorial-review
description: Conduct a full editorial and technical review of the EHR Testing Guide repository — manuscript, companion code, notes, and agent surfaces — scoring it against the frozen rubric in this directory and producing a findings report plus remediation plan. Use when the author asks for an editorial review, a repo audit, a quality/consistency check, a "score the repo" request, or after a milestone (a part landing, a chapter batch reaching review) to measure drift. Works in two modes: full (Claude Code, filesystem + build/test execution) and static (chat, pack only).
---

# Editorial Review

## Purpose

Re-runnable, comparable quality reviews of this repository. The
original ad-hoc review prompt ("review the repo, find
inconsistencies, deduce purpose and audience, devise a rubric, score
it, plan remediation") produced a good one-off but had structural
weaknesses this skill fixes: the rubric was regenerated per run (so
scores were not comparable across reviews), purpose/audience were
re-deduced each time (they are stated in the repo; re-deducing
invites drift), findings had no severity taxonomy or evidence
standard, there was no awareness of prior reviews, and nothing
stopped the reviewer from "helpfully" fixing things mid-review. This
skill measures; remediation is planned, not performed.

## Use this skill when

- The author asks for an editorial review, audit, or scoring of the
  repo, or asks "how is the book doing" in a quality (not schedule)
  sense.
- A milestone lands (a part reaches review, a remediation plan
  closes) and the author wants the delta measured.

## Do not use this skill when

- The author wants a specific defect fixed — do that directly.
- The author wants a chapter critiqued as prose (argument, style,
  pacing) — that is editorial judgment work for chat, not this
  checklist.
- The author wants the rubric itself changed — that is a plan/ADR
  decision; see the freeze note in `rubric.md`.

## Inputs

- **Full mode (default; Claude Code or any agent with the repo
  filesystem):** the working tree, ability to run `make book`,
  `make check` / `clojure -X:test`, and web search for verification
  spot-checks.
- **Static mode (chat, working from a `make pack` upload):** the
  pack only. No execution; all code findings are static-analysis
  findings and must be labeled as such.
- Both modes: `rubric.md` from this directory,
  `.agents/reviews/scores-log.md`, and the most recent prior report
  in `.agents/reviews/` if one exists.

## Procedure

1. **Establish trust in the input.** Full mode: note the current
   commit (`git log -1 --format='%h %ad'`) and whether the tree is
   dirty. Static mode: verify pack freshness before believing
   anything — check the pack contains a distinctive phrase from the
   most recent work described in the latest handoff, and the
   expected claims-register rows; if it does not, stop and request a
   re-pack rather than reviewing a stale snapshot.
2. **Read the reasoning-of-record first, as ground truth for
   intent:** `AUTHORS-GUIDE.md`, `notes/design-rationale.md`,
   `notes/ADRs.md`, `notes/claims-register.md`, the latest
   handoff, and any open plans in `.agents/plans/` (skip `archive/`
   subdirectories — spent plans are history, consulted only when
   a finding requires tracing a past decision). These files outrank
   the reviewer's inference about what the project is trying to
   be. Do NOT re-deduce purpose and audience from scratch; take
   them from the README/preface and note discrepancies with the
   stated intent as findings rather than silently substituting
   your own reading.
3. **Read prior review state.** Load `.agents/reviews/scores-log.md`
   and the previous report in `.agents/reviews/`. The review must
   classify each new finding as new, persisting, or regressed, and
   must check whether previously reported findings were fixed,
   consciously dropped, or ignored.
4. **Inventory and mechanics (full mode also executes):**
   - Full mode: run `make check`; record test/assertion counts and
     any lint output. Run `make book` and grep the *built* output
     (newline-flattened — the ~75-col wrap splits phrases) for
     leaked TODOs, `n.d.`, and unresolved `[@` citations.
   - Both modes: enumerate files; compare against the layout that
     README/AGENTS.md claim; note anything referenced but absent
     and anything present but undocumented.
5. **Run the consistency check families.** Each family names its
   evidence by file + grep-able phrase (never line numbers — they
   drift between packs):
   a. **Status truth:** AUTHORS-GUIDE status table vs actual file
      contents (a multi-KB drafted file marked `stub` is a finding).
   b. **Manuscript ↔ companion (ADR-0007):** every code listing,
      identifier, tolerance value, and count claimed in prose exists
      and matches in `companion/` — including elided listings, which
      must not contradict the real code they elide.
   c. **Term coverage:** every categorical/healthcare term used in
      committed content (drafted prose AND thesis blockquotes of
      stub chapters) has its glossary/acronym entry; introduce-twice
      honored in reading order.
   d. **Claims register:** every `in ch NN` status corresponds to
      the claim actually appearing in that chapter; transcript-only
      claims listed.
   e. **Part IV discipline:** every entry uses the template schema,
      carries an `Entry last verified` date, and its factual claims
      (versions, maintenance status, domain codes) spot-check clean —
      full mode may web-verify a sample.
   f. **Bibliography:** every cited key exists in the .bib; cited
      entries carrying TODO-verify are findings; uncited TODO
      entries are nits.
   g. **Specimen integrity (AUTHORS-GUIDE §5):** if the specimen
      changed since the last review, confirm all five listed
      chapters were checked.
   h. **Agent surfaces:** AGENTS.md, CLAUDE.md, and `.agents/`
      agree with each other and with the tree (skills that exist are
      listed; paths named actually resolve).
   i. **Domain accuracy:** spot-check clinical/standards facts the
      argument leans on (e.g. LOINC code identities, HL7 field
      semantics). Full mode: web-verify at least the facts that are
      load-bearing for a worked example.
6. **Classify findings by severity**, each with evidence:
   - **error** — factually wrong or self-contradictory content a
     reader could act on;
   - **inconsistency** — two places in the repo disagree;
   - **gap** — something the repo's own conventions require and
     lacks;
   - **stale** — was true, no longer is (docstrings, statuses,
     comments not covered by the leave-after-payment convention);
   - **nit** — cosmetic.
   Deliberate, documented states (intentionally stale sync comments,
   TODO-verify entries awaiting the verification pass, stub chapters
   per the writing order) are not findings; cite the documenting
   source when excluding them.
7. **Score against `rubric.md` as-is.** Both scores (Current,
   Drafted-only), per-dimension evidence per the rubric's rules,
   weighted totals. Do not add, remove, or reweight dimensions.
8. **Write the remediation plan** as a delta: update the open plan
   in `.agents/plans/` if one covers the findings, otherwise draft a
   new dated plan there following the existing plan's format
   (verbatim replacement text where prose changes are prescribed;
   acceptance checks per item; author-decision items flagged;
   chapter-drafting work pointed at the handoff queue, never
   duplicated).
9. **Report and stop.** Write the report (see Output), append one
   row to `.agents/reviews/scores-log.md`, and stop. Apply NO fixes
   — not even "obvious" one-liners; the review's value is that it measures a
   state, and fixing mid-measure contaminates both the measurement
   and the diff the author expects. No git operations ever (WSL-only
   rule; commits are the author's).

## Output

- **Review report** at
  `.agents/reviews/YYYY-MM-DD-review.md`:
  input identity (commit or pack-freshness evidence), mode,
  findings by severity with evidence, disposition of prior
  findings, the full per-dimension score table with rationale,
  weighted totals, and the remediation delta (or pointer to the
  updated plan).
- **One appended row** in `.agents/reviews/scores-log.md` (date,
  mode, Current and Drafted-only weighted scores, one-line delta
  note). Never edit prior rows.
- **Plan update or new plan** in `.agents/plans/` per step 8.

## Done when

- [ ] Input freshness/identity established and recorded.
- [ ] All nine check families run (or explicitly marked
      not-runnable in static mode, with what was checked instead).
- [ ] Every finding carries severity + grep-able evidence;
      documented-deliberate states excluded with citations.
- [ ] Prior findings dispositioned (fixed / persisting / dropped).
- [ ] Scores recorded against the unmodified rubric; log row
      appended.
- [ ] Remediation exists as a plan file, not as applied edits.
- [ ] Zero fixes applied, zero git operations performed.
