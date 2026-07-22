# Plan: Editorial Remediation — 2026-07-22 (rev 2)

<!-- Long-running plan derived from the 2026-07-22 editorial review
     (baseline scores in .agents/reviews/scores-log.md). Rev 2
     (2026-07-22): folded in verbatim C1 entry text, recorded the
     B1/B2 decisions (defaults adopted; author may override in the
     Code prompt before execution), added the D1 prose-touching
     review caveat, added reviews/ to B3, and added D3's
     verify-before-commit step. Owns the agent-executable
     remediation work only. Chapter drafting (the review's "Tier
     3") is deliberately NOT duplicated here — the writing queue
     lives in the current notes/handoff-*.md and that file remains
     its single source of truth. Update the Status column as items
     land; when every item is done or superseded, mark this plan
     closed at the top rather than deleting it. Statuses:
     todo / in-progress / blocked / done / dropped. -->

**Status: CLOSED (2026-07-22). All items A1–D4 done. The Completion
clause's follow-up editorial review is queued as the next standing action
and, per author decision at closure, does not block archival; its delta
row will land in `.agents/reviews/scores-log.md` when it runs.**
**Source review:** editorial review of pack dated 2026-07-22 (repo
state: 9 narrative chapters + Part IV in review; suite 21 tests / 55
assertions green).
**Execution model:** items are sized for a Claude Code integration
session under the established conventions (AUTHORS-GUIDE, handoff
"working method"): verbatim text supplied where prose is written
(below, in this plan — content on disk, not paste blocks), Code
inserts and never composes, acceptance checks per item,
report-and-stop, no git writes — commits are the author's, from WSL.
**Exception:** D1 (bibliography verification) is inherently
prose-touching — Code composes corrected fields from web search.
Per the ch-43 NIST precedent, its output is reviewed field-by-field
by the author, with the source URL for every changed field listed in
the session report.

---

## A. Correctness fixes

### A1. `value-vs-threshold` tolerance contradiction — done

The companion implements the quality-measure `value-vs-threshold`
row as `[:same-classification (<= value quality-measure-threshold)]`
(companion/src/ehr_testing/properties.clj), which is conceptually
correct: the measure asks which side of its threshold the value
falls on. Two manuscript locations contradict it by stating `Exact`:

1. `manuscript/90-backmatter/94-test-plan-templates.md`, Recipient B
   table. Replace the row
   `| Numeric value vs threshold | OBX-5 | Exact | Decision-affecting |`
   with
   `| Numeric value vs threshold | OBX-5 | Same classification (≤ threshold vs >) | Decision-affecting |`
2. `manuscript/20-part-2-the-method/22-purpose-sets-and-the-correctness-lattice.md`,
   the elided companion listing. Replace the line
   `{:name :value-vs-threshold :tolerance :exact ...}`
   with
   `{:name :value-vs-threshold :tolerance :same-classification ...}`

Then pin the tolerance so this cannot drift silently again: in
`companion/test/ehr_testing/properties_test.clj`, extend
`combined-suite-test` with one assertion that the
`:value-vs-threshold` row's tolerance kind is `:same-classification`
(same style as the existing `:code-in-value-set` first-element
check).

**Acceptance:** `clojure -X:test` green; flattened-newline grep of
`build/book.md` finds "Same classification" in the §94 Recipient B
table and no `:tolerance :exact` on a value-vs-threshold line.

### A2. §94 tolerance vocabulary alignment — done

§94's worksheet field list enumerates tolerances as "Exact /
normalized / within-range / set-equal", which no longer matches the
vocabulary Chapters 21–22 and the companion converged on (`exact`,
`normalized`, `same-classification`, `set-membership`,
`within-period`). Also Recipient A's in/out-of-range row says
"Set-equal" where ch 21 and the code say same-classification.

1. In `94-test-plan-templates.md`, Agreement-tolerance field
   description: replace
   `Exact / normalized (e.g. unit-canonicalized) / within-range / set-equal.`
   with
   `Exact / normalized (e.g. unit-canonicalized) / same classification (e.g. in-range vs out) / set membership / within period.`
2. Recipient A table: replace
   `| In/out of range | OBX-7 / OBX-8 flag | Set-equal | Decision-affecting |`
   with
   `| In/out of range | OBX-7 / OBX-8 flag | Same classification | Decision-affecting |`

**Acceptance:** built-book grep finds the new field-list sentence;
"Set-equal" no longer appears anywhere in §94.

### A3. §94 date-row source reconciliation — done

§94 Recipient B lists `OBR-7 / OBX-14` as the q-source for
date-in-period; the companion reads only `:effective-time` (OBR-7),
and ch 21/22 use OBR-7 throughout. Keep the worksheet honest about
the alternative without implying the companion reads OBX-14: replace
`| Date in measurement period | OBR-7 / OBX-14 | Within-period | Decision-affecting |`
with
`| Date in measurement period | OBR-7 (OBX-14 where populated; the companion reads OBR-7) | Within-period | Decision-affecting |`

**Acceptance:** built-book grep finds the parenthetical.

## B. Consistency and drift

### B1. Chapter 12 name reconciliation — done

**DECIDED (default adopted; override in the Code prompt if
preferred):** keep the filename `12-deltas-sigmas-signatures.md`
(the slug uses the chapter's own delta/sigma vocabulary, and
ADR-0008 makes renames noisy) and align the status-table title to
the H1. Change the AUTHORS-GUIDE table row to
`| 12 | Deltas, States, and Signatures | review |`.
The alternative — slug-matches-title via `git mv` from WSL — remains
available; if taken, record it here.

**Acceptance:** status-table title matches the chapter H1 exactly.

### B2. AUTHORS-GUIDE status-table refresh — done

**DECIDED (default adopted; override in the Code prompt if
preferred):** apply these values — 91 → drafting, 92 → drafting,
93 → drafting, 94 → review, 95 → drafting, 96 → stable. Rationale:
the accreting backmatter files (glossary, acronyms, standards
table, failure-mode index) grow with every chapter and never really
reach review before Part III lands, so `drafting` is honest; §94 is
a complete four-worksheet artifact; §96 is a citeproc shell and is
as done as it will ever be. The alternative — adding a fourth
status value (e.g. `living`) for accreting files, with a one-line
legend change in AUTHORS-GUIDE §2 — remains available; if taken,
record it here.

**Acceptance:** table matches the values above; no row says `stub`
for a file over 2 KB.

### B3. Agent-surface pointer updates for `.agents/` — done

The repo has adopted `.agents/` (skills, plans, handoffs, reviews).
Three stale references:

1. `AGENTS.md` "Skills" section still opens "No repo-local skills
   exist yet (there is no `.agents/skills/` directory)." Replace
   that paragraph with a short listing of the skills that now exist
   (currently: `editorial-review`) and the standing instruction to
   add new repeatable workflows under
   `.agents/skills/<slug>/SKILL.md`.
2. `CLAUDE.md` references `.claude/commands/` / `.claude/skills/`
   as where Claude-specific skills would live. Rewrite that bullet:
   canonical skills live in `.agents/skills/`; if Claude Code
   auto-discovery is wanted later, add thin pointer wrappers under
   `.claude/skills/` that reference (never duplicate) the `.agents`
   copies.
3. `AGENTS.md` should gain one sentence in the project-overview or
   notes paragraph naming `.agents/plans/` (long-running plans),
   `.agents/handoffs/` (session continuity, populated by the
   handoff skill), and `.agents/reviews/` (editorial-review scores
   log and dated reports) so agents know to look — and note that
   `notes/handoff-*.md` remain the historical record until/unless
   migrated.

**Acceptance:** grep confirms no remaining claim that no repo-local
skills exist; CLAUDE.md no longer names `.claude/` as the canonical
skills home; AGENTS.md names all three `.agents/` record
directories.

### B4. Stale companion docstrings — done

`companion/test/ehr_testing/smoke_test.clj` ns docstring still says
the specimen's transformation logic "is still stubbed" —
`parse-oru` and `oru->observation` have been implemented since.
Replace the clause with "(only `observation->oru` remains stubbed —
see ehr-testing.specimen)". While there, "the three core
namespaces" may stay (the test does load three), but confirm the
sentence still reads true.

**Acceptance:** `clojure -X:test` green; grep of smoke_test.clj
finds no "still stubbed" claim about the transform as a whole.

## C. Term-coverage debt

### C1. Glossary and acronym additions — done

Thesis blockquotes are committed content (handoff convention), so
terms they use are owed entries now, not at chapter landing. Insert
the following verbatim (Code inserts, never composes).

Glossary entry, `manuscript/90-backmatter/91-glossary.md`,
alphabetical slot between **Metamorphic relation** and
**Observation (as query)**:

```
**Naturality (naturality condition)** — The requirement that two
routes from the same start to the same end agree: transforming and
then asking a question yields the same answer as asking and then
transforming, for every input at once. Chapter 21's commuting
triangle is one instance; Chapter 24's central move is that a
metamorphic relation is exactly a naturality condition written as
an executable test. See Chapter 24.
```

Acronym rows, `manuscript/90-backmatter/92-acronyms.md` (the table
is grouped thematically, not alphabetically — place ST directly
after the NM row, ORM directly after the ORU row, CQRS in the
closing group alongside BX/IG/MLLP):

```
| ST | String (HL7 v2 datatype) |
| ORM | Order Message (general order) message type (HL7 v2) |
| CQRS | Command Query Responsibility Segregation (the read/write separation Chapter 32 maps the event log onto) |
```

**Acceptance:** all four entries present at the placements above;
glossary alphabetical order preserved; `make book` clean.

## D. Verification debt

### D1. Bibliography verification pass — done
### (prose-touching; see header exception)

Clear the TODO-verify / TODO notes so citeproc stops rendering them
into the built book. Verify (web search; record accessed dates):
`morris1969` (title wording, department), `hl7_v2_to_fhir_ig` and
`cda_fhir_mapping_ig` (URLs + accessed dates — both are cited in ch
11), `pandoc` (accessed date), `hofmann2012edit` (POPL vs ICALP),
`diskin2011state` (volume/issue), `johnson_rosebrugh_delta_lenses`
(title/venue/year), `foster2007combinators` (article number),
`chen2018metamorphic` (article number). Uncited entries may be
verified opportunistically; cited entries are the priority. The
session report must list, per entry, each field changed and the
source URL it was verified against, for the author's field-by-field
review.

**Acceptance:** no `TODO` remains in a *cited* entry; `make book`
output contains no literal "TODO" and no "n.d." in the references
div for cited works; per-field source list present in the report.

### D2. Built-output lint target — done

The citeproc gap was caught only by grepping the built book; make
that check permanent. Add a `lint-book` target to the Makefile that
depends on `build/book.md` and fails on: literal `TODO` outside
HTML comments, `n.d.` in the refs section, and any unresolved `[@`
citation remaining in `build/book.html` (citeproc leaves these
verbatim when a key is missing). Wire it into `check`
(`check: book lint-book test`). Grep patterns must be
newline-flattened per the handoff lesson (the ~75-col wrap splits
phrases).

**Acceptance:** `make check` fails when a deliberately-broken
`[@nosuchkey]` is added to a scratch chapter, passes after removal.

### D3. Makefile: pack coverage of selected dotfiles — done

The Makefile's `pack` target excludes all dotfiles/dotdirs, so
`.agents/` (plans, skills, handoffs, reviews), `.githooks/pre-commit`,
and `.gitattributes` are invisible to chat sessions — exactly the
continuity and enforcement files a fresh session needs, and the
reason the 2026-07-22 review could not verify the WSL hook exists.
Modify the `pack` target: keep the general dotfile exclusion, then
run a second `find` over an explicit allow-list (`.agents`,
`.githooks`, `.gitattributes`) and append those files in the same
`FILE:`-sentinel format before the directory listing. Also change
the `ls -lR` to `ls -alR` so dotfiles appear in the listing. Update
AUTHORS-GUIDE §8's `make pack` description to match.

**Verification before commit (author):** run `make pack` and
confirm the output contains `FILE: ./.githooks/pre-commit`,
`FILE: ./.gitattributes`, and every file under `.agents/`, and that
the trailing listing shows dotdirs — this item is the one the whole
chat-review loop depends on; do not commit it unverified.

**Acceptance:** the fresh-pack check above passes.

### D4. lint-book: cited-entry TODO check in the raw .bib — done

Added after D2 landed: the refs-section TODO check cannot catch the
repo's TODO convention, because TODO placeholders live in `note`
fields and pandoc's default CSL style never renders `note` into the
bibliography (proven by probe in the 2026-07-22 nits session — a
TODO in a rendered field like `title` fires the check; a TODO in
`note` does not). The invariant is "no TODO in a *cited* entry," so
`lint-book` now checks it at the source: extract every `[@key]`
cited in manuscript sources, locate each key's entry block in
`bibliography.bib`, and fail if the block contains TODO. Uncited
placeholder entries (currently `fhir_mapping_language`, `cda_r2`,
`fhir_r4`, `us_core`) are tolerated by construction. The
refs-section `n.d.|TODO` check remains as belt-and-suspenders for
TODOs leaked into rendered fields.

**Acceptance:** clean tree passes `lint-book` (proving uncited
TODOs are tolerated); a probe TODO added to a cited entry's `note`
field fails it.

## E. Explicitly out of scope for this plan

- **Chapter drafting and its ordering** (ch 24 next, then 25,
  31–34, 26, frontmatter last): owned by the current handoff +
  ADR-0006. This plan must never grow a copy of that queue.
- **`observation->oru` and the lens-law properties**: Chapter 31's
  landing obligation (claims C2, C3), tracked via the chapter's
  companion-sync comment and the register — not here.
- **Claims register flips** (C9, C10, C13, C14, C20 still awaiting
  their chapters): land with their chapters.
- **Part IV re-verification dates**: refreshed whenever an entry is
  touched, per ADR-0009 — no bulk pass scheduled.

## Completion

Plan is closed when A1–D3 are done or dropped with a reason, any
override of the B1/B2 defaults is recorded inline, and a follow-up
editorial review (`.agents/skills/editorial-review`) has been run
and its scores appended to `.agents/reviews/scores-log.md` —
expected effect: dimension 7 (internal consistency) and dimension 6
(reference accuracy) move up; dimension 8 (completeness) moves only
with Tier-3 chapter work, which is not this plan's business.
