<!-- Companion sync (ADR-0007): this chapter lands with
     `companion/src/ehr_testing/conceptmap.clj` and
     `companion/test/ehr_testing/conceptmap_test.clj` -- the worked
     local-to-LOINC map, the honest `check-translation`, and the dishonest
     equality check kept executable in the test namespace (C26). Register
     sync: flip C12 and C13 to "in ch 34"; extend C24 and C26 with ch 34.
     Glossary sync: add ConceptMap, CUI, crosswalk, section (of a span),
     terminology server, UMLS; acronyms add CUI, UTS, NLM. Bib sync: add
     bodenreider2004umls, nlm_snomed_icd10cm, hl7_fhir_validator_docs,
     fhir_r4_conceptmap, umls_uts. External facts asserted here carry
     F-rows F5-F9 in notes/claims-register.md (validator offline behavior,
     R4/R5 ConceptMap vocabularies, UMLS access terms and Metathesaurus
     scale, the NLM map's rule-based structure, LOINC 2339-0/2345-7).
     Part IV sync: Chapter 42's UMLS and ConceptMap entries gain
     where-to-get-it detail and refreshed verification dates in the same
     commit. -->

# Terminology: Where Arrows Are Not Functions

> **Thesis:** SNOMED, LOINC, ICD, and RxNorm partition clinical reality
> differently on purpose — they answer different questions — so the
> official maps between them are not functions but partial, n-to-m,
> context-dependent relations: spans with no clean section in the
> mathematical sense. UMLS acts as a terminology-level universal object
> (echoing, and complicating, Chapter 32), and FHIR's ConceptMap is the
> practical arrow between vocabularies, annotated with the honesty about
> equivalence that a plain function could never carry. This is not a defect
> to route around; it predicts, precisely, where terminology-translation
> tests need to look.

## Four terminologies, four different questions

The specimen's OBX segment carries one code this book has been reading
since Chapter 21: `2345-7^Glucose^LN`, a LOINC code in OBX-3 identifying
what was measured. Follow the same clinical event outward from that
segment, though, and the coding obligations multiply. The lab that ran
the test identified the observation with LOINC. The clinician who ordered
it is monitoring a condition that the problem list records as a SNOMED CT
concept. The encounter in which the order was placed will be billed, and
the claim wants the diagnosis as ICD-10-CM. And if the result moves the
clinician to adjust the patient's insulin, the new prescription names its
drug in RxNorm. One morning's glucose draw, four code systems — and none
of the four is redundant with any other, because each one is answering a
different question about the same event.

LOINC identifies *what was observed*: the test, the property measured,
the specimen, the method — the identity of the question the lab answered,
independent of the answer's value. SNOMED CT names *clinical meaning*:
conditions, findings, procedures, body structures, composed into an
ontology built for recording and reasoning about care. ICD-10-CM sorts
encounters into *statistical and reimbursement categories*: its classes
exist so that populations can be counted and claims can be paid, and its
boundaries fall where counting and payment need them, not where clinical
distinctions do. RxNorm names *drugs*, at several deliberate grains at
once — ingredient, clinical drug, branded product — because "which drug"
is a different question at the pharmacy counter than in an allergy list.

The instinct this chapter exists to correct is the reading of these four
as rival dialects — four vendors' attempts at the one true vocabulary,
awaiting a sufficiently diplomatic merger. They are nothing of the kind.
Each system *partitions* clinical reality: it divides the space of
clinical facts into named regions, one region per code, so that every
fact falls somewhere and coding a fact means naming its region. And the
four partitions slice along different axes *on purpose*, because each was
drawn to make its own question answerable. ICD-10-CM lumps together
clinical situations that SNOMED CT carefully separates, and it is not
wrong to do so: for counting and billing, the distinctions genuinely do
not matter. LOINC splits what a clinician would casually merge — the
same analyte measured in serum versus whole blood gets different codes —
and it is not pedantry: for comparing results across labs, the specimen
genuinely does matter. A partition tuned to one question is exactly as
coarse and exactly as fine as that question requires. Four questions,
four partitions, four terminologies. The trouble begins only when a
transformation has to carry a fact from one partition into another — and
an EHR integration pipeline does little else all day.

## Maps as relations, not functions

Crossing between partitions sounds like it should be a lookup. There is
even an official artifact that sounds like one: the National Library of
Medicine publishes a map from SNOMED CT to ICD-10-CM, produced jointly
with the terminologies' own stewards, precisely so that a diagnosis
recorded clinically once can be re-coded for billing without a human
re-deriving it from scratch [@nlm_snomed_icd10cm]. In healthcare
integration such an artifact is called a **crosswalk**: a published
correspondence between the codes of one system and the codes of another,
maintained by somebody with the standing to say what corresponds to
what. If any terminology map were a clean lookup table, it would be this
one — two of the most mature terminologies in the field, mapped by the
national institution that hosts them both.

Read what NLM actually publishes, though, and the lookup-table picture
dissolves clause by clause. The map covers a *subset* of SNOMED CT —
frequently used concepts, not the whole system — so it is partial: some
inputs simply have no row. A single SNOMED CT concept maps not to an
ICD-10-CM code but to *map groups* of candidate codes, so it is one-to-
many before any context enters. Which candidate is right is governed by
*map rules* whose conditions reach outside the code entirely — NLM's own
documentation says candidate selection may depend on "additional
information obtained from the electronic patient record or direct user
input": the patient's age, sex, comorbidities, things the source code
does not carry. And the output is explicitly advisory — review by a
professional coder is recommended, because the map's stewards do not
claim the answer is derivable from the input. Each of these properties
is deliberate. None of them is a defect to be fixed in a future release.

Now set that artifact against the kind of arrow this book's diagrams
have been drawn with so far. A **function**, operationally: a rule that
takes each input to exactly one output, using nothing but the input —
no side consultation of the patient record, no judgment call, no "it
depends." Every clause of the NLM map's published behavior breaks a
clause of that description, and breaks it on purpose. What the map
actually is, is a **relation**: a set of (source, target) pairs — some
sources paired with several targets, some with none — decorated with
side conditions saying when each pairing applies. "Partial, n-to-m,
context-dependent" is not an engineering complaint about an immature
artifact; it is the honest type signature of the best crosswalk the
field knows how to make.

The signature matters operationally because pipelines compose maps, and
relations compose differently than functions. Chain two lookup tables
and you get a lookup table. Chain two crosswalks — a local system to
SNOMED CT, then SNOMED CT to ICD-10-CM — and the candidate sets multiply
while the side conditions conjoin: the composite is *less* determinate
than either leg, not more. A pipeline that routes codes through an
intermediate vocabulary "to normalize them" has not simplified its
translation problem; it has compounded two relations and then, usually,
silently picked one representative from the result. Where that pick
happens, and on what authority, is precisely what a translation test
needs to see — and precisely what a function-shaped interface hides.

## Spans with no section

Part I gave this book its recurring picture for two representations that
share content without either containing the other: a **span**, two arrows
out of a common apex, one leg to each representation. The terminology
case fits the picture exactly, with clinical reality itself at the apex.
SNOMED CT is one leg's codomain, ICD-10-CM the other's: each terminology
is a projection of the same clinical facts onto its own partition. The
crosswalk of the previous section is then an attempt to travel from the
foot of one leg to the foot of the other — and the diagnosis of that
section can now be stated structurally. What the terminology span lacks
is a **section**: a rule that, given a code on one side, picks a
representative on the other side in a way that survives the round trip
— cross over, cross back, land where you started. Operationally, a
section is what would make the crosswalk safe to automate: a canonical
choice function needing no patient record and no coder's judgment. The
previous section's evidence says the field's best artifact offers no
such rule, and the structural reading says *why*: neither leg factors
through the other, because neither partition refines the other. ICD-10-CM
is not a coarsening of SNOMED CT, nor the reverse; their regions
genuinely crosscut. Where one draws a boundary the other sees none, and
a code on one side names a region that straddles several regions — or
parts of regions — on the other. No completion of the mapping table
fills this in, because there is no fact of the matter to tabulate. The
gap is not missing data; it is the two partitions disagreeing about
which distinctions exist.

This is worth saying at the humblest scale too, because the place most
teams first hit a sectionless span is not SNOMED-to-ICD. It is their own
site's lab dictionary against LOINC. The companion's worked example
(`local->loinc`, in `ehr-testing.conceptmap-test`) is a site that uses
one local code, `GLU`, for every glucose result it produces — serum,
plasma, fingerstick whole blood — while LOINC, partitioning by specimen
on purpose, splits that region in two: `2345-7` (Glucose [Mass/volume]
in Serum or Plasma — the specimen's own OBX-3 code) and `2339-0`
(Glucose [Mass/volume] in Blood). The local partition is coarser than
LOINC's along an axis the local system never needed until the day its
results had to travel. Mapping `GLU` forward therefore requires
information the code does not carry: which kind of glucose *was* this?
Sometimes the surrounding message answers it; sometimes nothing does.
One local code, two LOINC candidates, a side condition reaching outside
the code — the NLM map's whole anatomy, reproduced in miniature in a
two-row site dictionary. Every interface engineer who has "mapped the
locals" by hand has been choosing representatives across a sectionless
span, usually without a record of which choices were forced and which
were guesses. Chapter 23's corpus discipline applies with full force
here: the local-to-standard map is itself a transform input, and its
guessed rows are exactly where golden cases earn their keep.

> **Aside, for the categorically inclined.** Chapter 31's aside ended at
> this chapter's doorstep: an adjunction needs a best approximant to
> round-trip through, and cross-terminology maps are the case where no
> best approximant exists — no universal property singles out one
> ICD-10-CM code as *the* reflection of a SNOMED CT concept. With the
> adjunction gone, the honest structure is the bare span, and a
> crosswalk is a **tabulated relation**: the span's apex is the set of
> (source, target) pairs the stewards will vouch for, with its two
> projections. Composing crosswalks is span composition by pullback,
> which is exactly why chaining them widens candidate sets and conjoins
> side conditions rather than cancelling them. A section of the span
> would be a splitting of one projection; the text's claim "no
> completion of the table fills this in" is the observation that the
> projection admits no splitting — not for want of rows, but because
> the two quotients of the apex are incomparable. Pointer: relations as
> spans, and allegories, in Freyd–Scedrov; the Galois-connection
> contrast is Chapter 31's pointer continued.

## UMLS as terminology-level universal object

Chapter 32 resolved Part I's failed hub with a change of kind: the thing
every format can map into is not a better format but an event log, and
the formats become views of it. Terminology has a candidate object with
the same silhouette. The National Library of Medicine's **UMLS** — the
Unified Medical Language System — maintains a Metathesaurus that links
names for the same concept across nearly two hundred source
vocabularies, SNOMED CT, LOINC, ICD-10-CM, and RxNorm among them
[@bodenreider2004umls; @umls_uts]. Its unit of linkage is the **CUI**, a
concept unique identifier: one CUI gathers, from every participating
vocabulary, the codes and names that assert the same meaning. Squint and
the echo of Chapter 32 is exact — one central object, everything maps
in, cross-vocabulary questions become questions about the center. The
thesis of this chapter calls UMLS a terminology-level universal object,
and in the maps-in direction the title is earned: as a *directory* of
candidate correspondences — which codes, anywhere, might mean what this
code means? — there is nothing else of its scope, and no serious
cross-terminology test plan should be written without it on the desk.

But the echo breaks down, and where it breaks is as instructive as the
echo itself. The event log earned its universality *by construction*:
formats are views — projections with declared, inspectable loss — so
arrows out of the log exist as first-class citizens and lossiness lives
visibly in them. UMLS's arrows all point *inward*. A CUI is built by
recognizing synonymy among source codes; it certifies that the linked
codes have been judged to mean the same thing, not that any of them may
be substituted for another in a record. Shared CUI membership is not a
section of the span — it does not tell you *which* SNOMED CT code to
emit for an ICD-10-CM code, under what conditions, on whose authority.
Readers of Chapter 11 have seen this shape before: the naive universal
format U, the coproduct that everything maps into and nothing maps
lawfully out of, recurring one level up the stack. UMLS is a magnificent
coproduct. What it is not — and, to its stewards' credit, does not claim
to be — is a translator. The practical consequence for a test plan:
treat UMLS as an *oracle for candidacy*, never for equality. It can tell
you that a translation your pipeline emitted lands inside the source
concept's CUI neighborhood (worth checking; a translation that leaves
the neighborhood is almost certainly wrong). It cannot bless the
translation as correct, because correctness of a crossing is exactly the
information the span withholds and the CUI does not carry. Access terms
are part of planning here, so they are stated once in the reference
entry rather than casually: UMLS requires a license and a UTS account —
free of charge, issued to individuals rather than organizations, with
some uses requiring further agreements with individual vocabulary
owners. Chapter 42's UMLS entry carries the dated specifics.

## ConceptMap, offline gates, and what they predict for testing

If crosswalks are relations with side conditions, then an interoperable
record of a crosswalk had better be able to *say so* — and this is the
genuine achievement of FHIR's **ConceptMap** resource, which is easy to
miss if you read it as just a serialization of a lookup table. A
ConceptMap instance records a mapping between a source system and a
target system as a set of rows, and every row carries, alongside its
source and target codes, an explicit **equivalence annotation**: the map
steward's own statement of how strong this particular correspondence is.
In R4 the annotation lives at `group.element.target.equivalence` and
draws from a ten-code vocabulary — `relatedto`, `equivalent`, `equal`,
`wider`, `subsumes`, `narrower`, `specializes`, `inexact`, `unmatched`,
`disjoint` — read as describing the target relative to the source, so
`wider` means the target concept is wider in meaning than the source
[@fhir_r4_conceptmap]. A plain function signature could never carry this
information: a function's rows are all implicitly `equal`, which is
precisely the claim the previous sections showed no terminology crossing
can make wholesale. A ConceptMap is the relation made data, with a
per-row confession. (R5 renames the element to `relationship` and
rebuilds the vocabulary as five codes with the direction written into
the code names themselves — `source-is-narrower-than-target` and its
mirror — which reads as the standard learning this chapter's honesty
lesson a second time.)

A confession only helps if the check reads it, and here is the failure
mode this section exists to name: a
translation test that asserts plain code equality through a ConceptMap
row *passes when it should never have asserted anything*, because the
map said `inexact` and the test wasn't listening. The companion makes
both the failure and its repair executable. The worked map is the site
dictionary from the previous section, now as data —

```clojure
{:source-system "urn:local:acme-lab"
 :target-system "http://loinc.org"
 :mappings [{:source "GLU"    :target "2345-7" :equivalence :inexact}
            {:source "GLU"    :target "2339-0" :equivalence :inexact}
            {:source "GLU-SP" :target "2345-7" :equivalence :equivalent}]}
```

— three rows of honesty: the site's legacy `GLU` is related to two LOINC
codes and interchangeable with neither, and only the newer,
specimen-specific `GLU-SP` earns `equivalent`. `ehr-testing.conceptmap`
defines `translate` to return *every* row for a source code — the
relation itself, not a silently chosen representative — and
`check-translation` to fold a ConceptMap and one observed translation
into a verdict: `:asserted-equal` or `:wrong` only where the map's
annotation makes equality assertable in the first place,

```clojure
(def equality-safe #{:equivalent :equal})
```

`:flagged` where the map relates the pair under any other annotation
(the verdict carries the annotations and candidate targets outward for a
human or a stricter purpose-set rule to judge), and `:unmapped` where
the relation is silent. The dishonest check — accept any produced code
that appears among the targets, annotations unread — is kept executable
in the test namespace as `dishonest-equal?`, where it duly accepts
`GLU → 2345-7` while the honest check returns `:flagged` for the same
pair; and a generative property (`equality-asserted-iff-annotated-safe`)
pins the law that equality is asserted exactly where the map annotated
it safe, never as a function of the codes' plausibility. The shape of
`:flagged` is worth noticing: it is a check *refusing to decide* and
saying so in its verdict vocabulary, and Chapter 25's validator gates
will need precisely the same third verdict for the same structural
reason.

That reason has now been measured, and it gives this chapter's abstract
map a concrete, tool-drawn boundary. HL7's official FHIR validator
consults a terminology server — by default `tx.fhir.org` — to answer
binding questions, and it can be run with terminology support switched
off entirely (`-tx n/a`). Its documentation states the offline behavior
plainly: value sets defined in the specification itself, or carried in
loaded implementation-guide packages, are still validated locally, but
"external codes are not validated when run like this"
[@hl7_fhir_validator_docs]. Sit that sentence next to this chapter and
the alignment is exact. Small, closed enumerations that ship inside the
spec — administrative gender, status codes, the tables a standard can
carry whole — are the region where a binding check is a genuine
function: total on its domain, decidable from local data alone, and an
offline gate rejects violations outright. Bindings into LOINC, SNOMED
CT, RxNorm — the living, externally stewarded, licensed terminologies
this chapter has been about — are the region where the arrow was never
a function, and there the offline gate does not pretend: it reports
that it could not check, which a test harness must surface as a third
verdict (Chapter 25 again) rather than rounding to pass or fail. A
binary gate over terminology lies in one direction or the other; the
only question is which lie you configured. Even UCUM, Chapter 24 and
42's star exception — unit conversion really is a lawful, algebraic
function — sits on the far side of this line for *membership*: whether
a string is a valid UCUM code is a question about an externally
maintained grammar, not about the spec's own tables, so offline the
gate must flag rather than decide. Function-existence and
domain-decidability are different properties, and a gate only ever
measures the second. One more consequence follows for profile-shaped
testing, flagged here and developed in Chapter 25: profiles tighten
bindings — an implementation guide like US Core turns "example" and
"preferred" bindings into required ones over external terminologies —
so the tower of Chapter 13 (base, profile, version) is also a tower of
*growing* terminology obligations, and the indeterminate region widens
as you climb.

So the thesis pays its way. Terminology maps being sectionless spans is
not a lament; it is a map of where translation defects can live and
therefore where tests belong. Concentrate them, first, at every declared
binding whose code system is external — that is where an offline gate is
structurally silent, so a semantic check (a terminology-server-backed
gate, or a purpose-set property with the value set pinned as a locked
artifact per Chapter 23) has no substitute. Second, at the local
dictionary edge: the site-codes-to-standard-codes map is almost always
the least reviewed and most consequential crosswalk in a pipeline, and
its guessed rows deserve golden cases with hand-authored expected
outputs. Third, at every ConceptMap row whose annotation is anything
other than `equivalent` or `equal` — each such row is a documented site
where the pipeline must be making a choice the map refused to make, so
each one should have a test that knows what choice was made and on what
authority. And fourth, at every chained crossing, because composed
relations lose determinacy silently and the intermediate hop is usually
invisible in the output. None of these places is exotic. They are
exactly where the arrows stop being functions — which is what the
chapter's title promised the structure would predict.
