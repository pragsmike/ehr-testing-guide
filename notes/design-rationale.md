# Design Rationale: Why This Book Is Shaped As It Is

<!-- Reasoning-of-record. Not part of the Pandoc build. Harvest into
     ch 11 (The Pandoc Dream) and the openings of Parts II and III as those
     chapters are drafted; until then this is the canonical statement of the
     argument the thesis paragraphs only assert. -->

## The problem that starts everything

A hospital runs many systems that must exchange records, and the naive
architecture connects each to each: with *n* representations, that is on the
order of *n²* translators to write and maintain. Every integration engineer
eventually reaches for the same escape — a single canonical format *U* that
every representation maps into and out of, collapsing *n²* translators into
*2n*. Pandoc is the familiar exemplar: convert everything through one
document model. FHIR itself was, in part, an attempt to be this *U* for
healthcare.

Stated categorically, the dream is a **universal object**: for every
representation *A*, an arrow into *U* and an arrow back, such that going in
and coming out returns exactly what you started with. That round-trip
condition makes each *A* a **retract** of *U* — the precise categorical
statement of "converts losslessly." If it held, every transformation *A → B*
would factor through *U*, and the hub architecture would be not merely
convenient but *correct*.

## Why the dream, taken literally, is the wrong shape

Two things go wrong, and both are informative rather than fatal.

First, *U* is often imagined as the *union* of all representations — but a
union (categorically, a coproduct) gives you maps *out of* each part and no
canonical maps *back*, and no way to identify the content two formats share.
What the hub actually needs is the opposite: a way to *glue* representations
along the parts that mean the same thing. That gluing is a colimit over the
diagram of semantic overlaps, and the gluing is where all the difficulty
lives, because healthcare standards disagree not merely in syntax but in how
they carve up clinical reality.

Second, and more practically, the round-trip laws simply fail. Pandoc is
lossy and survives only via escape hatches (raw blocks). Healthcare is
worse: FHIR extensions, HL7 v2 Z-segments, and CDA's open templates mean any
concrete instance can carry content outside any fixed schema. The mapping
literature — the V2-to-FHIR guide, the C-CDA↔FHIR guide — is in effect a
catalog of where the retraction breaks.

The productive response is not to abandon *U* but to **weaken the arrows**.
A round-trip law stated asymmetrically — a `get` that reads a view and a
`put` that writes one back, obeying "read-after-write" and
"write-after-read" laws — is exactly a **lens**, and lenses tolerate that
the map back is partial or forgetful. The bidirectional-transformation (BX)
literature is the serious mathematics of precisely this problem. The other
natural weakening is a **span**: instead of a direct arrow *A → B*, a shared
core *D* with projections to both sides, so that what the two formats have in
common is named explicitly and what they don't is visibly absent.

## The objects are not the same kind of thing

The deepest reason a single super-format cannot work is that the
representations are not three encodings of one kind of value. An HL7 v2
message is an **event** — an admission, an order, a result. A FHIR resource
is a **state** — the current value of something. A CDA/C-CDA document is an
**attested snapshot** — a state frozen at a moment and *signed*, where the
signature is content, not metadata. And a state is a *fold* of an event
stream: the current state is what you get by replaying the events. So v2 and
FHIR are not two dialects; one is (roughly) the derivative and the other the
integral. Using the delta/sigma vocabulary of event sourcing: v2 messages
are the deltas, FHIR resources the accumulated sigma.

This reframes what a "v2 → FHIR mapping" even is. It is not a translation
between formats; it is the *action of a delta on a state*. That is why the
V2-to-FHIR guide struggles wherever a message does not determine a state
change without additional context — the difficulty is not incidental, it is
the category reporting that the arrow does not exist at that type.

## The kind-mismatches in detail

The previous section named three kinds — event, state, attested snapshot —
and asserted they are not dialects. This section pays that assertion off, one
reading at a time. Each is a specific categorical diagnosis of a specific
place where the standards resist the naive hub picture, and each is stated at
the strength the design work reached: the point is never the generic
"transformations lose information," but the particular structural reason a
given arrow misbehaves — or fails to exist at all.

**A v2→FHIR mapping is a delta acting on a state, and sometimes the arrow
simply is not there.** Recall that a v2 message is an event — a *delta*, a
described change — and a FHIR resource is a *state* — a *sigma*, the
accumulated fold of every delta so far. Grant that, and a "v2→FHIR mapping"
stops being a translation from one format into another and becomes the
*action of a delta on a state*: apply this change to the world as currently
recorded, then read off the new value. The consequence is sharp and worth
stating without hedging. Applying a delta requires a state to apply it to.
Where a message does not determine its state change without additional
context — a result that is only meaningful against a prior order, an update
that presupposes a record it does not itself carry — there is no
state-independent function from that message to a resource. The arrow does
not exist *at that type*. This is not "the mapping is hard." It is the
non-existence of a total function, reported as a typed fact. The V2-to-FHIR
implementation guide's most tortured passages cluster at exactly these
points, and that clustering is not a failure of the guide's authors; it is
the category faithfully reporting that nothing total lives there for them to
write down.

**C-CDA's narrative and entries are a span with unchecked legs.** A *span* —
a shared core with one projection to each side — is the right picture for a
C-CDA section, but a defective instance of it. Each section carries two
things: authoritative human-readable **narrative**, and optional
machine-readable **entries** (coded observations structured to be parsed).
The standard is explicit about the asymmetry — the narrative is the legally
authoritative content, the thing the clinician is deemed to have attested,
and the entries are a derived convenience encoding. Categorically this is a
span whose apex is the section's *intended clinical meaning*, projecting down
one leg to narrative and the other to entries. The defect is that nothing in
CDA validation checks that the two legs agree; the legs are *unchecked*. A
fully conformant document can carry narrative asserting one thing and entries
asserting another, or entries that quietly omit what the narrative states.
Every transform to FHIR reads only the entries leg, because that is the
machine-readable one, and so silently discards the authoritative narrative
leg. A round-trip law cannot hold here even in principle: the information
ordering is narrative ≥ entries, and the extraction projects through the
*smaller* of the two. What is lost is not incidental detail — it is precisely
the legally salient part, the content that was actually attested.

**Regenerating a C-CDA is a new attestation, not an inverse.** It is tempting
to treat extraction (a document broken into discrete FHIR resources) and
assembly (resources gathered back into a document) as a *lens pair* — a `get`
and a `put` that undo one another under the round-trip laws. They are not,
and the reason is provenance, not fidelity. Assembly is an *authorship act*:
a document must be attested, so producing one mints new provenance — a new
author, a new signing time, a new "as of" — and those signatures are content,
not metadata (the attested-snapshot point from the previous section). A
C-CDA regenerated from FHIR resources, even with clinically identical content
down to the last coded value, is therefore a *different object* than the
document those resources were extracted from: it bears a different
attestation. The two directions do not even share a type — extraction is
document→resources, assembly is resources→(newly attested) document — so they
are two independent arrows, not a map and its inverse. The general lesson,
and the one the correctness question actually cares about, is that some
"transformations" in healthcare are provenance-minting authorship events;
modeling them as plain invertible arrows erases exactly the distinction —
who attested this, and when — that decides whether a document may be relied
upon.

**OBX-2 makes OBX-5 a dependent sum.** In an HL7 v2 OBX ("observation/result")
segment, the field OBX-2 carries a datatype code — NM for a number, CWE for a
coded entry, ST for a string, SN for a structured numeric, and so on — and
that code declares how to read the value sitting in OBX-5. Operationally: you
cannot know the type of OBX-5 until you have read the runtime value of OBX-2.
A type selected at runtime by a tag, where the tag's value determines which
interpretation the payload receives, is a *dependent sum* — Σ(t : Datatype)
⟦t⟧, the disjoint union over every datatype t of the values that t denotes —
a tagged union chosen dynamically. That construction, familiar from dependent
type theory, has been sitting inside a 1989 pipe-delimited wire format for
decades. FHIR did not escape it; it made it explicit. FHIR's `value[x]`
polymorphism — `valueQuantity`, `valueCodeableConcept`, `valueString` — is
the same dependent sum, now spelled out in a schema with the tag fused into
the field name. There is a second, subtler point. OBX-3 conventionally
carries a LOINC code naming the *question* the observation answers, and that
question implies an expected datatype; OBX-2 declares the *actual* datatype of
the answer. The two are supposed to cohere, and nothing enforces that they do
— another span with unchecked legs, the same shape as the C-CDA case above,
one leg the question's implied type and the other the tag's declared type.

**Terminology maps are spans with no section.** SNOMED CT, LOINC, ICD, and
RxNorm each carve clinical reality into concepts, and they carve it
*differently on purpose*: SNOMED for clinical meaning, ICD for billing and
statistical classification, LOINC for the identities of observations, RxNorm
for medications. These are not competing attempts at a single partition; they
are different partitions chosen for different jobs. An official "map" between
two of them — a crosswalk — is therefore partial, n-to-m, and
context-dependent: a *relation* carrying side conditions, not a function. The
categorical picture is again a span, but here the diagnosis is about what the
span *lacks*. Both terminologies project down from the same apex — clinical
reality — each along a leg; but neither leg factors through the other, which
is to say there is no *section*: no canonical one-sided inverse that would let
you cross from one vocabulary into the other and back without making a choice.
(A section, operationally, is a rule for picking a representative on the far
side that survives the round trip; here no such rule is canonical.) This is
why "just use the standard crosswalk" underdelivers as engineering advice:
the residue — the region where the vocabularies' worldviews genuinely diverge
— is not a gap a more complete table would fill. It is a place where no
function exists to be tabulated, and no amount of tooling makes the relation
total.

Two threads run through these five. The C-CDA narrative/entries split and the
OBX-2/OBX-3 coherence gap are the *same* defect in two different standards —
a span whose legs are never checked for agreement, so a fully conformant
instance can be internally contradictory. The narrative/entries split and the
regeneration-as-attestation point are both consequences of C-CDA being a
*document* — a thing that is authored and attested rather than merely
computed. And the delta-on-state reading and the terminology span are the
same failure at two different layers: an arrow that does not exist as a total
function, once because a delta needs a state it does not carry, once because
two vocabularies share no section. These are exactly the phenomena the method
of Part II must be built to *detect* rather than assume away, and each is
formalized where it lives: the delta-on-state reading in Chapter 12, the
spans-with-unchecked-legs and the failed round trip in Chapter 31, the
dependent sum of OBX in Chapter 33, and the sectionless terminology span in
Chapter 34.

## What U actually is: the event log

If states are folds of events, the honest canonical object is not a
super-format at all — it is the **event log itself**: an append-only,
**bitemporal** record of everything ever asserted, tracking both *valid
time* (when something was true in the world) and *transaction time* (when
the system was told). Every concrete representation is then either a **view**
of the log (a projection, a query) or an **ingestion** into it. "All arrows
factor through *U*" now holds by construction, and — crucially — lossiness
is relocated from *hidden inside translations* to *honestly visible in the
views*. This is the CQRS / event-sourcing reading, and it is what lets *U*
faithfully represent what each source system asserted and when, which is the
information a super-format would silently discard.

Attested documents slot in cleanly under this reading. Receiving a C-CDA is
an event ("at time *t*, organization *O* asserted this snapshot"); the
document's clinical claims are assertions with valid-time in the patient's
history and transaction-time at ingestion; and *generating* a C-CDA is a
query against the log followed by a new attestation event appended back to
it. Documents stop being awkward once they are things that *happen* rather
than things that *are*.

## What "correct" can mean once loss is unavoidable

If every arrow out of a real representation is lossy, "correct" cannot mean
"lossless" or "invertible" — those bars are unreachable. The move that
rescues the notion is to make correctness **relative to an observation**.
You do not ask whether *f : A → B* is correct absolutely; you ask whether it
preserves the answers to the questions the recipient will actually ask.
Formally: for a question there is a query *q* on the target and a
corresponding query *q_source* on the source, and *f* is correct *for that
question* when asking after transforming equals asking before —
*q ∘ f = q_source*. This is **observational equivalence**, borrowed from
programming-language semantics, and it dissolves the paradox: a lossy map is
fine precisely when what it loses is invisible to every question in scope.

The set of questions a recipient will ask is their **purpose set**, and the
purpose set *is* the specification. Two consequences follow immediately and
shape the whole method. First, correctness is not one boolean but a
**lattice**: a transform can be correct for medication reconciliation and
incorrect for quality reporting at the same time, and honest tooling reports
the vector, not a verdict. Second, the gap between "correct for every
question in scope" and "lossless" is a concrete, enumerable artifact — the
**residue**, the information the purposes have declared they do not need —
and making that residue explicit and reviewable is exactly what separates
engineering from hope.

Everything in Part II (the method) operationalizes this definition;
everything in Part III (the structure) explains why the phenomena that force
the definition — lossiness, kind-mismatch, non-functional terminology maps —
have the categorical shapes they do. This document is the through-line those
parts elaborate.
