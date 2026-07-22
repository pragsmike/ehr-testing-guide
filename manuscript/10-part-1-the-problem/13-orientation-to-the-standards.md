# Orientation to the Standards

> **Thesis:** This chapter gives just enough of the standards landscape to
> read Parts II and III without stopping to look things up — HL7
> International's generations (v2, v3/RIM, CDA, FHIR), the profiling towers
> built on top of them (C-CDA over CDA, US Core over FHIR), and
> terminologies as a layer that sits beside all of it rather than inside
> any one of them. It is deliberately thin. The full reference — every
> standard, every profile, every tool, each with a verification date — is
> Part IV; this chapter's job is orientation, not completeness.

<!-- Sync: this chapter cites no code and creates no companion obligation.
     Acronym sync: verify CCR, ASTM, RIM, XDS, NEMA, DICOM, X12, openEHR,
     OMOP present in 92-acronyms.md. Cross-refs assume the ch 41/42 entries
     (including the four kind-classifications beyond the original enum) have
     landed. -->

## HL7 International and its generations

Nearly every standard in this book comes from one publisher, and the
publisher's name is the first stumbling block. **HL7** is Health Level
Seven International, a standards organization founded in 1987 — the
"seven" is the application layer of the OSI networking model, a fossil of
the era's preoccupations. But "HL7" is also what everyone calls the
organization's oldest product, the version 2 messaging standard, so the
word means both a body and a wire format depending on the sentence. This
chapter uses *HL7 International* for the organization and *v2* for the
messaging standard, and the reader should expect the field to be less
careful.

The organization's output comes in generations, and the generations are
successive *attempts*, not versions of one design. **v2** (1989 onward) is
the workhorse: terse, pipe-delimited messages announcing that something
*happened* — a patient was admitted, an order was placed, a result came
back. It is old, ugly, underspecified at the edges, and it still carries
the bulk of real-time traffic inside hospitals, because it is everywhere
and it works. **v3** (2000s) was the ambitious replacement: derive every
message from a single grand semantic model, the Reference Information
Model (RIM), and serialize it as XML. As a messaging standard, v3 failed —
the complexity was fatal to adoption — and the reader will meet it in this
book almost exclusively through its one thriving descendant. That
descendant is **CDA**, the Clinical Document Architecture: a v3-derived
standard not for messages but for *documents* — human-readable clinical
snapshots that a person attests to and signs, built on the RIM's generic
machinery and deliberately open-ended on its own. Then, after a decade of
watching v3 struggle, HL7 International reset. **FHIR** (2011 onward,
pronounced "fire") abandoned the derive-everything-from-the-RIM program
for pragmatic, web-native design: discrete resources — a Patient, an
Observation, a MedicationRequest — exchanged as JSON or XML over REST,
modeling the current *state* of things rather than the events that
produced them.

So the lineage, in one pass: one organization; a first generation of
event messages that never went away; a second generation that died as
messaging but lives on as the document standard; and a third generation,
now dominant for new work, built on web idioms and resource states. Three
kinds of artifact — messages about events, signed documents, stateful
resources — and those are three genuinely different *kinds* of thing, not
three syntaxes for one thing — the previous chapter made that distinction
precise, and its consequences run through the whole book.

Two boundary notes keep the map honest. Not everything healthcare-shaped
is HL7 International's: imaging (DICOM) belongs to NEMA, claims (X12) to
an ANSI committee, and rival record-modeling approaches (openEHR) and
analytics models (OMOP) to their own communities — this book touches them
only where a transformation crosses their borders. And within the HL7
family there are smaller siblings the reference covers — SMART's
app-authorization machinery, CQL's computable clinical logic, IHE's
workflow profiles assembled *from* HL7 and DICOM parts — that this
chapter leaves to their Part IV entries.

## The profiling towers

None of the three generations is used bare. Each base standard is
deliberately permissive — v2 leaves fields optional and tables open, CDA
will validate almost any well-formed act structure, FHIR marks nearly
everything cardinality-zero — because each must serve every country,
specialty, and workflow at once. Real interoperability happens one level
up, where a **profile** takes the permissive base and constrains it:
*this* field is required here, *this* code system binds there, *these*
templates apply. The two towers the US reader lives under: **C-CDA**
(Consolidated CDA) stacks template constraints on base CDA to define
concrete document types — the Continuity of Care Document, the discharge
summary — and is what US regulation actually requires when it requires
"CDA"; **US Core** does the same to FHIR, profiling base resources with
must-support elements and binding strengths, and is what US regulation
means when it says "FHIR." (v2 has the same structure less formally: its
conformance profiles and the implementation guides that accompany
regulation play the profile role.)

The practical consequence is a habit of speech this book will enforce:
"the standard" is almost always *the standard plus a profile plus a
version*, and naming all three is not pedantry. A document is not "a
CDA"; it is a CCD under C-CDA 2.1, declaring specific template
identifiers. A resource is not "FHIR"; it is an Observation under US Core
against FHIR R4. Every layer of the tower adds constraints an instance
can satisfy or violate independently, which means every layer is a
distinct thing to validate against — and when Part II builds structural
gates (Chapter 25), the gates are per-layer, and when it builds a corpus
(Chapter 23), the corpus must say which tower level it exercises, or the
tests are checking against an undefined target.

## Terminology as a separate layer

Everything so far concerns *containers* — the shapes of messages,
documents, and resources. What fills their coded slots comes from
somewhere else entirely. **SNOMED CT** (clinical concepts), **LOINC**
(identities of observations — the *question* a lab result answers),
**ICD** (diagnoses, shaped for statistics and billing), **RxNorm**
(medications), and their smaller kin are published by separate
organizations on separate schedules under separate licenses — SNOMED
International, the Regenstrief Institute, the WHO and US agencies, the
National Library of Medicine. No HL7 standard contains them; every HL7
standard *points into* them, through a binding that says "this field
draws from that system."

The independence buys exactly what it costs. What it buys: the same
clinical vocabulary can fill a v2 message today, a C-CDA document
tomorrow, and a FHIR resource next year — meaning outlives container, and
a laboratory's LOINC catalog survives every interface migration around
it. What it costs: the vocabularies were built by different communities
for different purposes, so they partition clinical reality along
genuinely different lines, and no container standard can reconcile them —
translation between terminologies is its own hard problem, living below
every format-level transformation and unsolved by any of them. A
transform can move a coded field flawlessly between containers and still
deliver the wrong code for the recipient's purpose; the coded field's
journey and the code's meaning are separate concerns, tested separately.
Chapter 34 gives the structural account of why terminology translation
resists being a lookup table; Chapter 42 is the layer's reference.

## What this chapter deliberately skips

Nearly everything, on purpose. The RIM's internal structure, the
mechanics of CDA templates and their identifiers, FHIR's resource
lifecycle and search semantics, v2's encoding rules, terminology-server
operations, the licensing terrain — each matters exactly when a
transformation touches it, and each has a dated entry in Part IV rather
than a paragraph here. Two omissions deserve explicit flags because
readers ask: this chapter has said nothing about *how* any of these are
tested — that is the entire business of Part II — and nothing about *why*
the generations resist unification into one canonical format — that
argument is Chapters 11 and 12, already behind the front-to-back reader
and worth revisiting for the reader who jumped here first.

## Where to go for more

The intended reading pattern is a loop, not a sequence. Read Parts II and
III with Part IV open as a companion: on meeting an unfamiliar standard,
read its reference entry — each answers the same questions in the same
order (what it is, who stewards it, what kind of object this book takes
it to be, where it bites in testing) and carries the date its claims were
last verified — then return. Chapter 41 covers the standards family this
chapter sketched; Chapter 42 the terminology layer; Chapter 43 the tools
that parse, validate, and generate all of it. The entries are written to
be scanned in under a minute, because that is the honest unit of
attention a reference gets mid-task; anything needing more than a minute
belongs to the chapters, and the entries say which one.
