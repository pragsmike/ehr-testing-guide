<!-- Every entry in this chapter follows manuscript/templates/reference-entry.md -->

# The Standards Family

> **Thesis:** This chapter is the reference companion to Chapter 13's
> orientation lap — one dated, structured entry per standard in the HL7
> family and its profiling towers, answering the same questions in the
> same order so it can be scanned rather than read.

### HL7 v2

| Field | Value |
|---|---|
| What it is | A pipe-and-hat-delimited messaging standard for clinical and administrative events (ADT, ORU, ORM, ...), first published in the late 1980s and still the dominant wire format for real-time hospital interfaces. |
| Publisher / steward | HL7 International |
| Kind of object (in this book's terms) | delta (event) — see Chapter 12 |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | Almost always the source side of a transformation in this book's examples; its segment/field structure (Chapter 33) drives the corpus design in Chapter 23. |
| Where it bites | Z-segments and site-specific table extensions that no downstream format has a slot for (Chapter 31). |
| Entry last verified | 2026-07-21 |

### HL7 v3 / RIM

| Field | Value |
|---|---|
| What it is | HL7's second-generation standards framework (2000s): every artifact derived from a single semantic model, the Reference Information Model (RIM), serialized as XML. As a messaging standard it largely failed on complexity; it survives through its one successful derivative, CDA. |
| Publisher / steward | HL7 International |
| Kind of object (in this book's terms) | a modeling framework, not a wire format — its instances (v3 messages, CDA documents) are the objects; the RIM itself is the shared apex those objects project from |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | Rarely a direct source or target; matters because CDA's generic act/participation structure (everything is an `act`) is inherited from the RIM, so RIM literacy explains why base-CDA validation is so permissive (Chapter 25's gates catch little without templates). |
| Where it bites | Assuming the RIM's semantic ambitions are enforced anywhere — they are not; conformance is carried entirely by the template layer above, not the model below. |
| Entry last verified | 2026-07-21 |

### CDA (base)

| Field | Value |
|---|---|
| What it is | Clinical Document Architecture, Release 2 — an HL7 v3-derived standard for clinical documents: human-readable, attestable, immutable snapshots with optional machine-readable entries. Deliberately generic; almost all real-world constraint comes from template layers above it (C-CDA in the US). |
| Publisher / steward | HL7 International |
| Kind of object (in this book's terms) | attested snapshot — see Chapter 12; distinct from C-CDA, which is a profile tower over it |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The layer at which XSD validation applies (structure only); testing anything clinically meaningful requires the template layer, so a base-CDA-valid document proves very little (Chapter 25). |
| Where it bites | The narrative-vs-entries split originates here, not in C-CDA: the standard itself makes narrative authoritative and enforces no agreement between the legs (Chapter 31). |
| Entry last verified | 2026-07-21 |

### C-CDA

| Field | Value |
|---|---|
| What it is | Consolidated CDA — a US profile of CDA R2 constraining document templates (CCD, discharge summary, and others) for document exchange, required by US regulation for certified EHR technology. |
| Publisher / steward | HL7 International (base CDA and the Consolidated CDA profile); required for certified EHR technology by ONC regulation, but ONC does not publish it |
| Kind of object (in this book's terms) | attested snapshot — see Chapter 12 |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | Source or target for document-level transformations; the narrative-vs-entries split is the canonical example of an information leak in Chapter 31. |
| Where it bites | Human-readable narrative and machine-readable entries can drift apart within the same document; round-trip tests that only check entries miss this. |
| Entry last verified | 2026-07-21 |

### CCD

| Field | Value |
|---|---|
| What it is | Continuity of Care Document — one specific document template *within* C-CDA (a patient-summary snapshot), historically a harmonization of CDA with the defunct ASTM CCR. Frequently confused with C-CDA itself; a CCD is one document type C-CDA defines. |
| Publisher / steward | HL7 International (as a C-CDA document-level template) |
| Kind of object (in this book's terms) | attested snapshot, at a specific node in the C-CDA template tower — an object of the form (format, template-set, version) |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The most common concrete document type in care-summary exchange; a corpus for document transformations (Chapter 23) should say "CCD per C-CDA x.y," never just "a CDA," or the template layer under test is undefined. |
| Where it bites | Template-version coexistence: C-CDA 1.1, 2.0, 2.1 documents all circulate, each declaring its own templateIds, so one "CCD transform" is really several transforms indexed by version. |
| Entry last verified | 2026-07-21 |

### FHIR

| Field | Value |
|---|---|
| What it is | Fast Healthcare Interoperability Resources — a REST/JSON (or XML) resource-based standard for representing current clinical state, now on major version R4/R4B/R5. |
| Publisher / steward | HL7 International |
| Kind of object (in this book's terms) | state (fold of deltas) — see Chapter 12 |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | Almost always the target side of a transformation in this book's examples; `value[x]` typing (Chapter 33) and ConceptMap (Chapter 34) are frequent property-test subjects. |
| Where it bites | "Current state" resources silently discard the event history that produced them unless a system also retains the log (Chapter 32). |
| Entry last verified | 2026-07-21 |

### US Core

| Field | Value |
|---|---|
| What it is | The US-realm FHIR implementation guide profiling base FHIR resources (must-support elements, binding strengths, search expectations) for regulatory use — the FHIR analog of what C-CDA is to CDA. |
| Publisher / steward | HL7 International; anchored in US regulation via ONC certification and information-blocking rules |
| Kind of object (in this book's terms) | profile — a refinement of FHIR's objects; conformance-to-profile is a factorization condition, not a new format |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | Usually the *actual* target of a "to FHIR" transformation in US work; structural gates (Chapter 25) should validate against US Core, not bare R4, or must-support omissions pass silently. |
| Where it bites | "Must support" is a documentation obligation, not a cardinality — validators enforce less of it than teams assume, so profile-valid output can still be missing what a purpose set needs (Chapters 21–22 remain necessary). |
| Entry last verified | 2026-07-21 |

### IHE profiles

| Field | Value |
|---|---|
| What it is | Integrating the Healthcare Enterprise — a separate organization that publishes *profiles* combining other bodies' standards (HL7, DICOM) into deployable workflows: XDS for document sharing, PIX/PDQ for patient-identity cross-referencing, ATNA for audit, and many others. |
| Publisher / steward | IHE International (not HL7) |
| Kind of object (in this book's terms) | workflow composition — arrows-plus-choreography over other standards' objects, not a data format of its own |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | Defines the *context* many transformations run in (which actor sends what, when); IHE's connectathon test tooling (Gazelle) is prior art for corpus design at the workflow level (Chapter 23). |
| Where it bites | Passing an IHE profile's transaction tests proves choreography conformance, not content correctness — a document can arrive perfectly via XDS and still fail every purpose set it meets. |
| Entry last verified | 2026-07-21 |

### SMART on FHIR

| Field | Value |
|---|---|
| What it is | An OAuth2/OpenID-based app-launch and authorization framework atop FHIR, letting third-party applications run against an EHR's FHIR API with user- and patient-scoped permissions. Originated at SMART Health IT; since adopted into HL7 standards and US regulation. |
| Publisher / steward | SMART Health IT / HL7 International |
| Kind of object (in this book's terms) | access machinery — it governs *who may run which queries*, i.e. it scopes the observable queries, without adding data objects of its own |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | Mostly out of scope for transformation testing, with one exception: scopes bound what a recipient application can observe, which can silently truncate the purpose set a team assumed (Chapter 21's elicitation should note the recipient's actual scopes). |
| Where it bites | Testing against an unscoped sandbox and deploying behind narrow scopes — queries that passed in test are unavailable in production, which reads as data loss but is authorization. |
| Entry last verified | 2026-07-21 |

### CQL / CDS Hooks

| Field | Value |
|---|---|
| What it is | Two HL7 standards for computable clinical logic: Clinical Quality Language (CQL), a declarative query language over clinical data used heavily in quality measures, and CDS Hooks, a callback protocol for injecting decision support into EHR workflows. |
| Publisher / steward | HL7 International |
| Kind of object (in this book's terms) | observation languages — CQL programs are literally purpose-set queries (a quality measure *is* a formalized q), which is why Chapter 22's measure recipient is not a metaphor |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | When the downstream recipient is a CQL-based measure, the purpose set can be elicited *from the measure's own logic* — the rare case where the recipient's queries exist as inspectable code rather than interviews (Chapters 21–22). |
| Where it bites | CQL runs over the *transformed* data model; a transform correct for the measure's clinical intent can still fail the measure as written when the CQL's retrieve paths assume elements the transform routed elsewhere. |
| Entry last verified | 2026-07-21 |
