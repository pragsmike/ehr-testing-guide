# Authors' Guide

This is the guide for future-you and any collaborator working on this book.
It exists so decisions don't have to be re-litigated every time someone opens
the repo.

If you are an AI coding agent, read [`AGENTS.md`](AGENTS.md) (or
[`CLAUDE.md`](CLAUDE.md), which points to it) first — it covers the
WSL-only git rule and other things you need before taking your first
action, and links back here for everything else.

## 1. Why this shape

The manuscript and the companion Clojure project are one repository because
they evolve in lockstep. The book's authority rests on a specific promise:
every listing shown on the page runs, in the version of `companion/` sitting
next to it. That promise is only checkable if the two live under one root
and one CI run. A book about a discipline that treats "it worked on my
machine" as a failure mode should not itself be exempt from that standard.

Part IV (`40-part-4-reference/`) is split into its own files, separate from
Parts I–III, because reference material rots on a different clock. HL7
publishes new FHIR versions and IGs; HAPI and other tools ship releases;
UMLS and terminology bindings drift. Part IV keeps receiving commits long
after Parts I–III have stabilized, and every entry in it carries an
`Entry last verified` date for exactly this reason — a reference chapter
with no verification dates is just a chapter that used to be true.

**Reasoning-of-record.** The fuller argument behind this shape is recorded
in `notes/`: `design-rationale.md` (the argument behind the book's
structure), `ADRs.md` (architecture and authoring decisions — supersede one
with a new record, never silently revert an Accepted one), and
`claims-register.md` (an insight-to-chapter coverage checklist). These three
files outrank any agent's — or any collaborator's — own inference about how
or why the project is organized; consult them before restructuring anything,
and update the claims register's status column when a claim reaches drafted
prose.

## 2. Writing order and current status

Recommended writing order, and why:

1. **The test-plan artifact templates** (`94-test-plan-templates.md`) first.
   Writing the worksheet the reader will actually fill in forces the
   vocabulary of Part II to exist before Part II's prose does.
2. **Part II** (the method — correctness, purpose sets, corpus, properties,
   validators) before **Part III** (the structure — lenses, event logs,
   type systems, terminology-as-relations). Part III explains *why* Part
   II's method is the right one; readers need the method in hand before the
   justification is interesting.
3. **Part I and the preface last.** They're the sales pitch and the
   orientation lap, and both are easiest to write once you know what you're
   pitching and orienting toward.
4. **Chapter 21 is the keystone.** It defines correctness-relative-to-a-
   purpose-set, which everything downstream — the lattice in 22, the
   properties in 24, the lens laws in 31 — depends on. If chapter 21 does
   not write cleanly, the spine of the book is wrong and no amount of
   polish on the surrounding chapters will fix it. Stop and re-derive it
   before writing forward.

Chapter status (update as work proceeds — values: `stub` / `drafting` /
`review` / `stable`):

| # | Chapter | Status |
|---|---|---|
| 01 | Title | stub |
| 02 | Preface | stub |
| 11 | The Pandoc Dream | review |
| 12 | Deltas, States, and Signatures | review |
| 13 | Orientation to the Standards | review |
| 21 | What "Correct" Means | review |
| 22 | Purpose Sets and the Correctness Lattice | review |
| 23 | Designing the Corpus | review |
| 24 | Properties and Metamorphic Relations | stub |
| 25 | Validators as Gates | stub |
| 26 | Notes for the Non-Clojure Reader | stub |
| 31 | Why Retractions Fail, and Lenses | stub |
| 32 | The Event Log as Universal Object | stub |
| 33 | The Type Systems Nobody Mentions | stub |
| 34 | Terminology: Where Arrows Are Not Functions | drafting |
| 41 | The Standards Family | review |
| 42 | The Terminology Layer | review |
| 43 | Clojure-Reachable Tools | review |
| 91 | Glossary | drafting |
| 92 | Acronyms | drafting |
| 93 | Standards and Bodies | drafting |
| 94 | Test Plan Templates | review |
| 95 | Failure Mode Index | drafting |
| 96 | Bibliography | stable |

## 3. Conventions

- **File naming is reading order.** `NN-slug.md` inside `NN-part-name/`
  directories, so a lexical sort of the tree is the table of contents. Never
  renumber to "make room" — pick a number in the gap, or if the gap is
  exhausted, renumber the whole part in one commit and say so.
- **The introduce-twice rule.** Every categorical term (span, lens,
  naturality, retract, ...) is introduced operationally first — what it
  does, in the healthcare example at hand — and only then given its name.
  Every healthcare/HL7 term (Z-segment, OBX-2, C-CDA entry, ...) gets the
  same treatment for the reader coming from category theory. Neither
  audience should hit a bare term they have to look up mid-sentence.
- **Glossary and acronym entries are added at first use, not as a final
  pass.** If you use a term in a draft, it goes in
  `90-backmatter/91-glossary.md` or `92-acronyms.md` in the same commit (or
  the next one, but before the chapter leaves `drafting`). A end-of-project
  glossary sweep always misses entries because nobody rereads the whole book
  looking for them.
- **No pseudocode.** Every code listing referenced or shown in the
  manuscript lives in `companion/`, is real Clojure, and is covered by a
  test. If a listing doesn't run, the chapter isn't done.
- **Citations are BibTeX keys** from `manuscript/bibliography.bib`, cited as
  `[@key]` in prose. Add the entry to the `.bib` file in the same commit
  that introduces the citation.

## 4. Templates

- Use [`manuscript/templates/chapter.md`](manuscript/templates/chapter.md)
  to start **every** new chapter in Parts I–III. It carries the H1/Thesis/H2
  skeleton and the introduce-twice reminder.
- Use [`manuscript/templates/reference-entry.md`](manuscript/templates/reference-entry.md)
  for **every** entry in Part IV. Never write a Part IV entry freeform —
  the whole point of Part IV is that every entry answers the same
  questions in the same order, so a reader can scan it like a table.

## 5. The specimen

The book has one running example, threaded through every part so the reader
never has to re-orient to a new dataset: an HL7 v2 **ORU^R01** result
message (a single numeric lab result, LOINC-coded, UCUM-unitted) being
transformed into a FHIR R4 **Observation**. It lives in code as
`companion/src/ehr_testing/specimen.clj`.

The specimen appears in:

- **Ch. 21–22** — as the running example for defining a purpose set and
  showing the correctness lattice split by recipient.
- **Ch. 23–24** — as the seed for corpus design and as the subject of the
  chapter's property-based tests and metamorphic relations.
- **Ch. 31** — as the worked lens: `oru->observation` / `observation->oru`
  as a get/put pair, and where its round-trip law fails.
- **Ch. 33** — as the worked example of OBX-2 as a runtime type tag and
  OBX-5 as the resulting dependent sum.
- **Ch. 34** — as the worked example of the LOINC-to-local-code gap in the
  terminology layer.

**If you change the specimen** (the message text, the target schema, the
function signatures in `specimen.clj`), you must check and update all five
of the above. This list is the checklist — do not rely on memory or on
grepping for the string "ORU", since prose references don't always contain
it.

## 6. Anti-drift checklist (run before every push)

- [ ] `make check` passes (Pandoc concatenation succeeds; companion tests
      pass).
- [ ] Any new categorical or healthcare term introduced in this diff has a
      glossary and/or acronym entry.
- [ ] Any new Part IV entry uses `reference-entry.md` verbatim as its
      skeleton and has a current `Entry last verified` date.
- [ ] Any new or changed code listing exists in `companion/` and is
      exercised by a test.
- [ ] If the specimen changed, the five chapters in §5 were checked.

## 7. Git operations: WSL only

**All git operations on this repo — especially commits — are done from
WSL, never from native Windows.** This is a hard rule, not a preference:
mixed-platform git usage (committing from Windows sometimes, WSL other
times) is exactly how other repos ended up with line-ending wars, spurious
executable-bit flips, and diffs that are 90% whitespace noise. `make check`
building fine on Windows (§8 below) is not an exception to this — running
the build there is fine; writing to git history from there is not.

`.gitattributes` (`* text=auto eol=lf` plus explicit per-extension rules)
is defense in depth for readers and tools that check out the repo without
knowing this rule. It is not a substitute for it — don't let its presence
talk you into "it's probably fine to commit from Windows this once."

**Enforcement:** `.githooks/pre-commit` refuses to run outside WSL (it
checks `$WSL_DISTRO_NAME` and `/proc/version` for the WSL kernel marker).
It only becomes active once someone runs, from WSL:

```sh
make hooks
```

which points `core.hooksPath` at the tracked `.githooks/` directory instead
of the untracked, per-clone `.git/hooks/`. Run this once per clone or
worktree, from WSL, before your first commit. The hook only guards
`git commit` (including the commits rebase/cherry-pick/merge make under the
hood) — it can't gate every git subcommand, so the rule still relies on you
not running `git push`/`git merge`/etc. from Windows either.

## 8. Toolchain notes

- **Pandoc is optional locally.** `make book` detects its absence and
  prints a notice instead of failing — CI owns the canonical rendered
  build (`build/book.html`), so you don't need Pandoc installed to write
  or commit chapters.
- **Spacemacs + CIDER + clojure-lsp, run from WSL,** is the reference
  editing environment for `companion/`. `companion/.lsp/config.edn` and
  `companion/.clj-kondo/config.edn` are checked in so both tools work with
  no local setup beyond having the binaries on `PATH`.
- **Jack in with the `:dev` alias** (`clojure -M:dev` or your CIDER
  jack-in command configured to add `:dev`) — it pulls in `nrepl/nrepl`
  and `cider/cider-nrepl` without adding them to the base dependency set
  that `make test` resolves.
- **Windows is a supported secondary environment for building and
  reading**, just not for git operations (§7). `make check` runs fine
  there — Windows has no first-party `clojure`/`make`/`pandoc` packaging,
  but each has a no-admin-rights install path; see `companion/README.md`.
  Nothing in `deps.edn`, the Makefile, or the Clojure sources is Windows-
  or WSL-specific.
- **`make pack`** concatenates every manuscript and companion source file
  (`*.md`, `*.clj`, `*.edn`, `*.bib`, the `Makefile` — skipping dotfiles
  and dotdirs generally, and `build/`), then re-includes an explicit dotfile
  allow-list — `.agents/` (plans, skills, handoffs, reviews), `.githooks/`
  (the WSL-only pre-commit hook), `.gitattributes`, and `.gitignore` — so
  a chat session that can't read the filesystem directly can still see the
  continuity and enforcement files it needs. The trailing directory listing
  uses `ls -alR` so dotfiles/dotdirs are visible there too. Output goes to
  `$HOME/ehr-testing-guide-pack.txt`, outside the repo.
