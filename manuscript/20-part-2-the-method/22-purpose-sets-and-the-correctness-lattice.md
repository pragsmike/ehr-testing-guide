# Purpose Sets and the Correctness Lattice

> **Thesis:** Because correctness is always correctness-for-a-purpose-set,
> a single transformation can be simultaneously correct for medication
> reconciliation and incorrect for quality reporting — correctness forms a
> lattice over purpose sets, not a boolean. The information a purpose set
> declares irrelevant is the *residue*, and the residue is not a shameful
> gap to hide: it is an enumerable, reviewable artifact. Writing the
> residue down, rather than discovering it in production, is what separates
> engineering a transformation from merely hoping it works.

<!-- Companion sync (ADR-0007): the listing in "The lattice, operationally
     first" requires adding purpose-set-quality-measure as a second data
     table in ehr-testing.properties, plus the union suite and defspecs for
     the B rows computable from the current schemas (value-set membership,
     threshold, period, identity, status). The order/result (OBR-4 vs OBX-3)
     divergence is prose-only here; parsing OBR-4 is deferred to ch 23's
     mutation layer. Glossary sync: verify residue, correctness lattice;
     add join/meet (paired entry) and accidental contract if absent.
     Register sync: flip C16 to "in ch 22" on landing. Failure-mode index:
     this chapter suggests two new rows (unreviewed residue -> accidental
     contract; silent code normalization -> shifted denominator). -->

## Two purpose sets, one transform, two verdicts

Chapter 21 worked the specimen for one recipient: a prescriber
reconciling medications against a glucose result, whose action yielded
six query pairs — value, units, in/out-of-range, final-vs-preliminary,
time, identity. Now put a second recipient behind the *same* transform: a
quality-measure pipeline deciding whether this patient counts toward a
diabetes-control denominator. Walking *that* action yields a different
set of dependences: whether the OBX-3 code falls inside the measure's
value set; the numeric value against the measure's own threshold; the
patient identity, for attribution and deduplication; the effective time,
against the measurement period; and the result status. (The full
worksheet, alongside the clinician's, is in the templates chapter; here
we need only the comparison.)

Lay the two sets side by side and three regions appear. There is a
**shared core** — value, identity, status, time — that both recipients
read, though not always at the same tolerance. There is content only the
clinician observes: the reference range and abnormal flag, which the
measure ignores entirely because it brings its own thresholds. And there
is content only the measure observes: *which* code names the analyte,
tested for membership in a value set — a question the clinician, reading
"glucose, 95, in range," never asks with that precision.

The divergence is not decorative; it produces opposite verdicts on real
transforms. Consider two candidate implementations. Transform one drops
the reference range and abnormal flag — perhaps the target profile made
interpretation optional and nobody mapped it. Every query the measure
runs still commutes: it is *correct for quality reporting* and
*incorrect for medication reconciliation*, because the clinician's
in/out-of-range triangle no longer commutes. Transform two "cleans up" a
site quirk: when the resulted OBX-3 code differs from the ordered test
(the sample's own lineage has this — an order placed as `1554-5`,
fasting glucose, resulted as `2345-7`, generic serum glucose), it
normalizes the reported code to the ordered one. The clinician's
verdict is untouched — none of their six queries reads the code at that
grain. But the measure's value-set membership question now gets a
different answer than it would have from the source, records move in or
out of the denominator, and the transform is *incorrect for quality
reporting* while remaining *correct for medication reconciliation*.
Same transform, two recipients, two verdicts — and neither verdict is
confused or provisional. Each is exactly right about its own question.

Any framework in which "is it correct?" demands a single boolean answer
must treat this situation as a paradox or an edge case. It is neither.
It is the normal condition of shared clinical data, and the structure it
exhibits has a name.

## The lattice, operationally first

Order purpose sets by demand. Say one purpose set *demands at most* what
another does when every query in the first appears in the second at a
tolerance at least as strict — everything the first checks, the second
also checks, at least as hard. Under this ordering, three operational
facts do all the work of this section.

First, **verdicts flow downhill**. If a transform is correct for a
purpose set, it is automatically correct for every purpose set that
demands less — each of the smaller set's triangles is among the larger
set's triangles, already shown to commute. Contrapositively, a failure
against a small purpose set dooms every larger one that contains it.
This gives cheap reasoning in both directions: passing a demanding
suite certifies all its weakenings for free, and a single failing query
condemns every recipient who asks it.

Second, **two purpose sets combine**. The union of the clinician's set
and the measure's set — every query either runs, each at the strictest
tolerance either requires — is itself a purpose set: the demand of
serving both recipients at once. Correct-for-the-union is exactly
correct-for-both, since each query in the union belongs to at least one
of them. Operationally this is how a multi-consumer interface is
actually tested: build the union suite, run it once, and read each
recipient's verdict as a projection of the results onto their rows.

Third, **two purpose sets intersect**. The queries both recipients run —
here: value, identity, status, time, at the stricter of each pair's
tolerances — form the demand that *any* consumer of this data makes.
This intersection is worth pausing on, because it is an old friend
wearing new clothes: it is the *shared core* of Part I's span, the
overlap along which the hub architecture needed to glue representations
together. What two formats can be glued along, and what two recipients
jointly require, are the same object approached from opposite ends —
the semantic content everyone agrees to observe.

A partially ordered collection in which any two elements have a least
combined element (a **join** — here, the union of demands) and a
greatest common element (a **meet** — the intersection) is called a
**lattice**, and this is the sense in which correctness is
lattice-shaped rather than boolean: the verdicts a transform earns are
not one bit but a region of this lattice — everything below the largest
purpose sets it satisfies. Honest tooling reports the region: the
vector of per-recipient verdicts, with the failing queries named.

In the companion, the second recipient is what you would expect by now —
another data table — and the join is a `concat` with tolerance
reconciliation on the shared rows:

```clojure
(def purpose-set-quality-measure
  "Chapter 22, recipient B: denominator inclusion for a glucose measure."
  [{:name :code-in-value-set  :tolerance :set-membership ...}
   {:name :value-vs-threshold :tolerance :same-classification ...}
   {:name :subject-identity   :tolerance :exact ...}
   {:name :in-measurement-period :tolerance :within-period ...}
   {:name :status             :tolerance :exact ...}])

(def combined-suite
  "The join: serve both recipients; shared rows keep the stricter tolerance."
  (union-strictest purpose-set-med-rec purpose-set-quality-measure))
```

One property per row, as always; the per-recipient verdict is a filter
over the results. The purpose set was the test suite's table of
contents in Chapter 21; the lattice is what the tables of contents do
when a second reader arrives.

## The residue, defined

Fix the purpose sets in scope — say, the union just built. The
**residue** is everything the source carries that no in-scope query
observes: for the specimen, the ordering provider in OBR-16, the
performing-lab identity, the specimen-source detail, the filler order
number, and — if the measure were ever descoped — the reference range
and flag would fall into it too. The residue is the precise complement
of the specification: content whose preservation or destruction no
in-scope triangle can detect. By construction, a transform that
mangles its residue and one that preserves it perfectly earn identical
verdicts.

The tempting inference is that residue is therefore *safe to drop*, and
resisting that inference is this section's whole job. "No in-scope
query observes it" is a statement about the purpose sets you gathered,
not about the world. Three distinct hazards live in the gap. There are
**out-of-scope recipients** — billing reads the ordering provider;
an audit reads the performing lab — who were not consulted, and whose
purpose sets would move content out of the residue the moment they are.
There are **future recipients**, the consumers who do not exist yet and
therefore cannot be elicited, for whom the event log (Part III) is the
structural answer — the log keeps what the views drop — but for whom
the *transform's* residue is still a real decision today. And there is
the subtlest hazard: **accidental contracts**. If the transform happens
to carry a field no purpose set claims, some downstream system will
eventually be built against it — the phenomenon software engineers know
informally as Hyrum's Law: with enough consumers, every observable
behavior of a system becomes load-bearing for someone. Unreviewed
residue that survives is a promise you don't know you made; unreviewed
residue that is dropped is a need you don't know you failed. The only
losing move is the *unreviewed* part.

So the residue is not the specification's trash. It is the
specification's *shadow* — cast by the purpose sets you chose, exactly
as sharp-edged as they are, and shifting whenever they do. Chapter 21
said loss outside the observation set is "irrelevance by declaration,
reviewed and signed." The residue is where the declaring, reviewing,
and signing happen.

## Making the residue reviewable

An unreviewed residue is a list nobody wrote down. A reviewed residue
is an artifact with four columns and a signature line — the full
template is in the templates chapter; the shape is:

| Dropped element | Who doesn't need it | Who *might* | Disposition & sign-off |
|---|---|---|---|
| Ordering provider (OBR-16) | Clinician, measure | Billing — **out of scope**, named, dated | |
| Reference range / flag | Measure | Clinician — **kept** | |
| Order/result code divergence | Clinician | Measure — **flag, never silently normalize** | |
| Performing-lab identity | Clinician | Measure attribution — **confirm before drop** | |

The review discipline is short enough to state completely. *Enumerate*:
the residue is computed, not intuited — walk the source schema, subtract
the union purpose set's footprint, and list what remains; a residue you
brainstormed is a residue with holes. *Adjudicate each row*: kept,
dropped-with-named-out-of-scope-recipient, or escalated — and "might
need it someday" is not a disposition; it converts into either a named
recipient with an elicited purpose set or a dated decision to rely on
the upstream log. *Sign*: by someone entitled to bind the recipients
named — which is a governance statement, not a formality, because the
signature is what turns "we didn't map it" into "we decided not to
carry it," and those two sentences have very different readings in an
incident review. And *re-review on change*: the residue shifts when a
purpose set does, so the sheet carries the purpose-set versions it was
computed against, and a purpose-set change reopens it.

None of this is heavy. For the specimen it is one afternoon and one
page. Its value is asymmetric: cheap always, and on the day a dropped
field surfaces in production, the difference between an engineering
decision with a date and a name on it, and a shrug.

## Where teams skip this step, and what it costs them

The step gets skipped for an understandable reason: everything upstream
of it can be green. The messages parse; the profile validator passes;
the interface runs for months. Every signal the team watches says done.
What the green signals share is that they are all *structural* — and
the whole burden of Chapters 21 and 22 is that structural validity and
correctness-for-a-promise are different properties with an unmeasured
gap between them. Three recurring shapes of the cost, each traceable to
a specific skipped artifact:

The **dropped-flag incident**: a transform that loses the abnormal flag
passes every schema check, and the loss surfaces as a clinician acting
on an unflagged critical value — found not by a test but by an incident
report. The missing artifact is the clinician's *purpose set*: the
in/out-of-range row was never written down, so no triangle existed to
fail. The **shifted denominator**: a transform that silently
normalizes codes passes the same checks, and the loss surfaces months
later as a quality measure that cannot be reconciled with the source
system — found at audit, unwound at the cost of restating a reporting
period. The missing artifact is the *second recipient's* purpose set:
one consumer's correctness was assumed to transfer, which is exactly
the inference the lattice exists to forbid. And the **accidental
contract**: a field carried by luck, never claimed by any purpose set,
acquires a downstream consumer; a later "cleanup" drops it and breaks a
system nobody knew was listening. The missing artifact is the *residue
sheet*: the field was in nobody's specification and therefore in
nobody's impact analysis.

The pattern across all three is the same. The failure was invisible to
every artifact the team maintained, and would have been a one-line
entry in an artifact this chapter costs an afternoon: a worksheet row,
a second worksheet, a residue line with a signature. That is the
economics of the method in miniature — the artifacts are cheap
precisely because they are small and declarative, and the incidents
they preempt are expensive precisely because they surface far from
their cause, in another team's system, in production, with a patient
or an auditor attached. Part II's remaining chapters supply the
mechanics — the corpus that exercises the triangles (Chapter 23), the
properties that check them in bulk (Chapter 24), the structural gates
that keep malformed data from ever reaching them (Chapter 25). But the
mechanics inherit their meaning from what this chapter and the last
established: a specification is a set of named observers, correctness
is commuting with all of them, and everything else — enumerated,
adjudicated, signed — is residue.
