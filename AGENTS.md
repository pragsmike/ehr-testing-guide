# AGENTS.md

> Primary instruction surface for AI coding agents working in this
> repository. Read by tools that support the `AGENTS.md` convention
> (Codex, OpenCode, and others). Claude Code users: see `CLAUDE.md`, which
> points here.

## Project overview

**Project:** EHR Testing Guide — a book-length practitioner's guide to
writing test plans for EHR (electronic health record) data
transformations, using Clojure tooling and category theory as the
reasoning language, plus its Clojure reference implementation.
**Language(s):** Markdown (manuscript), Clojure (companion).
**Architecture:** a two-part monorepo — `manuscript/` (Pandoc-flavored
Markdown book source) and `companion/` (a `deps.edn` Clojure project that
is the book's reference implementation; every listing shown in the book
runs there).

Full human-facing detail — chapter structure, writing conventions, the
running specimen, toolchain notes — lives in [`README.md`](README.md) and
[`AUTHORS-GUIDE.md`](AUTHORS-GUIDE.md). Read `AUTHORS-GUIDE.md` before
writing or editing any manuscript chapter or companion code; this file
only covers what an agent needs before taking its first action.

`notes/` holds the project's authoritative **reasoning-of-record** —
`design-rationale.md` (why the book is shaped as it is), `ADRs.md`
(architecture and authoring decision records), and `claims-register.md`
(insight-to-chapter coverage). These files take precedence over your own
inference about the project's structure: consult them before restructuring
the manuscript or the reference layout, and never silently revert an
Accepted ADR — supersede it with a new record.

`.agents/` holds the project's agent-facing working state:
`.agents/plans/` (long-running remediation and work-order plans),
`.agents/handoffs/` (session-continuity handoffs, populated by the
`handoff` skill), and `.agents/reviews/` (the editorial-review scores
log and dated review reports) — check these before starting an
integration session. `notes/handoff-*.md` remains the historical
continuity record until/unless it is migrated into `.agents/handoffs/`.
Directories named `archive/` under any of these (e.g.
`.agents/plans/archive/`) hold spent records kept for history: do
not read them during normal work — consult them only when a task
explicitly requires reviewing past decisions or prior plan
history.

## Before your first git operation: read this

**All git operations — especially `git commit` — must be run from WSL,
never from native Windows.** This is a hard rule (see `AUTHORS-GUIDE.md`,
"Git operations: WSL only"), not a style preference: mixed-platform
commits are how past repositories ended up with line-ending wars. It is
technically enforced by `.githooks/pre-commit`, but only once `make hooks`
has been run for a given clone — don't rely on the hook to catch a mistake
for you. Confirm you are in WSL *before* attempting a commit, not after a
rejection.

If you are an agent running in a Windows-native shell (PowerShell,
cmd.exe, or Git Bash/MSYS outside WSL) and a commit is needed, either hand
off to the user to commit from WSL themselves, or invoke WSL explicitly
(e.g. `wsl.exe bash -lc '...'`) so the git operation genuinely executes
inside WSL against the repo. Building and testing (`make check`,
`clojure -X:test`) are fine from either platform — only git operations are
restricted.

## Quick start

```sh
make book    # concatenate manuscript chapters -> build/book.md
             # (+ build/book.html if pandoc is on PATH; skipped with a
             # notice, not an error, if it isn't)
make test    # cd companion && clojure -X:test
make check   # both of the above
make pack    # concatenate manuscript + companion sources into
             # $HOME/ehr-testing-guide-pack.txt (outside the repo), for
             # pasting into a chat UI that can't read the filesystem
make hooks   # (run once per clone, from WSL) activate the WSL-only
             # pre-commit hook
```

`make check` requires the Clojure CLI (`clojure`) on `PATH`. See
`companion/README.md` for how to get `clojure`/`make`/`pandoc` on Windows
vs. WSL.

## Code conventions

- Manuscript chapter files are named `NN-slug.md` so lexical sort is
  reading order. Every chapter needs an H1, a `> **Thesis:**` blockquote
  (a real 2–4 sentence claim, not a placeholder), and H2 section stubs.
  Start new chapters from `manuscript/templates/chapter.md`.
- Part IV reference entries follow one fixed schema — never write one
  freeform. Start from `manuscript/templates/reference-entry.md`.
- Every code listing referenced in the manuscript must exist, run, and be
  tested in `companion/`. No pseudocode.
- New categorical or healthcare terms get a glossary and/or acronym entry
  (`manuscript/90-backmatter/91-glossary.md`, `92-acronyms.md`) in the
  same change that introduces them, not deferred to a later pass.
- Full conventions, writing order, and the running specimen's cross-
  chapter checklist are in `AUTHORS-GUIDE.md`.

## Constraints

- **Git operations: WSL only.** See above — this is the constraint most
  likely to trip up an agent that hasn't read this file yet.
- **This repository is public, or will be.** Never write secrets, tokens,
  credentials, or personal data beyond what's already committed, into any
  file, commit message, or generated artifact — including `make pack`
  output, which is written outside the repo but still should never be
  used to stash anything sensitive.
- Don't hand-edit `build/` — it's generated by `make book` and gitignored.
- Don't add a Part IV entry, chapter, or code listing without following
  the templates and testing conventions above; `AUTHORS-GUIDE.md` section
  6 has the full pre-push checklist.

## Skills

`.agents/skills/` holds this repo's local skills: `editorial-review`
(the rubric-scored quality audit that this remediation plan came from),
plus general-purpose agent-hygiene skills — `handoff`, `committee`,
`find-skills`, `repo-adaptation`, `shared-skill-layout`, and
`wsl-windows-git-hygiene`. If a repeatable multi-step workflow emerges
— e.g. "scaffold a new chapter," "add a Part IV entry plus its
glossary/acronym entries in one pass" — add it under
`.agents/skills/<slug>/SKILL.md` rather than reinventing the steps each
time.

## Compatibility

- **Claude Code** reads `CLAUDE.md`, not `AGENTS.md`, by convention.
  `CLAUDE.md` at the repo root is a thin pointer to this file — keep both
  in sync if you edit one.
- **Codex / OpenCode** read `AGENTS.md` natively.
- **Cursor** and other tools with their own instruction-file convention:
  none exist in this repo yet. Add one only if the team actually adopts
  that tool, and keep it a thin pointer to this file rather than a
  parallel copy that can drift.
