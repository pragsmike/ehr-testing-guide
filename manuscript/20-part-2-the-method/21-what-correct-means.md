# What "Correct" Means

> **Thesis:** A lossy transformation cannot be judged correct or incorrect
> in the abstract — it can only be judged correct *relative to a set of
> observations*. A transform f is correct for a query q if asking q of the
> transformed data agrees with asking the corresponding query of the
> source: q composed with f matches q-source, an observational equivalence.
> The purpose set — the specific queries the downstream recipient will
> actually run against the data — is not an input to the specification.
> It *is* the specification. This chapter is the keystone of the book: if
> it does not hold, nothing built on top of it in Parts II and III holds
> either.

<!-- Companion sync (ADR-0007): the listing in "The specimen, worked" mirrors
     the purpose-set property currently stubbed in the comment block of
     companion/src/ehr_testing/properties.clj. Landing this chapter requires
     promoting that stub to a real, tested property. Glossary sync: verify
     entries for observational equivalence, purpose set, agreement tolerance,
     q/q-source; add "vacuous specification" if absent. Bib sync: add an
     entry for the origin of observational/contextual equivalence
     (Morris 1969, "Lambda-calculus models of programming languages",
     key suggestion: morris1969) — TODO-verify. -->

## Why "correct" needs an object to be correct *for*

Every working integration engineer has heard a sentence of the form "the
interface is working." It is worth pausing on what that sentence could
possibly mean. The messages parse; the transform runs without throwing; the
output validates against its schema; the downstream system accepts it. All
four of those facts together are compatible with the interface being
dangerously wrong — with the units field silently defaulted, the result
status dropped, the preliminary result presented as final. Each of those
failures has occurred in production systems, and each occurred in an
interface that was, by every check just listed, "working."

The instinct is to reach for a stronger absolute standard: the transform is
correct if it *loses nothing* — if the output carries everything the input
carried, so that the input could be reconstructed. Part I of this book (and
the design rationale behind it) spent its whole length establishing that
this standard is unreachable, not as an engineering shortfall but as a
structural fact. The representations are different *kinds* of objects —
events, states, attested documents — so some arrows between them do not
exist as total functions at all. The formats carry open-ended content —
extensions, Z-segments, narrative — so any fixed target discards something.
And the terminologies the coded fields point into partition clinical
reality along genuinely different lines, so code translation is a relation
with side conditions, not a function. A correctness standard that demands
losslessness classifies *every* real healthcare transformation as
incorrect, which makes it useless as a standard: it draws no distinction
between the transform that drops the performing lab's address and the one
that drops the abnormal flag.

So the naive absolute standards fail in opposite directions. "It runs and
validates" is too weak: it is a specification against which nothing
meaningful can fail. "It loses nothing" is too strong: it is a
specification against which everything fails. Both share the same defect —
neither mentions what the transformed data is *for*. A transformation with
no stated purpose cannot fail, because it cannot be judged; and it cannot
be meaningfully tested, because a test needs a behavior to hold the system
to. The way out is not a cleverer absolute standard. It is to admit that
"correct" has always been a two-place predicate wearing a one-place
disguise: correct *for* something. This chapter makes the second place
explicit and precise.

## Observational equivalence, operationally first

Forget formalism for a moment and picture the check you would actually
perform if you distrusted a transformation. You have the source message.
You have the transformed output. You pick a question that matters — *what
is the numeric result?* — and you answer it twice: once by reading the
source (find the OBX segment, the observation/result line of an HL7 v2
message; read its fifth field), and once by reading the output (find the
Observation resource; read its valueQuantity). Then you compare the two
answers. If they agree, the transformation preserved *that question*. You
repeat this for every question you care about: the units, the reference
range, whether the result was final or preliminary, which patient it
belongs to. If every question you care about yields agreement, you trust
the transform — not because it preserved everything, but because it
preserved everything you asked.

That procedure — same question, both sides, compare — is the entire
definition, and everything else in this chapter is bookkeeping around it.
The bookkeeping, stated once, precisely: for each question there are two
queries, one the recipient runs against the transformed output, call it
*q*, and a corresponding one against the source, call it *q-source*. Each
is just a function from data to an answer. The transform *f* is **correct
for q** when the two routes to an answer agree on every input — when
applying *f* and then asking *q* gives the same answer as asking
*q-source* directly. As an equation: *q ∘ f = q-source*. Drawn as a
diagram, source and target sit at two corners, the answers at a third, and
correctness-for-*q* says the triangle commutes: both paths from source to
answer are the same path.

This idea has a name and a lineage. In programming-language semantics, two
program fragments are **observationally equivalent** when no observation
you are permitted to make can tell them apart — they may differ internally
however they like, so long as every experiment in the permitted set yields
the same result [@morris1969]. We are applying the same move to data
rather than programs: the source and its transformed image are equivalent
*with respect to a set of observations*, namely the recipient's queries.
What the transform does to content outside that set is not merely
forgiven; it is *invisible*, in the exact sense that no permitted
observation can detect it. Lossiness stops being a sin and becomes a
scope: loss inside the observation set is incorrectness, loss outside it
is irrelevance — by declaration, reviewed and signed (Chapter 22 takes up
the review).

One refinement, forced immediately by practice: "agree" rarely means raw
equality. If the source carries `mg/dL` and the recipient's query
normalizes units, then a source answer of `95 mg/dL` and a target answer
of `95 mg/dL` after canonicalization agree, while `5.3 mmol/L` — the same
physical quantity — may or may not count as agreement depending on what
the recipient's action can absorb. Every query in a purpose set therefore
carries an **agreement tolerance**: exact equality, equality after a named
normalization, membership in the same classification (in-range vs
out-of-range), or answer-sets equal as sets. The tolerance is part of the
question, not an implementation detail — asking "what is the value,
exactly" and "what is the value, up to unit conversion" are different
questions, and a transform can be correct for one and not the other. The
worksheet in the templates chapter gives tolerance its own column for
exactly this reason.

## The purpose set as specification

The set of queries against which a transformation is judged — the
**purpose set** — is not chosen by the transform's author, and this is the
hinge of the whole method. It is elicited from the *recipient*, and not
from the recipient's preferences but from their **action**: the decision
they will take with the data in hand. A clinician reconciling medications
will read the value, the units, the flag, the status, and act on them; a
quality-reporting pipeline will test a code against a value set and a date
against a period, and count. Each action determines, with unusual
concreteness for a requirements exercise, the questions that must survive
the transformation — because a question the recipient never asks cannot
affect their action, and a question they do ask, answered wrongly, acts
on them directly.

Two consequences follow, and they cut in opposite directions — this is
the "no more, no less" of this chapter's title section.

*No less:* every query the recipient's action depends on belongs in the
set, at the tolerance the action requires and at a stated criticality. A
purpose set that omits result status is not a lean spec; it is a spec with
an unstated requirement, and unstated requirements in this domain surface
as incidents. The elicitation discipline — sit with the recipient, walk
the action, write each dependence as a query pair with a tolerance — is
requirements engineering, not mathematics, and the mathematics does not
excuse you from it. What the mathematics adds is a *form* the elicited
requirements must take: not "the data should be accurate" but a list of
(q, q-source, tolerance, criticality) rows, each one executable.

*No more:* a query nobody runs does not belong in the set. This is the
uncomfortable half, because it feels like rigor to demand more fidelity
than any consumer uses — and it is in fact a cost with no beneficiary.
Every additional query narrows the space of acceptable transforms,
generates test failures someone must triage, and — worst — dilutes
attention from the queries that carry clinical weight. A purpose set
padded "to be safe" manufactures false alarms in exactly the way
over-broad monitoring does, and the response to alarm fatigue is always
the same: the alarms stop being read. Where genuine uncertainty exists
about whether a future consumer might need a field, the honest home for
that uncertainty is the residue review (Chapter 22), which records the
dropped content and who signed off on dropping it — not a phantom query
in the purpose set on behalf of a recipient who does not yet exist.

Because the purpose set is per-recipient, the same transform faces a
different specification for each consumer, and there is no useful sense
in which it is "correct, period" — only correct-for-A, incorrect-for-B,
a vector of verdicts. That structure, and what to do about it, is
Chapter 22's subject; here it is enough to note that the plurality is
not a flaw in the definition but the definition finally matching how the
data is actually used. And because every entry in a purpose set is an
executable query pair with a decidable agreement relation, the purpose
set is not documentation *about* the tests — it compiles directly into
them. Each row becomes a property: for all inputs, the triangle for this
q commutes. Chapter 24 builds that compilation; the templates chapter
gives the worksheet that feeds it.

## The specimen, worked

The book's running specimen is the transformation of an HL7 v2 ORU^R01 —
an unsolicited observation-result message — into a FHIR R4 Observation
resource. The companion project carries the sample message; its one OBX
segment reports a serum glucose: LOINC code `2345-7` in OBX-3 naming the
question asked of the specimen, the numeric value `95` in OBX-5 with
OBX-2 declaring the type NM, units `mg/dL` in OBX-6, a reference range
`70-100` in OBX-7, the abnormal-flag field OBX-8 reading `N` for normal,
and a result status of `F` — final — in OBX-11.

The recipient, for this chapter, is a **medication-reconciliation
clinician**: a prescriber checking a recent glucose before adjusting an
insulin dose. (The second recipient — a quality-measure pipeline reading
the *same* transform — is deferred to Chapter 22, where the two verdicts
diverge; the full two-recipient worksheet appears in the templates
chapter.) Walking the clinician's action yields six dependences, stated
as query pairs:

| q (against the Observation) | q-source (against the ORU) | Tolerance | Criticality |
|---|---|---|---|
| valueQuantity.value | OBX-5 | Exact | Safety-critical |
| valueQuantity unit, as UCUM | OBX-6 | Normalized to UCUM; **not** cross-unit conversion | Safety-critical |
| interpretation: in/out of range | OBX-8 (with OBX-7) | Same classification | Decision-affecting |
| status: final vs preliminary | OBX-11 | Exact | Safety-critical |
| effective time | OBR-7 | Exact | Decision-affecting |
| subject identity | PID-3 | Exact | Safety-critical |

Three of the rows deserve a sentence each, because their tolerances and
criticalities encode clinical judgment, not convention. The **units** row
is normalized-not-converted: mapping `mg/dL` to its UCUM form is
preservation, but converting to `mmol/L` — even correctly — changes what
the clinician sees against their memory of the patient's prior values,
so for *this* recipient the tolerance stops at normalization. The
**status** row is safety-critical at exact tolerance because a
preliminary result presented as final invites action on a number the lab
may yet correct; F and P must survive as themselves. And the
**interpretation** row is deliberately classification-tolerant: the
clinician acts on *in-range or not*; the transform may restructure how
range and flag are carried so long as the classification is preserved.

Notice, finally, what the purpose set *ignores*: the ordering provider
in OBR-16, the performing-lab identity, the specimen-source detail, the
filler order number. Under this recipient's queries, a transform that
drops all four is exactly as correct as one that carries them — the
queries cannot see the difference. Whether they may be dropped
*simpliciter* is a different question, answered by other recipients'
purpose sets and by the residue review; the point here is only that
this recipient's correctness does not depend on them.

In the companion, a purpose-set row is a property, live and tested in
`ehr-testing.properties`, where `property-for-row` builds one such
property from each row. The shape is:

```clojure
;; correct-for-q: the triangle commutes, at this row's tolerance
(prop/for-all [oru gen-parsed-oru]
  (agree? tolerance
          (q        (oru->observation oru))
          (q-source oru)))
```

One property per row; six rows, six properties; the purpose set *is* the
test suite's table of contents. Generation of the `oru` inputs, and what
the generator must cover for the properties to mean anything, is
Chapter 23's subject.

## What this does not claim

The definition invites four misreadings, each worth closing off.

*It is not relativism.* Correctness is relative to a purpose set, but
once the purpose set is fixed, the verdict is as objective as an
equation, because it is one: for each row, either the triangle commutes
over the tested inputs or it does not. Two engineers who disagree about
whether the transform is correct are disagreeing about *which purpose
set governs* — a resolvable, factual dispute about a named recipient's
action — not about a matter of taste.

*It does not make structural validity optional.* A message that fails to
parse, or an output that violates its profile, never reaches the
observational question — the queries cannot even be asked of it.
Validators are gates upstream of everything in this chapter, necessary
and insufficient (Chapter 25). Observational correctness is what remains
to be established *after* both sides are well-formed, and no amount of
schema conformance establishes it.

*It does not bless what it ignores.* That the clinician's queries cannot
see the dropped ordering provider makes the drop invisible to *this*
verdict, not harmless. The discipline that keeps invisible drops from
becoming incidents is the residue review — enumerate what is dropped,
name who does not need it, obtain sign-off from someone entitled to say
so (Chapter 22). The purpose set draws the boundary of correctness; the
residue sheet patrols the far side of it.

*It does not elicit the purpose set for you.* The mathematics converts a
good requirements conversation into an executable specification; it
cannot have the conversation. If the recipient's action is
misunderstood — if the elicitation misses that the prescriber also
glances at the collection time to judge staleness — the resulting purpose
set is faithfully, precisely wrong, and every property will pass. The
failure mode of this method is not broken triangles; it is commuting
triangles for the wrong queries. That is why purpose sets are reviewed
with the recipient, versioned, and revisited when the recipient's action
changes — and why the criticality column exists: it records how much a
wrong answer to *this* question costs, which is the one judgment no
formalism supplies.

What this chapter establishes, then, is small and load-bearing: a
two-place notion of correctness, an operational test for it, and the
identification of the specification with the recipient's own queries.
Chapter 22 shows what the plurality of recipients does to the notion;
Chapters 23 and 24 build the corpus and the properties that make the
triangles checkable in bulk; and Part III explains why the triangles
that refuse to commute — the ones this method is built to find — fail
for the structural reasons they do.
