# Deltas, States, and Signatures

> **Thesis:** HL7 v2 messages, FHIR resources, and C-CDA documents are not
> three encodings of one underlying clinical fact — they are three
> different *kinds* of object. A v2 message is an event: a delta, something
> that happened. A FHIR resource is a current state: the fold of some
> history of deltas up to now. A C-CDA document is an attested snapshot: a
> state frozen at a point in time and signed, where the attestation is
> itself part of the content, not metadata bolted on afterward. Because
> these are different kinds of object, a transformation between them is
> never a format conversion in the way converting CSV to JSON is; it is a
> conversion between kinds, and that is a strictly harder — and different —
> problem.

<!-- Sync: no code listings, no companion obligation (deliberate — Part I
     prose). Register: flip C4, C5, C8 to "in ch 12". Glossary: verify
     delta/sigma (add as paired entry if absent), fold, attestation.
     Acronyms: verify EVN, ADT. Cross-chapter: ch 13's "ahead of the curve"
     sentence needs one-line reconciliation now that 12 precedes it and
     makes the kind-distinction explicitly (in debt list). -->

## Events: v2 as delta

An HL7 v2 message is a sentence in the past tense. Its header segment —
MSH — says who is speaking, to whom, and when; its message type says what
kind of thing is being reported: ADT^A01, a patient was admitted; ORM^O01,
an order was placed; ORU^R01, a result came back. Admission messages even
carry a dedicated EVN segment whose whole job is to say *what occurred and
when it occurred*, as distinct from when the message about it was sent.
The grammar is consistent: a v2 message does not assert "the following is
true"; it asserts "the following *happened*." In the vocabulary of
event-sourced systems — which this book borrows because it fits exactly —
each message is a **delta**: a unit of change, an increment to a history,
as opposed to a **sigma**, an accumulated state.

Three properties follow from delta-hood, and all three matter for
testing. First, a delta's meaning is *positional*: what an A08
(update-patient-information) means depends on what it is updating, and
receiving it twice is not the same as receiving it once unless someone has
done deliberate work to make it so. Second, deltas do not rewrite; they
accumulate. When a lab corrects a result, no message reaches back and
edits the original ORU — a *new* ORU arrives carrying a corrected status,
and the correction is one more event in the stream. The history only
grows. Third, every delta carries at least two times — when the thing
happened and when the message reporting it was sent — and the gap between
them is not noise but information, the seed of the bitemporal treatment
in Chapter 32.

None of this makes v2 primitive. It makes v2 *committed to a worldview*:
the world is a stream of occurrences, and the standard's job is to report
them promptly. What the worldview omits — any single place where the
accumulated present is written down — is precisely what the next kind of
object exists to provide.

## States: FHIR as fold

A FHIR resource speaks in the present tense. An Observation says what was
observed; a MedicationRequest says what is currently prescribed; a Patient
says who and where the patient is *now*. Fetch the resource twice and you
get the current answer twice; when reality changes, the resource is
updated in place (with a version number ticking in its metadata), and the
previous answer is no longer what the server gives you.

The relationship between this kind of object and the last one is not
rivalry but arithmetic. Take an empty record and a history of deltas —
admitted, transferred, corrected, discharged — and apply each in order,
letting every event update a running value. The final value is the current
state. Functional programmers know this operation as a **fold**: a
function that consumes a sequence step by step, threading an accumulator
through, and returns the accumulator at the end. Everyone else knows it as
the running balance at the bottom of a bank statement: the transactions
are the deltas, the balance is the fold. A FHIR resource is best
understood as exactly this — the balance line. Somewhere behind it is a
history of occurrences that produced it; the resource is the history
*folded*, with the folding already done and the intermediate steps
discarded.

Discarded is the operative word. A fold is not invertible: many different
transaction histories produce the same closing balance, and nothing in
the balance tells you which history was yours. FHIR acknowledges the
history's existence — servers may offer version-history endpoints,
Provenance resources can annotate lineage — but the *primary* object,
the thing exchanged and queried, is the present-tense value with its past
compressed out. That design is not a defect; it is what makes FHIR
pleasant to build applications against, since most consumers want the
balance, not the statement. But it fixes the kind of the object: a FHIR
resource is a sigma, and everything a sigma cannot carry — order,
causality, the difference between two paths to the same present — is
structurally absent, not merely unmapped.

## Attested snapshots: C-CDA as signed state

The third kind looks, at first, like a special case of the second: a
C-CDA document also reports states — a problem list, a medication list, a
set of results — as of some moment. The temptation is to file it as
"FHIR before FHIR," a clunkier serialization of the same present-tense
content. The temptation should be resisted, because the document adds two
things that change its kind rather than its format.

The first is *frozenness*. A document is a snapshot as of its stated
time, and it does not update. Where a FHIR resource revises in place as
reality moves, a signed document is immutable: corrections arrive as new
documents — addenda, replacements — that stand *beside* the original in
the record, never as edits *to* it. In this one respect the document
behaves like a delta: it is appended to a history rather than mutated.
What gets appended, though, is not an increment but an entire frozen
sigma.

The second, and decisive, addition is the **attestation**. A C-CDA
document names a human or an organization who vouched for its content at
a stated time, typically with legal weight — this is a record someone
*signed*. And the attestation is not metadata about the clinical payload;
it is part of what the document *is*. Two documents with byte-identical
problem lists, attested by different clinicians or at different times,
are different objects — different assertions, different legal facts,
different entries in any honest record — in a way that two FHIR
Observations with identical content and different server timestamps are
not. Strip the attestation and you have not simplified the document; you
have destroyed the property that makes it a document. (The document's
other famous structural quirk — an authoritative human-readable narrative
paired with optional machine-readable entries, with nothing enforcing
their agreement — is deferred to Chapter 31, where it becomes the
canonical example of an unclosable information leak.)

So the third kind is a compound: a state, plus a time, plus a signature —
frozen together. Call it an attested snapshot, and note the type: it
contains a sigma but is not one.

## Three kinds, not three encodings

Converting CSV to JSON is a transposition: both sides are serializations
of the same kind of thing — a value — and the conversion changes the
spelling while the value holds still. The transformations of this book
are not like that, and the difference can now be said precisely. A delta
and a sigma are related as increment to accumulation — as a bank
transaction to a balance, as (for readers who want the calculus
metaphor) a derivative to an integral. A sigma and an attested snapshot
are related as a value to a *signed, dated assertion of* that value. No
amount of format engineering converts between these, because the
difference is not in the spelling but in what kind of claim each object
makes about the world: *this happened*; *this is the case*; *I vouched,
at this moment, that this was the case*.

For a test plan, this is the organizing distinction — more organizing
than the wire formats that usually get top billing. Organize a test plan
by format and you get a parser section, a mapper section, a serializer
section: the anatomy of the code. Organize it by kind and you get the
anatomy of the *problem*: which properties are even expressible depends
on the kind pair, before a single field mapping is examined. Fold
determinism — same events, same state — is a meaningful property for a
delta-to-sigma transformation and meaningless for a transposition.
Round-trip laws are natural for transpositions and structurally
unavailable for folds, which discard by design. Attestation demands a
human gate no property can automate. Chapter 24's property catalog is
organized by exactly this taxonomy, and the taxonomy is why: the kind
pair of a transformation is the first fact a test plan should record
about it, because everything else in the plan inherits its shape from
that fact.

## Consequences for transformation

Walk the kind pairs, and each turns out to demand something a format
conversion never would.

**Delta to sigma** — the v2-to-FHIR direction, the book's specimen — is
not translation but *application*: an event acting on a state. Sometimes
the event carries a complete fact within itself, and the action degrades
gracefully into construction. The specimen is chosen to live in exactly
this happy case: an ORU result message carries the observation's
identity, value, units, status, and subject all within its own segments,
so a standalone Observation can be built from the message alone — which
is what makes the specimen tractable enough to thread through a book.
But the same message type shows where the grace runs out. A corrected
result (status C) is an action on a *prior* result the message does not
contain. The clinical meaning of a glucose value can depend on the order
context — the specimen's own lineage orders a *fasting* glucose and
results a generic one, and only the order knows the difference. An ADT
merge message acts on an identity state spread across every record the
patient has. In each of these, the message alone does not determine the
resulting state: the arrow from message to resource, as a total function
on messages, *does not exist* — and the struggles of the official
v2-to-FHIR mapping guide at precisely these points are not editorial
shortcomings but the structure showing through. A test plan that knows
this reserves its context-free properties for the context-free cases and
demands stateful fixtures — a prior result, an order, an identity
history — for the rest, instead of discovering the difference in
production.

**Sigma to delta** — extracting events from a state — is
underdetermined in the opposite way: a fold has forgotten its path, so
emitting a history from a state is not recovery but *invention* unless
there is something to difference against. The honest versions of this
arrow are all differential: state now versus state before, emitted as
the delta between them. A test plan meeting a state-to-message interface
should immediately ask *against what baseline*, because an answer of
"none" means the deltas are being minted, not derived.

**Sigma to attested snapshot** — assembling a C-CDA from FHIR resources
— looks like serialization and is actually *authorship*. The content can
be queried out of the resources, but the signature cannot: someone must
attest, at a moment, and that act mints new provenance. Two consequences
follow with full force. The assembled document is a *new object* — even
with clinical content identical to some prior document, it bears a
different attestation, and no honest equality check identifies them. And
the reverse arrow — extracting resources from a document — is not this
arrow's inverse, not because information dribbles away in transit, but
because the two directions *do not share a type*: one is a query
followed by an authorship act; the other is a projection that discards
the authorship (and, per Chapter 31, the authoritative narrative with
it). A test plan for a document interface therefore contains a kind of
step no message interface needs: a human attestation gate, scheduled and
named, that no property-based test can stand in for.

The longer arrows — delta to snapshot, snapshot to delta — are
compositions of the above and inherit every demand along the way. The
pattern across all of them is the chapter's cash value: for each pair of
kinds, the transformation's hard part is exactly the component one side
has and the other structurally lacks — the state a delta needs, the
history a sigma forgot, the signature only a human can supply. Chapter
31 builds the machinery — lenses — for the arrows that do exist, and
takes just as seriously the ones that don't; a missing arrow, honestly
identified, is a fixture requirement or a human gate discovered at
design time, which is the cheapest moment anything is ever discovered.
