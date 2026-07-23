# Architecture / Authoring Decision Records

<!-- Each record: Context / Decision / Alternatives rejected / Consequence.
     Status: Accepted unless noted. These capture WHY, where AUTHORS-GUIDE.md
     captures WHAT-TO-DO. All records are kept together in this single file
     (notes/ADRs.md); they are intentionally not fanned out into separate
     per-record files. Do not silently revert an Accepted decision; supersede
     it with a new numbered record. -->

## ADR-0001 — Monorepo: manuscript + companion together
**Context.** A book of markdown chapters plus a runnable Clojure reference
implementation whose listings appear in the book.
**Decision.** One repository, `manuscript/` and `companion/` side by side.
**Rejected.** Two repos. Cleaner boundaries, but the two must evolve in
lockstep as the ORU→Observation specimen threads through the chapters;
cross-referencing across repos is friction with no offsetting gain at this
scale.
**Consequence.** CI must distinguish "build the book" from "run the tests."
Split is reconsiderable if the companion grows independent life.

---
## ADR-0002 — Canonical object is a bitemporal event log, not a super-format
**Context.** The book needs a *U* through which transformations factor.
**Decision.** *U* is an append-only bitemporal event log; every format is a
view or an ingestion. (Framing decision — load-bearing for Parts I & III.)
**Rejected.** A pandoc-style universal document schema. It cannot satisfy the
round-trip laws and hides loss inside translations; it also mismodels events,
states, and attested snapshots as one kind of thing.
**Consequence.** Chapters 12 and 32 depend on this. Do not recast *U* as a
format in later drafts.

---
## ADR-0003 — Arrows are lenses and spans, not isomorphisms
**Context.** Real transformations are lossy.
**Decision.** Model transformations as lenses (asymmetric, with get/put
laws) and spans (shared core), not invertible maps. (Framing decision.)
**Rejected.** Isomorphisms / lossless round-trips as the correctness bar —
unreachable in practice; asserting it produces dishonest tests.
**Consequence.** Correctness is defined observationally (ADR-0004), not by
round-trip.

---
## ADR-0004 — Correctness is observational, relative to purpose sets
**Context.** "Correct" must be defined for lossy maps.
**Decision.** f is correct for a query q iff q∘f = q_source; the recipient's
purpose set is the spec; correctness is a lattice; the residue is a reviewed
artifact. (Framing decision — the book's keystone.)
**Rejected.** Absolute/format-level correctness; single-boolean verdicts.
**Consequence.** Chapter 21 is the keystone; the whole method (Part II)
derives from this.

---
## ADR-0005 — Four parts, with Reference as its own part
**Context.** Readers will both *read through* and *consult* the work.
**Decision.** Part I Problem, II Method, III Structure, IV Reference, plus
back matter. Reference (standards, terminology, tools) is a full part, not an
appendix.
**Rejected.** Folding reference material into narrative chapters. It would be
unfindable for the consult-mode reader and would rot inside prose.
**Consequence.** Part IV lives in separate files with uniform entry schema
and per-entry verification dates (ADR-0009); it keeps receiving commits after
I–III stabilize.

---
## ADR-0006 — Keystone-first writing order
**Context.** Limited energy; risk of writing setup chapters around a spine
that turns out wrong.
**Decision.** Order: test-plan templates (§94) → Part II → Part III → Part I
& preface last. Draft ch 21 before its dependents.
**Rejected.** Linear 1→N drafting. Books die at chapter 4; the setup chapters
are also the cheapest to write once the spine is proven.
**Consequence.** Introductions are written knowing what they introduce.

---
## ADR-0007 — Companion is a runnable reference implementation; no pseudocode
**Context.** The book's central claim is that correctness properties are
executable.
**Decision.** Every code listing exists in `companion/` and is covered by a
test. No pseudocode in the manuscript.
**Rejected.** Illustrative-only snippets. They rot and undercut the book's
own thesis.
**Consequence.** `make check` gates pushes; the specimen must stay in sync
across every chapter that cites it.

---
## ADR-0008 — File naming encodes reading order
**Context.** Pandoc concatenates by sort order.
**Decision.** `NN-slug.md` within `NN-part/` directories; lexical sort =
reading order.
**Rejected.** Arbitrary names with an external manifest. One more thing to
drift out of sync.
**Consequence.** Renumbering a chapter is a file rename, visible in git.

---
## ADR-0009 — Uniform reference-entry schema, date-stamped
**Context.** Reference material must be consultable and it rots.
**Decision.** Every Part IV entry uses `templates/reference-entry.md`; every
entry carries a verification date.
**Rejected.** Freeform entries. They become non-comparable and undatable.
**Consequence.** Never write a Part IV entry freehand (AUTHORS-GUIDE §4).

---
## ADR-0010 — Dual audience via introduce-twice
**Context.** Two audiences with complementary gaps (CT-fluent vs
domain-fluent).
**Decision.** Every categorical term and every healthcare term is introduced
twice in quick succession — once operationally, once by name. Glossary serves
both directions.
**Rejected.** Writing for both simultaneously in every sentence — condescends
to each reader half the time.
**Consequence.** A per-clause cost accepted throughout; glossary is
half math-for-clinicians, half healthcare-for-mathematicians.

---
## ADR-0011 — AGENTS.md as the agent-instruction convention
**Context.** Multiple agent tools may read the repo.
**Decision.** AGENTS.md is canonical; CLAUDE.md points to it.
**Rejected.** CLAUDE.md-only. Non-portable across tools.
**Consequence.** Agents read AGENTS.md first.

---
## ADR-0012 — WSL-only git on Windows
**Context.** Mixed-platform commits cause CRLF/LF churn and exec-bit flips.
**Decision.** All git operations run under WSL (via `wsl.exe bash -lc`),
enforced by a pre-commit hook; `.gitattributes` sets `eol=lf`. Hard-won from
prior repos.
**Rejected.** Native Windows git with `.gitattributes` alone — sufficient in
most cases but not belt-and-suspenders enough for this author's experience.
**Consequence.** Code/Cowork must commit through WSL; autonomous agents leave
commits to the human.

---
## ADR-0013 — Claims register gains an external-facts table
**Context.** Two false factual claims (Synthea HL7 v2 export; HAPI HL7v2
licensing) survived in the manuscript and bundled research because the
claims register tracks conceptual insights and drafting coverage, not
externally verifiable facts; reference-chapter "Entry last verified" dates
cover register entries but not facts asserted in prose or bundled
documents. Both errors were found by external component-selection
research (2026-07-22), not by the repo's own verification sweeps.
**Decision.** `notes/claims-register.md` gains a second table, "External
factual claims" (F-rows), for load-bearing, externally verifiable
assertions about tools, licenses, and ecosystem capabilities — each row
carrying where the claim is asserted, its evidence, a last-verified date,
and a status. The C-table's purpose and format are unchanged.
**Rejected.** (a) Folding fact-rows into the C-table — muddies a checklist
that tracks drafting coverage, not truth. (b) Relying solely on
reference-chapter "Entry last verified" dates — demonstrated insufficient:
the false claims lived outside the reference chapters. (c) A separate new
file — the register is already the place agents and sessions consult; a
second table there is discoverable, a fourth notes file is not.
**Consequence.** Verification sweeps now have a definite worklist; new
load-bearing tool facts asserted anywhere in the repo should get an F-row.

---
## ADR-0014 — Aside vocabulary exempt from introduce-twice
**Context.** ADR-0010 requires every categorical and healthcare term be
introduced twice — operationally, then by name. Chapter 31 adds an aside
addressed solely to the CT-fluent reader (closure operator, adjunction,
triangle identity, Galois connection); introducing these operationally
would defeat the aside's purpose and toll the domain-fluent reader the
aside exists to spare.
**Decision.** Asides explicitly addressed to the already-fluent reader are
exempt from introduce-twice and from the glossary-at-first-use rule.
Scope is the aside's own text only: any term that escapes into main prose
re-acquires both obligations at that point. Asides must be visibly
set off and name their audience.
**Rejected.** (a) Introduce-twice inside asides — pays ADR-0010's
per-clause cost precisely where its dual-audience rationale doesn't
apply. (b) Per-aside ratification notes in chapter text — leaves the
policy invisible to future integration sessions reading ADRs.md, which
would see ADR-0010 as unqualified and "fix" compliant asides.
**Consequence.** ADR-0010 is narrowed, not superseded. The ch-31 in-text
ratification flag is removed once this ADR lands. Glossary continues to
serve main prose only; aside-only terms get literature pointers instead.
