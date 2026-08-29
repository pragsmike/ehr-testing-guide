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

**Catalytic input** — An input an operation reads but does not consume:
the configuration a mutation step consults on every run, the profile a
validator checks against — present unchanged on the far side of the box.
Drawn as a dashed wire into the side of a box. The wire whiteboard
drawings most often omit, and the one an implementation most often
hard-codes. See Chapter 35.

**Coherence square** — Two views of one object — say, two emitters
reading the same event log — together with a witness that they agree
on the content they share: both paths around the square land in the
same place. A coherence square between two views is a metamorphic
relation (Chapter 24), and an audit diagram marks each one as
witnessed or not. See Chapter 35.

**Colimit** — The categorical name for gluing a family of objects along
their declared overlaps, so that shared content exists once and each
object's remainder hangs off it. What a universal healthcare format would
actually have to be — and the identifications (which parts of two formats
"say the same thing") are where all the difficulty lives. See Chapter 11.

**ConceptMap** — A FHIR resource type recording a translation between a
source code system and a target code system as rows, each row pairing a
source code with a target code *and an explicit equivalence annotation*
(R4: `equivalent`, `wider`, `narrower`, `inexact`, and six more, read as
describing the target relative to the source; R5 renames the element to
`relationship`, with a five-code vocabulary carrying the direction in the
code names). In this book's terms: a relation made data, with a per-row
confession of how strong each correspondence is — the honesty a plain
function signature cannot carry. See Chapter 34; the dated reference
entry is in Chapter 42.

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

**Crosswalk** — A published correspondence between the codes of one
terminology and the codes of another, maintained by a party with the
standing to say what corresponds to what (e.g. NLM's SNOMED CT to
ICD-10-CM map). Despite the lookup-table connotation, the mature examples
are partial, one-to-many, and context-dependent — relations with side
conditions, not functions. See Chapter 34.

**CUI (Concept Unique Identifier)** — The UMLS Metathesaurus's unit of
linkage: one CUI gathers, from every participating vocabulary, the codes
and names judged to assert the same meaning. A shared CUI certifies
recognized synonymy, not substitutability — it links concepts without
telling you which code to emit, under what conditions, on whose
authority. See Chapter 34.

**Delta / sigma** — This book's event-sourcing vocabulary for two of its
three recurring object kinds: a **delta** is a unit of change, an event,
an increment to a history (an HL7 v2 message); a **sigma** is an
accumulated state, the fold of some history of deltas up to now (a FHIR
resource). Distinct from *delta lens* below, a different, lens-specific
sense of "delta." See Chapter 12.

**Delta lens** — A lens (see below) whose `put` operates on a change to the
source rather than on the whole source state, appropriate when the source
is itself event-shaped. See Chapter 31.

**Enrichment (versus transformation)** — Of the two kinds of box in a
process diagram: a *transformation* produces new content (a mutation
step returning a different corpus), while an *enrichment* leaves its
input's content untouched and attaches something to it — a verdict, a
score, a digest. Enrichments are safe to run twice and commute when
they write to different parts of the stamp; transformations in general
are neither. Validators and properties are enrichments. See Chapter 35.

**Equivalence annotation** — A ConceptMap row's own statement of how
strong the correspondence between its source and target codes is —
`equivalent`, `wider`, `inexact`, and so on. Test input, not decoration:
a translation check may assert code equality only where the annotation
claims it. See **ConceptMap** above, and Chapter 34.

**Erasure** — The move from an implementation diagram back to the
conceptual design it was lowered from: forget the stores, runtimes, and
infrastructure boxes, keep only the kinds on the wires. A design is
sound when lowering then erasing is the identity, `lower ⨟ erase = id`.
Where an arrow exits into a format you do not control, erasure does not
exist and the checkable surrogate is a frozen, digested copy of the
lowered bytes. See Chapter 35.

**Extension (FHIR)** — A standard-sanctioned mechanism by which any FHIR
resource may carry locally defined elements no fixed schema anticipated.
FHIR's member of the open-endedness family — parallel to v2's Z-segments
and CDA's open templates — that makes any fixed universal schema lossy.
See Chapter 11.

**Fiber** — Over a design, the set of every implementation that erases
to it: files-and-processes, table-and-queue, stream-and-cache, all one
design. A *fiber move* is a change of implementation that stays inside
the fiber — the design's witnesses are unchanged, and their staying
green is the check that it was one. Autonomy in delegated work is
freedom of movement within a declared fiber. See Chapter 35.

**Fold** — A function that consumes a sequence step by step, threading an
accumulator through, and returns the accumulator at the end — the running
balance at the bottom of a bank statement, not the list of transactions.
This book's operational picture of how a **sigma** relates to the
**deltas** that produced it: a state is a fold of events, with the
intermediate steps discarded and unrecoverable. See Chapter 12.

**Function** — A rule assigning to each input exactly one output, using
nothing but the input: no side consultation of context, no judgment
call, no "it depends." The kind of arrow integration diagrams silently
assume, and the set of promises (Chapter 34) that mature terminology
crosswalks explicitly decline to make. See also **Relation**.

**Golden case** — A single handcrafted test case: a curated input paired
with its expected output. The individual unit that makes up the *golden
corpus* (below); its expected output is a reviewed artifact carrying its
author's and spec version's blind spots, not ground truth. See Chapter 23.

**Golden corpus** — The curated, hand-built layer of a test corpus, as
opposed to the generated or mutated layers. See Chapter 23.

**Graded check** — A witness whose verdict is a grade rather than a
boolean: a rubric, a review score, a clinical judgment of acceptability.
It tests membership against a declared shape exactly as a crisp check
does, differing only in its codomain. A graded pass enters the record
with its grade, and grades only degrade through composition — a chain
is never more trustworthy than its weakest checked link. See Chapter 35.

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

**Lowering** — The move from a conceptual design to an implementation
of it: the same boxes and wires, with a store attached to every kind, a
runtime to every box, and infrastructure boxes — persist, retry,
checksum — spliced in where the substrate needs them. Lowering only
ever adds; erasure forgets what it added. See Chapter 35.

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

**Relation** — A set of (input, output) pairs with no promise that every
input appears (partiality), that any input appears only once (n-to-m),
or that which pair applies is decidable from the input alone (side
conditions). What remains of a function once those three promises are
withdrawn — and, per Chapter 34, the honest type of every mature
cross-terminology map.

**Residue** — The information the source carries that a transform drops and
that every purpose set in scope has declared it does not need. Making the
residue explicit and reviewable — rather than discovering it in production —
is what separates an engineered transform from a hopeful one. See
Chapter 22.

**Resource equation** — The one-line text form of a box in a process
diagram: its inputs, an arrow, its outputs, the box's name in brackets,
annotations in braces — `corpus × config → corpus [Mutate]
{catalytic: config}`. The equation and the picture are the same object
written two ways; the equation is the form that fits in a repository
and a diff. See Chapter 35.

**Retraction** — In this book, a lossless round-trip: transforming into a
form and back out again recovers the original exactly. The property
Chapter 11's canonical-format dream requires and Chapter 31 shows fails in
practice. See Chapter 11.

**Section (of a span)** — A rule that, given a code (or more generally a
value) at the foot of one leg of a span, picks a representative at the
foot of the other leg in a way that survives the round trip — cross
over, cross back, land where you started. Operationally: the canonical
choice function that would make a crosswalk safe to automate, with no
patient record consulted and no coder's judgment applied. Formally, a
one-sided inverse splitting one of the span's legs. Chapter 34's central
negative claim is that cross-terminology spans have no canonical
section — not for want of rows in the table, but because the two
partitions disagree about which distinctions exist.

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

**Terminology server** — A service that answers terminology questions —
most importantly membership: is this code in this value set, in this
version of this code system? — against the large, externally stewarded
terminologies (SNOMED CT, LOINC, RxNorm, UCUM) that no validator can
carry whole. What a conformance gate consults when a binding check is
not decidable from local data; run without one, an honest gate reports
"could not check" for exactly those bindings rather than pass or fail.
See Chapters 25 and 34.

**UMLS (Unified Medical Language System)** — NLM's meta-thesaurus
linking names for the same concept across nearly two hundred source
vocabularies via CUIs (see above). This book's terminology-level
universal object — in the maps-in direction only: an unmatched directory
of candidate correspondences, but a coproduct-like center with no lawful
maps back out, so a linker and cross-check, never a translator or an
equality oracle. Access requires a free license and UTS account. See
Chapter 34; the dated reference entry is in Chapter 42.

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

**Witness** — For an edge of a process diagram, the named check whose
failure is what it looks like for the edge to be wrong: a test, a
digest compared against a recorded one, a schema check at a boundary, a
rubric-scored review. Witnesses are pointwise — green on the inputs
they saw — and never theorems. The concept generalizes Chapter 24's
naturality-conditions-as-executable-tests from data transformations to
the process that produces and tests the data. See Chapter 35.

**Work signature** — The declared type of a delegated unit of work: its
*domain* (the artifacts it may change, plus the design, conventions,
and existing witnesses it reads catalytically), its *codomain* (the
declared shape of the delivered value), its *invariants* (the fiber it
must stay inside), and its *scope* (what it may not touch). The
delivered change is type-checked against the signature by a checker who
is not the doer, from the artifacts alone; on a type error the doer
halts and reports. See Chapter 35.

**Z-segment** — An HL7 v2 segment with a locally defined structure, named
with a leading Z, which the standard explicitly reserves for site-specific
content. One of the sanctioned open-endedness mechanisms (with FHIR
extensions and CDA open templates) that make any fixed universal schema
lossy. See Chapter 11.
