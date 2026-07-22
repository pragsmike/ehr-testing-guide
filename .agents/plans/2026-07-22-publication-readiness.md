# Plan: Publication Readiness — 2026-07-22 (rev 2)

<!-- Rev 2 (2026-07-22): all decisions recorded — licenses (Apache-2.0
     code / CC BY-NC-ND 4.0 manuscript, with two carve-outs), byline
     (Mike Gallaher), G2 resolved by fresh-history rebuild with
     delete-and-recreate of the GitHub remote, D2 confirmed, plus new
     items H7 (CITATION.cff) and H8 (post-flip settings). Executor
     tags: AUTHOR-LOCAL (must run on the real clone), DECIDED
     (recorded; agents execute), CODE (integration session).
     Statuses: todo / in-progress / blocked / done / dropped. Close
     and archive per the .agents/plans/archive/ convention. -->

**Status: OPEN.**

## Review verdict (context, not a task)

No secrets, credentials, emails, personal names, or personal paths
in any tracked file. Sample patient data unmistakably synthetic.
Committee roster fictional. Research file's negative maintenance
claims are dated, sourced, and criteria-defined. The
CVE-2026-33180 note was independently verified accurate. Remaining
work is licensing, history, one unknown dotfile, and hardening.

---

## GATE — do before flipping public

### G1. Licensing — done (DECIDED, then CODE)

**Decided:** `manuscript/` prose is CC BY-NC-ND 4.0 International;
everything else in the repository (companion/, notes/, .agents/,
research/, Makefile, root docs) is Apache-2.0. Copyright holder:
Mike Gallaher, 2026. Two carve-outs, both required to keep the
ND clause from defeating the manuscript's own purpose:

- **Carve-out 1 (listings):** code listings reproduced in the
  manuscript are the companion's code and remain Apache-2.0.
- **Carve-out 2 (templates/worksheets):** `manuscript/templates/`
  and the worksheets in
  `manuscript/90-backmatter/94-test-plan-templates.md` may be
  freely copied, filled in, and adapted for any purpose,
  notwithstanding the ND condition.

Execute as:

1. Root `LICENSE`: the canonical Apache-2.0 full text (fetch from
   https://www.apache.org/licenses/LICENSE-2.0.txt), with the
   standard copyright line "Copyright 2026 Mike Gallaher" applied
   per its appendix instructions.
2. `manuscript/LICENSE.md`, verbatim:

```
# Manuscript License

The prose of this manuscript (everything under `manuscript/`) is
licensed under the Creative Commons
Attribution-NonCommercial-NoDerivatives 4.0 International license
(CC BY-NC-ND 4.0): https://creativecommons.org/licenses/by-nc-nd/4.0/

Copyright 2026 Mike Gallaher.

Two exceptions, granted expressly:

1. **Code listings.** Every code listing reproduced in the
   manuscript also exists in this repository's `companion/`
   project and is licensed under Apache-2.0 (see the repository
   root `LICENSE`), not under this license. Use the code freely
   under Apache-2.0 terms regardless of where you read it.
2. **Templates and worksheets.** The files under
   `manuscript/templates/` and the worksheets in
   `manuscript/90-backmatter/94-test-plan-templates.md` are meant
   to be used: you may copy, fill in, adapt, and redistribute them
   for any purpose, commercial or not, notwithstanding the
   NoDerivatives condition. Attribution appreciated, not required.
```

3. README.md gains a "License" section, verbatim:

```
## License

Everything in this repository except the manuscript's prose —
the `companion/` project, build tooling, notes, and agent
scaffolding — is licensed under [Apache-2.0](LICENSE). The
manuscript's prose is
[CC BY-NC-ND 4.0](manuscript/LICENSE.md), with two express
exceptions: code listings shown in the book remain Apache-2.0,
and the templates and worksheets may be copied, filled, and
adapted for any purpose. See
[`manuscript/LICENSE.md`](manuscript/LICENSE.md) for the exact
terms.
```

4. `manuscript/00-frontmatter/01-title.md` gains, after the
   subtitle line, verbatim:

```

Mike Gallaher

*© 2026 Mike Gallaher. Prose licensed CC BY-NC-ND 4.0; code
listings Apache-2.0 — see the repository's license files.*
```

**Acceptance:** LICENSE and manuscript/LICENSE.md exist with the
texts above; README section present; `make book` renders the title
page with byline and license line; `make check` green.

### G2. Fresh history and fresh remote — todo (AUTHOR-LOCAL)

**Decided:** discard history and rebuild, and delete-and-recreate
the GitHub repository rather than force-pushing (pushed history
survives force-pushes as dangling objects, caches, and forks — the
old private repo must go). Procedure, all from WSL, in order:

1. Land G1, D1, and all H items in the working tree FIRST, so the
   first public commit is fully licensed and hardened.
2. `mv .git ~/ehr-testing-guide-old-git` — out of the repo
   entirely; keep as a private archive until confident, then
   delete.
3. `git init`.
4. **Before any commit**, set identity with a non-personal email:
   `git config user.name "Mike Gallaher"` and
   `git config user.email` to the GitHub noreply address
   (`<id>+<username>@users.noreply.github.com`; enable "Keep my
   email addresses private" in GitHub settings first). Commit
   email is the one PII channel the file review cannot cover, and
   fresh history makes this a one-time decision.
5. `make hooks` — the WSL-only pre-commit guard is tracked in
   `.githooks/` and needs only re-pointing; nothing is copied from
   the old `.git`.
6. Single initial commit; tag it (suggestion:
   `v0-public-baseline`).
7. Delete the old private GitHub repository; create the new public
   one; `git remote add origin <new-url>`; push with the tag.
8. Record here: "G2 resolved by history rebuild + fresh remote,
   <date>". No history scan is needed — nothing pre-rebuild is
   published.

### G3. `.claude/settings.local.json` disposition — todo
### (AUTHOR-LOCAL)

Unchanged from rev 1: `git ls-files .claude/` to determine tracked
status; if tracked, review contents, `git rm --cached`, and ignore
it; either way confirm `.gitignore` covers it plus `build/` and
`companion/.cpcache/`. Do this before G2 step 6 so the first
commit is already clean.

## DECISIONS — recorded

### D1. Byline and copyright — done (DECIDED)

Mike Gallaher, © 2026. Executed via G1's title-page and license
texts.

### D2. Workflow transparency — done (DECIDED)

notes/ and .agents/ publish as-is — working-in-the-open,
knowingly. (Recorded per author's "run the plan as-is",
2026-07-22; veto by reopening this item before G2 step 6.)

## HARDENING — land before G2's first commit

### H1. Synthetic-data statement — done (CODE)

README.md, appended to the companion bullet, verbatim:

```
All patient data in this repository — the sample messages, every
generated case, every worked example — is entirely synthetic. No
real patient data, PHI, or production message content appears
anywhere in this repository or its history.
```

And append to the `sample-oru-message` docstring in
`companion/src/ehr_testing/specimen.clj`:

```
Entirely synthetic: no real patient data or production message
content.
```

### H2. Scope note for clinical content — done (CODE)

In `manuscript/00-frontmatter/02-preface.md`, under
"## Acknowledgments and scope", append verbatim:

```
The clinical values, ranges, and codes in this book's examples are
illustrative of testing technique only; nothing in this repository
is clinical guidance or intended for diagnostic or treatment use.
HL7®, FHIR®, and the FHIR flame design are registered trademarks
of Health Level Seven International; their use here is descriptive
and does not imply endorsement.
```

### H3. Third-party skill provenance — todo (AUTHOR-LOCAL then
### CODE)

Unchanged from rev 1: for each of the six `license: MIT` skills,
identify origin; installed ones get their upstream copyright
notice restored (SKILL.md frontmatter or a root NOTICE file);
authored-here ones get "Copyright 2026 Mike Gallaher" as the MIT
holder. `find-skills` (references skills.sh / `npx skills`) is the
clearest externally-sourced candidate.

### H4. Stale "Private GitHub repo" in handoffs — done (DECIDED:
### leave)

Historical records stay as written; the next handoff records the
repo as public with fresh history.

### H5. Scanner-noise placeholder — done (CODE)

In research/, change the example `:password "secret"` to
`:password "changeme"` with the comment
`;; placeholder, not a credential`.

### H6. Pack allow-list: add `.gitignore` — done (CODE)

Add `.gitignore` to `PACK_DOTFILE_ALLOWLIST` in the Makefile and
to AUTHORS-GUIDE §8's description. `.claude/` stays deliberately
excluded.

### H7. CITATION.cff — done (CODE)

Create `CITATION.cff` at the repo root, verbatim except the
repository-url placeholder, which G2 step 7 fills in:

```
cff-version: 1.2.0
message: "If you use or cite this work, please cite it as below."
title: "EHR Testing Guide"
type: software
authors:
  - family-names: Gallaher
    given-names: Mike
year: 2026
license: Apache-2.0
repository-code: "https://github.com/REPLACE-AT-G2-STEP-7"
abstract: >-
  A practitioner's guide to writing test plans for EHR data
  transformations, with a runnable Clojure reference
  implementation. Manuscript prose CC BY-NC-ND 4.0; code
  Apache-2.0.
```

GitHub renders this as a "Cite this repository" button; the
`license` field names the code license, with the split explained
in the abstract.

### H8. Post-flip settings — todo (AUTHOR-LOCAL, after G2)

On the new public repository: enable secret scanning and push
protection (free on public repos — a standing guard). Then write
the next handoff recording: repo public, history rebuilt at
`v0-public-baseline`, license split in force — so no future
session is puzzled by a one-commit log on a project with this much
visible past.

## Completion

Order matters: CODE items (G1's file creation, H1–H2, H5–H7) and
AUTHOR-LOCAL pre-checks (G3, H3) land in the working tree first;
then G2 rebuilds history on top of the finished tree; then H8.
Close when G1–G3 and H1–H8 are done or dropped with a reason,
G2's resolution line is recorded, and the next handoff exists.
Then archive this plan per convention.
