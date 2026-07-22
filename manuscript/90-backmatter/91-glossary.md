# Glossary

<!--
Half of this glossary is math-for-healthcare-readers (defining category-
theory terms in terms healthcare practitioners already understand) and half
is healthcare-for-math-readers (defining HL7/FHIR/terminology terms in
terms category theorists already understand). Entries are added the moment
a term first appears in ANY draft chapter, never as a final end-of-project
pass — see AUTHORS-GUIDE.md section 3.
-->

Terms are listed alphabetically. Each entry gives a short definition and,
where useful, the chapter where the term is first introduced operationally.

**Accidental contract** — A field or behavior a transform happens to carry
that no purpose set claims, which some downstream consumer nonetheless
comes to depend on — the phenomenon informally known as Hyrum's Law: with
enough consumers, every observable behavior of a system becomes
load-bearing for someone. The residue review exists to catch these before
they harden into an unintended promise. See Chapter 22.

**Agreement tolerance** — The comparison rule attached to a purpose-set
query pair, stating what counts as the two sides agreeing: exact equality,
equality after a named normalization, or membership in the same
classification (e.g. in-range vs out-of-range). Part of the question being
asked, not an implementation detail — a transform can be correct for a
query at one tolerance and not at another. See Chapter 21.

**Attestation** — The act (and the resulting content) of a party formally
signing off that a document reflects the record as of a point in time. In
this book's terms, part of what makes a C-CDA document an *attested
snapshot* rather than a plain state. See Chapter 12.

**Binding** — A declared connection from a structural field to a
terminology's codomain (e.g. OBX-2's Table 0396, or a FHIR ValueSet
binding). See Chapter 33.

**Bitemporal** — Tracking two independent time axes for a fact: valid time
(when it was true) and transaction time (when the system learned it). See
Chapter 32.

**Colimit** — The categorical name for gluing a family of objects along
their declared overlaps, so that shared content exists once and each
object's remainder hangs off it. What a universal healthcare format would
actually have to be — and the identifications (which parts of two formats
"say the same thing") are where all the difficulty lives. See Chapter 11.

**Controlled mutation** — Deliberately injecting a single, documented defect
into a known-good test case (golden or generated) so that a specific test is
expected to fail, confirming the suite can catch that defect class. Each
mutation is tagged with the purpose-set question it should break; a mutation
that fails to break its target reveals a missing or too-weak property. See
Chapter 23.

**Coproduct** — The categorical name for a disjoint union: a pile of
objects with an inclusion arrow from each part, and nothing identifying
content the parts share. The shape most sketches of a universal healthcare
format actually have — a place for everything, no canonical way back out,
and no account of sameness. See Chapter 11.

**Correctness lattice** — The partial order of correctness that appears when
one transform is judged against several recipients at once: the same
transform can be correct for one recipient's purpose set and incorrect for
another's at the same time, so "correct" is not a single verdict but a
position in a lattice indexed by recipient. See Chapter 22.

**Criticality tier** — A classification of a purpose-set question by the
consequence of getting it wrong — safety-critical, decision-affecting, or
cosmetic — driving corpus depth (Chapter 23) and gate placement
(Chapter 25).

**Delta / sigma** — This book's event-sourcing vocabulary for two of its
three recurring object kinds: a **delta** is a unit of change, an event,
an increment to a history (an HL7 v2 message); a **sigma** is an
accumulated state, the fold of some history of deltas up to now (a FHIR
resource). Distinct from *delta lens* below, a different, lens-specific
sense of "delta." See Chapter 12.

**Delta lens** — A lens (see below) whose `put` operates on a change to the
source rather than on the whole source state, appropriate when the source
is itself event-shaped. See Chapter 31.

**Extension (FHIR)** — A standard-sanctioned mechanism by which any FHIR
resource may carry locally defined elements no fixed schema anticipated.
FHIR's member of the open-endedness family — parallel to v2's Z-segments
and CDA's open templates — that makes any fixed universal schema lossy.
See Chapter 11.

**Fold** — A function that consumes a sequence step by step, threading an
accumulator through, and returns the accumulator at the end — the running
balance at the bottom of a bank statement, not the list of transactions.
This book's operational picture of how a **sigma** relates to the
**deltas** that produced it: a state is a fold of events, with the
intermediate steps discarded and unrecoverable. See Chapter 12.

**Golden case** — A single handcrafted test case: a curated input paired
with its expected output. The individual unit that makes up the *golden
corpus* (below); its expected output is a reviewed artifact carrying its
author's and spec version's blind spots, not ground truth. See Chapter 23.

**Golden corpus** — The curated, hand-built layer of a test corpus, as
opposed to the generated or mutated layers. See Chapter 23.

**Hub architecture** — An integration architecture with one designated
representation at the center and every other format translated into and
out of it, collapsing n² point-to-point translators to 2n. Correct only if
each spoke's round trip is a retraction — the condition Chapter 11 shows
fails for healthcare formats.

**Join / meet** — Over the lattice of purpose sets ordered by demand, the
**join** of two purpose sets is their union — every query either
requires, at the stricter tolerance where both require it, serving both
recipients at once — and the **meet** is their intersection — the
queries *both* require, each at the stricter of the two tolerances. The
meet is the shared core Part I's span glues representations along, seen
from the recipients' side rather than the formats' side. See Chapter 22.

**Lens** — A pair of functions, `get` and `put`, relating a source
structure to a view of it, obeying round-trip laws (GetPut, PutGet). This
book's honest replacement for the lossless retraction that Chapter 11 shows
cannot exist for healthcare formats. See Chapter 31.

**Metamorphic relation** — A test relation between outputs of *related*
inputs (e.g. permuted or filtered inputs), used when no independent oracle
exists for a single input's expected output. See Chapter 24.

**Naturality (naturality condition)** — The requirement that two
routes from the same start to the same end agree: transforming and
then asking a question yields the same answer as asking and then
transforming, for every input at once. Chapter 21's commuting
triangle is one instance; Chapter 24's central move is that a
metamorphic relation is exactly a naturality condition written as
an executable test. See Chapter 24.

**Observation (as query)** — Within a purpose set, one question expressed as
a paired query: `q` run against the transform's output and `q-source` run
against its input. The transform is correct for that observation when
`q(f(x))` and `q-source(x)` agree. Distinct from the FHIR **Observation**
resource, which is the specimen's transform target. See Chapter 21.

**Observational equivalence** — Two things are observationally equivalent
under a set of queries if every query in that set gives the same answer on
both. The basis for this book's definition of correctness. See Chapter 21.

**Open template** — A CDA template that constrains what a document section
must contain while permitting content beyond its constraints — the
document-standard member of the open-endedness family (with FHIR
extensions and v2 Z-segments) that defeats any fixed universal schema. See
Chapter 11.

**Oracle problem** — The difficulty of knowing the correct expected output
for a given input independently of the transform under test. Where no
independent oracle exists, correctness is asserted through metamorphic
relations rather than by comparison to a precomputed answer. See Chapter 24.

**Profile** — A standard-conformant constraint on another standard (e.g.
US Core constrains FHIR, C-CDA constrains CDA), narrowing what's allowed
without introducing a new base standard. See Chapter 13.

**Purpose set** — The specific set of queries a downstream recipient will
actually run against transformed data; this book's definition of what a
transformation must be correct *for*. See Chapter 21.

**q / q-source** — In a purpose-set query pair, `q` is the query the
recipient runs against the transform's output and `q-source` is the
corresponding query run against the source; the transform is correct for
the pair when `q` composed with the transform agrees with `q-source` on
every input, at the pair's agreement tolerance. See Chapter 21.

**Residue** — The information the source carries that a transform drops and
that every purpose set in scope has declared it does not need. Making the
residue explicit and reviewable — rather than discovering it in production —
is what separates an engineered transform from a hopeful one. See
Chapter 22.

**Retraction** — In this book, a lossless round-trip: transforming into a
form and back out again recovers the original exactly. The property
Chapter 11's canonical-format dream requires and Chapter 31 shows fails in
practice. See Chapter 11.

**Seed (reproducibility)** — The starting number that determines a
generator's pseudo-random sequence and therefore its entire output
population. Recorded alongside generator and module versions as
reproducibility metadata, because an unseeded corpus is a different corpus
each time it is built. See Chapter 23.

**Span** — A pair of arrows out of a common object into two others, used
in Chapter 34 to model terminology maps that are not simple functions in
either direction.

**Synthetic population** — A realistic-but-fake patient dataset produced by
a generator (in this book, Synthea) from a model of disease and lifecycle,
supplying the volume and co-variation of real data without containing any
real patient. Valid by construction, so defects must be injected afterward
by controlled mutation. See Chapter 23; the tool's dated entry is in
Chapter 43.

**Universal object** — In this book's use: the imagined single
representation U that every format maps into and out of again coherently
— the hub architecture's center. The Pandoc dream is the claim that such
a U exists with every healthcare format as a retract of it; Chapter 11
shows why none does, and Part III names the honest replacement, which is
not a format at all but an event log (Chapter 32).

**Vacuous specification** — A correctness standard so weak that no real
transformation can fail it (e.g. "the message parses and the output
validates against its schema"); useless as a standard because it draws no
distinction between an acceptable transform and a dangerous one. The
opposite-direction failure — a standard so strong every transform fails it
— is equally useless; a purpose set is built to avoid both. See Chapter 21.

**Z-segment** — An HL7 v2 segment with a locally defined structure, named
with a leading Z, which the standard explicitly reserves for site-specific
content. One of the sanctioned open-endedness mechanisms (with FHIR
extensions and CDA open templates) that make any fixed universal schema
lossy. See Chapter 11.
