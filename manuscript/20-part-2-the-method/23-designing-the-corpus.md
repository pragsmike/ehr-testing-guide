<!-- Companion sync (ADR-0007): landing this chapter pays the OBR-4 debt that
     Chapters 21-22 deferred here. It requires (1) promoting `parse-oru` to
     read OBR-4 (the ordered test code, `1554-5`) and carrying it in
     `ParsedOru` alongside the resulted OBX-3 code, so the order/result
     divergence can be exercised at all; (2) a controlled-mutation helper in
     the companion that rewrites OBX-3's resulted code to the ordered OBR-4
     code, with a test showing the mutation breaks the quality-measure
     `code-in-value-set` property (2345-7 is in the value set, 1554-5 is not)
     while leaving all six medication-reconciliation properties green -- the
     Chapter 22 lattice split, finally executable; (3) extending the golden
     layer in `specimen-test` with H and L abnormal-flag cases whose expected
     classifications are hand-authored, so `classify-abnormal` is checked by
     an oracle independent of the transform (the shared-rule bug the
     purpose-set properties structurally cannot see). Register sync: flip C17
     and C22 to "in ch 23" on landing. Glossary sync: add "controlled
     mutation" (defect injection), "synthetic population" / Synthea (full
     dated entry is Chapter 43, not here), and "seed / reproducibility
     metadata"; verify golden case, golden corpus, oracle problem, and
     criticality tier are already present (they are) rather than duplicating
     them. Acronyms: OBR and OBX are already listed; nothing new (Synthea is
     not an acronym). Failure-mode index: the "different golden output across
     runs -> unseeded generator" row (ch 23) already exists; add a row for a
     mutation that fails to break its tagged target -> a missing or too-weak
     property in the suite. No new bib keys: Synthea is a Part IV tool entry,
     not a citation, and metamorphic testing's chen2018metamorphic belongs to
     Chapter 24. -->

# Designing the Corpus

> **Thesis:** A test corpus for an EHR transformation needs three distinct
> layers that fail in different ways: curated golden cases, whose danger is
> that their expected outputs silently encode the spec's own blind spots
> (the provenance problem); generated cases from a synthetic-population tool
> like Synthea, extended with controlled post-generation mutation to inject
> known defect classes; and, underneath both, the discipline of recording
> generator seeds and tool versions so that a failing case found today can
> be reproduced tomorrow. Skipping any one layer produces a corpus that
> looks thorough and isn't.

## Where the property suite goes blind

Chapter 21 turned each purpose-set row into a property and Chapter 22
combined two recipients' rows into one suite, and the result is genuinely
load-bearing: run the union suite over generated inputs and it catches a
transform that drops the abnormal flag, misroutes the status field, or
mangles the subject reference on the way from source to output. But look
closely at how one of those properties actually decides that two answers
agree, because the mechanism has a gap in it that no amount of running the
suite will close.

Take the interpretation row. The property reads the flag out of the
Observation's `interpretation` coding, reads OBX-8 out of the source, and
asks whether the two agree — but "agree" here means *agree after
classification*: both answers are run through `classify-abnormal` before
they are compared, because the clinician acts on in-range-or-not, not on
the literal letter. Now suppose `classify-abnormal` is wrong — suppose it
maps the high flag `H` to in-range. The transform carries the flag through
faithfully; the source query reads the same flag; both sides are then
handed to the same broken `classify-abnormal`, both come back "in range,"
and the two agree. The property passes. It passes on every input, at every
trial count, forever — and the transform it is certifying will present a
critically high result to a clinician as normal.

This is not a bug in the property. It is a structural feature of the
observational method, and naming it precisely is the whole reason this
chapter exists. A purpose-set property checks that the transform
*transports* a field's meaning intact from source to output; it computes
the source's answer using the very rules the transform uses, so it is
constitutionally unable to check those rules. When the transform and the
source query share a definition — `normalize-ucum` on the units row,
`classify-abnormal` on the interpretation row, `status->fhir` on the status
row are each called on both sides in the companion — a bug *inside* the
shared definition makes both sides wrong in unison, and the triangle
commutes by construction for the rule's own mistaken semantics. The
property is blind to exactly the errors it shares with the thing it tests.
(The companion records this as a comment on `purpose-set-med-rec`, so that
a future reader adding a row does not mistake the circularity for an
oversight.)

The name for the general difficulty is the **oracle problem**: to check an
output you need to know, by some route independent of the computation under
test, what the right output is. A purpose-set property solves the oracle
problem for *transport* — the independent route is "read the source" — but
it solves it by borrowing the transform's own rules, so for the rules
themselves it has no independent oracle at all. Supplying that missing
oracle is the first job of a test corpus, and it is why a corpus is not
merely a convenience for feeding the properties inputs. The properties can
generate their own inputs all day; what they cannot generate is a second,
independent opinion about what the answer should be. That opinion has to be
written down by hand, by someone reading the specification rather than
running the code — and a case carrying such a hand-written answer is the
first of the three layers this chapter builds.

## Golden cases and the provenance problem

Start with the layer that supplies the missing oracle. A **golden case** is
a single test case built entirely by hand: a specific input paired with the
specific output a person decided, by reading the spec, that input should
produce. The companion already carries the specimen's golden case — the
sample ORU parsed to a `ParsedOru` whose every field is asserted against a
literal (`patient-id` is `"123456"`, `value` is `95`, `units` is `"mg/dL"`,
`status` is `"F"`), and the derived Observation spot-checked against
literals in turn (`valueQuantity.value` is `95`, `status` is `"final"`, the
subject reference is `"Patient/123456"`). None of those literals was
computed by `oru->observation`. They were typed out by a person who read
the message and the FHIR spec and decided what the answer was. That
independence is the entire value of the layer: because the expected output
came by a different route than the transform, a golden case can catch a
shared-rule bug the properties cannot — add a case whose OBX-8 is `H` and
whose hand-written expected interpretation is *out of range*, and a
`classify-abnormal` that maps `H` to in-range fails it immediately, where
the property sailed through.

And now the danger, which is the mirror image of the value. The expected
output is only as good as the person and the spec that produced it. If the
author misreads the specification — believes, wrongly, that a preliminary
result should be presented as final — then the golden case they write will
*encode that misreading as its expected output*, and a transform that makes
the same mistake will pass the golden case that was supposed to catch it.
The independence that makes golden cases powerful is not automatic; it
holds only as far as the case author's understanding of the spec diverges
from the transform author's, and when the same person writes both, or both
read the same wrong guidance, the independence collapses and the golden
case is wrong in precisely the way the transform is wrong. Call this the
**provenance problem**: a golden expected output is not ground truth, it is
a claim made by a particular person against a particular version of a spec
on a particular day, and it inherits every blind spot of that provenance.

The discipline the provenance problem demands is not "write better golden
cases" — you cannot audit your way out of a blind spot you share — but
*recording where each expected output came from*, so that when a golden
case and the transform agree, you can ask whether they agree because the
transform is right or because they share an author. Every golden expected
output carries, as metadata, who wrote it, against which version of which
spec, on what date. Treat the golden corpus as a set of reviewed claims,
not as an answer key: the review that matters is a second person, reading
the same spec independently, confirming the expected output — the only
thing that actually re-establishes the independence the layer's value
depends on.

Within that discipline, the coverage a golden layer aims for is the one
Chapter 21's criticality column already dictates. There is one clean
happy-path case per typical shape; a case exercising every safety-critical
and decision-affecting field, so that each purpose-set row has at least one
golden case that would fail if that field were dropped; boundary values,
where transforms characteristically break — the reference-range edges,
zero, a negative, the maximum precision the field allows; and the
known-hard cases harvested from the field, the ones the V2-to-FHIR
Implementation Guide's own examples and the site logs already know are
tricky. The golden layer is small by nature — every case is hand-built and
hand-reviewed — so it cannot cover the space by volume. Covering the space
by volume is the second layer's job, and the second layer buys that volume
at the cost of the very oracle the first layer exists to supply.

## Generated cases: a synthetic population

No team hand-writes ten thousand patients. To exercise a transform across
the range of shapes real data actually takes — the odd demographics, the
unusual but valid lab panels, the co-occurring conditions a single
hand-built case never thinks to combine — you need a program that
manufactures realistic-but-fake patients in bulk, inventing plausible
histories from a model of how people age, fall ill, and get tested. Such a
program is a **synthetic-population generator**, and the one this book
points to is **Synthea**, an open, MITRE-maintained generator that runs a
state-machine model of disease and lifecycle to emit whole synthetic
populations, exportable as FHIR R4 bundles, C-CDA documents, or CSV
tables (convertible onward to OMOP via OHDSI's ETL-Synthea). Its dated
entry — versions, interop mode, where
it bites — is in Chapter 43; here what matters is exactly what it buys and
exactly what it cannot.

What it buys is breadth and realism at a volume no human supplies:
thousands of patients whose values co-vary the way real ones do, so the
transform meets combinations its authors never enumerated. What it *cannot
know* is the shape of your transform's failure modes. A generator produces
data that is valid by construction — that is its design goal — and a
transform's most dangerous inputs are precisely the invalid and the
edge-lying ones: the missing mandatory field, the local code outside every
published map, the malformed date, the order/result mismatch. Synthea will
not generate these, because it is built not to; asking it to is asking a
tool for realistic patients to double as a tool for unrealistic ones, and
it correctly refuses. The gap between what the generator's modules can
produce and what the transform needs to be tested against is a real,
nameable list, and every item on it is a hole the third layer must fill by
hand.

There is a second, subtler limit, and it reaches back to the oracle
problem. A generated case arrives with *no expected output*. Nobody
hand-wrote the answer, because nobody looked at the case — it did not exist
until the generator made it. So a generated case cannot be checked the way
a golden case is, against a precomputed answer; there is no answer. It can
only be checked by a property — by the observational triangle of Chapter
21, or by the metamorphic relations of Chapter 24, both of which decide
correctness without needing to know any single output in advance. This is
why the layers are not interchangeable and why the corpus is not one pile
of cases. Golden cases carry answers and check rules; generated cases carry
volume and check transport; the property suite is the machinery that turns
generated volume into verdicts. Feed generated cases to the golden layer's
expectations and you have nothing to compare them to; feed them to the
properties and they become the thousands of triangles the properties were
written to check.

## Controlled mutation for defect injection

The first two layers between them test that correct inputs produce correct
outputs. Neither tests the other half of the contract: that *incorrect*
inputs are caught rather than waved through. To test that, you have to
produce incorrect inputs deliberately, and the disciplined way to produce
them is not to hand-craft each broken case from nothing but to take a case
you already trust — a golden case, or a generated one — and make one
precise, documented change that ought to make one specific test fail.
Breaking a known-good case on purpose, in a known way, to confirm the break
is caught, is **controlled mutation**, or defect injection: the base data
stays valid by construction, and the defect is a single, labelled edit
layered on top, so that what the case tests is never in doubt.

Doing the injection *after* generation rather than teaching the generator
to produce defects is a deliberate choice, and it is the one Synthea's own
guidance recommends: keep the generated population clean and valid, and
introduce invalid codes, missing fields, date violations, and profile
breaches as a separate post-processing step. The reason is that a defect
modelled inside the generator is a defect you can no longer see — it
dissolves into the population and you lose the labelling — whereas a defect
injected on top of a known-clean base is a defect with a name attached. The
catalog of injections the specimen invites is small and concrete: an OBX-3
carrying a local code like `99zzz` outside any published map (does the
transform flag it or silently null it? — Chapter 34); a blanked
safety-critical field (the companion's `parse-oru` already throws rather
than defaults on a missing value or status, and a mutation is how you prove
that guard fires); a unit string of `mmol/L` where the target expects the
normalized `mg/dL`; a truncated timestamp. Each is a defect that *should*
break a specific purpose-set property, and that pairing is the point.

The specimen carries one designed mutation more instructive than the rest,
because it is the case that made Chapter 22's two recipients disagree. The
sample message *orders* one test and *reports* another: OBR-4 names the
ordered code `1554-5`, fasting glucose, while OBX-3 names the resulted code
`2345-7`, generic serum glucose. A transform that "cleans up" this divergence
by normalizing the reported code to the ordered one leaves the clinician's
verdict untouched — none of the six medication-reconciliation queries reads
the code at that grain — while moving the record in or out of a quality
measure whose value set contains `2345-7` but not `1554-5`. That is the
correctness lattice made executable: one mutation, tagged with the single
property it should break (the quality measure's `code-in-value-set` row),
that must leave the other recipient's six properties green. Wiring it up was
this chapter's one debt to the companion, paid at this chapter's landing:
`parse-oru` now carries the ordered OBR-4 code alongside the resulted one,
and `ehr-testing.corpus` holds the mutating function and the test that
asserts the split.

The tagging is not bookkeeping; it is what makes the mutation layer test
the tests. Every injected defect is labelled with the purpose-set question
it is supposed to break, and the label turns a passing test into
information: if a mutation that should break its tagged property *doesn't*,
the property is missing, or too weak, or reading the wrong field — the
mutation has found a hole not in the transform but in the test suite. Where
the golden layer checks that the right things pass and the generated layer
checks the transform at volume, the mutation layer checks that the suite is
capable of failing at all — the one thing a suite of always-green
properties can never tell you about itself.

## Reproducibility: seeds and versions as first-class artifacts

A synthetic-population generator makes its ten thousand patients by drawing
from a pseudo-random sequence, and a pseudo-random sequence is determined
entirely by the number it starts from — its **seed**. Run the generator
with no fixed seed and you get a different population every run: different
patients, different values, different edge cases surfacing and vanishing
between one Tuesday and the next. This is fatal in two directions at once. A
failing case you find today cannot be reproduced tomorrow, because
tomorrow's run generates different data and the failure is gone — you have
a bug report you cannot re-run. And any expected output you computed from an
unseeded generated case is not golden at all, because the input it was the
answer to will not come back; regenerate and the "golden" output is the
answer to a question no longer being asked. A corpus whose generated layer
has no recorded seed is not a fixed corpus. It is a different corpus each
time it is built, wearing the same name.

The discipline is to treat everything that could vary as an artifact to be
pinned and recorded alongside the corpus: the generation seed, so the
population is reproducible; the generator's version and the version of its
module set, because a generator update changes what the same seed produces;
and the population configuration that was passed in. The failure-mode index
carries this as a lookup — a golden output that changes across runs points
straight back to a generator invoked without a fixed seed — because it is
one of the most common and most disorienting ways a test suite silently
stops meaning anything. And the pinning does not stop at the generator. The
golden layer's expected outputs were authored against a specific version of
a specific spec, and that version is part of their provenance — the
previous section's point, now with a reproducibility edge: a spec revision
can turn a once-correct golden expectation stale. The structural gates of
Chapter 25 carry their own versions, the IG package and the code-system
release, and those too belong in the reproducibility metadata, because
"validates" against a moving terminology server is not a repeatable claim.
A corpus is a fixed object exactly to the degree that everything capable of
varying underneath it has been written down.

## Assembling the three layers into one plan

The three layers are not three ways of doing the same thing; they are three
sources with complementary blind spots, and the plan works because no one
failure slips past all three. The golden layer supplies the independent
oracle — the hand-written answer that catches a bug in a shared rule — but
it is small and only as trustworthy as its provenance. The generated layer
supplies volume and realism — the thousands of co-varying cases no one
would hand-build — but it carries no answers and cannot produce the invalid
inputs a transform most needs to meet. The mutation layer supplies the
deliberate defects and, in doing so, tests the suite's own capacity to fail
— but it only ever tests the defects someone thought to inject. Line them
up and the gaps do not overlap:

| Layer | What it supplies | What it catches | Its blind spot |
|---|---|---|---|
| Golden | Hand-authored expected outputs (an independent oracle) | Shared-rule bugs the properties can't see | Small; only as good as its provenance |
| Generated | Realistic cases at volume | Transport failures across combinations no one enumerated | No oracle (property-checked only); no invalid cases |
| Mutation | Labelled defects on a known-good base | Missing or too-weak properties; uncaught bad input | Only the defects you thought to inject |

Read the table as a sentence: a shared-rule bug the property suite is blind
to is caught by a golden case; a transport bug in a combination no golden
case covers is caught by a generated case run through the properties; a
property too weak to catch either is caught by a mutation that fails to
break it. Skip the golden layer and shared-rule bugs go uncaught; skip the
generated layer and the transform is certified only on the handful of
shapes someone imagined; skip the mutation layer and a green suite tells
you nothing, because you never established it could go red. That is the
sense in which the chapter's thesis holds: a corpus missing any one layer
looks thorough and isn't.

Where this connects to the rest of Part II is at both ends. Upstream, the
purpose sets of Chapters 21 and 22 are what the corpus is built to
exercise: each golden case checks specific rows, each mutation is tagged to
a specific row, and the generated cases are fed to the properties those
rows compiled into. The purpose set is the test suite's table of contents;
the corpus is the material the entries are run against. Downstream, the
corpus is the input side of the two chapters that follow — the properties
and metamorphic relations of Chapter 24, which turn generated volume into
verdicts where no oracle exists, and the structural validators of Chapter
25, which gate malformed input out before the properties are asked to
reason about it. The worksheet form of everything here — the three-layer
checklist, with the provenance and seed fields called out — is in the
templates chapter, ready to fill in against a transform of your own.
