<!-- Every entry in this chapter follows manuscript/templates/reference-entry.md -->

# The Terminology Layer

> **Thesis:** This chapter is the reference companion to Chapter 34 — one
> dated, structured entry per terminology and terminology-adjacent artifact,
> so a reader can look up what a code system is *for* without re-deriving
> the partition-of-reality argument from Chapter 34 each time.

### SNOMED CT

| Field | Value |
|---|---|
| What it is | A large, compositional clinical terminology covering conditions, procedures, findings, and body structures, organized as a description-logic ontology. |
| Publisher / steward | SNOMED International |
| Kind of object (in this book's terms) | terminology |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | Codomain for clinical-concept fields (conditions, procedures) reached via bindings like those in Chapter 33's OBX-2/OBX-5 discussion. |
| Where it bites | Post-coordination (building a concept from parts) means "the same clinical idea" can have more than one valid SNOMED representation. |
| Entry last verified | 2026-07-21 |

### LOINC

| Field | Value |
|---|---|
| What it is | A terminology for identifying laboratory and clinical observations — the "what was measured," independent of the result value. |
| Publisher / steward | Regenstrief Institute |
| Kind of object (in this book's terms) | terminology |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The code system for OBX-3 in this book's ORU specimen (Chapter 33) and for `Observation.code` in the target FHIR resource. |
| Where it bites | Local lab codes mapped to LOINC outside any published map (Chapter 34's "spans with no section") are a leading cause of silent translation failure. |
| Entry last verified | 2026-07-21 |

### UMLS

| Field | Value |
|---|---|
| What it is | The Unified Medical Language System — a meta-thesaurus linking SNOMED, LOINC, ICD, RxNorm, and dozens of other vocabularies via shared concept identifiers (CUIs). |
| Publisher / steward | US National Library of Medicine (NLM) |
| Kind of object (in this book's terms) | terminology (terminology-level universal object — see Chapter 32's echo in Chapter 34) |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | A cross-check when validating a ConceptMap between two terminologies that don't publish a direct map to each other. |
| Where it bites | A shared CUI does not imply a safe 1:1 clinical equivalence — UMLS links concepts, it does not certify interchangeability. |
| Entry last verified | 2026-07-21 |

### ICD-10 (CM / PCS)

| Field | Value |
|---|---|
| What it is | A terminology for diagnoses (ICD-10-CM) and inpatient procedures (ICD-10-PCS), shaped primarily for statistical reporting and billing rather than clinical description. |
| Publisher / steward | World Health Organization (base ICD-10); US National Center for Health Statistics (ICD-10-CM) and CMS (ICD-10-PCS) for the US clinical modifications |
| Kind of object (in this book's terms) | terminology |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The code system for diagnosis/procedure fields in claims- and billing-adjacent transformations; distinct from SNOMED CT even where both describe the same clinical fact, because ICD's axis is reimbursement categories, not clinical concepts. |
| Where it bites | A SNOMED-to-ICD mapping is lossy in a specific direction: ICD's categories are coarser and reimbursement-driven, so round-tripping a diagnosis through ICD and back loses clinical specificity that every purpose set about care would miss, even though a billing-only purpose set never would. |
| Entry last verified | 2026-07-21 |

### CPT

| Field | Value |
|---|---|
| What it is | Current Procedural Terminology — a proprietary code set for medical, surgical, and diagnostic procedures, used primarily for professional billing in the US. |
| Publisher / steward | American Medical Association (AMA) |
| Kind of object (in this book's terms) | terminology |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The code system for procedure/service fields in US billing-facing transformations; licensed, unlike most of this book's other terminologies, which affects what a corpus (Chapter 23) may redistribute. |
| Where it bites | CPT's licensing terms restrict republishing the code list itself, so test fixtures built from CPT often ship as opaque codes with no local description table — a corpus detail easy to miss until a legal review catches it. |
| Entry last verified | 2026-07-21 |

### RxNorm

| Field | Value |
|---|---|
| What it is | A normalized naming system for clinical drugs, linking branded and generic names, ingredients, and dose forms to a common set of concept identifiers (RXCUIs). |
| Publisher / steward | US National Library of Medicine (NLM) |
| Kind of object (in this book's terms) | terminology |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The code system for medication fields (`MedicationRequest.medication`, v2 RXA/RXE segments); its layered structure (ingredient / clinical drug / branded drug) means a single medication concept has several valid RxNorm codes at different specificities. A medication purpose set must therefore state which grain its queries compare at, or "same drug" is ambiguous between ingredient-level and package-level agreement. |
| Where it bites | Mapping a local formulary code to RxNorm at the wrong layer (e.g. ingredient instead of the specific branded drug administered) silently changes what a downstream query about "which drug" is answering. |
| Entry last verified | 2026-07-21 |

### UCUM

| Field | Value |
|---|---|
| What it is | The Unified Code for Units of Measure — a formal, machine-parseable grammar for units (e.g. `mg/dL`, `mmol/L`), letting units be validated and converted algorithmically rather than looked up in a table. |
| Publisher / steward | Regenstrief Institute |
| Kind of object (in this book's terms) | terminology — but a structural exception among this chapter's terminologies: UCUM's units compose and convert by lawful algebraic rules, so translation between UCUM units is a genuine function, not the sectionless span Chapter 34 shows for concept terminologies. |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The unit system for `valueQuantity.unit`/`.code` in this book's specimen (Chapter 21's units row); because conversion is algebraic, a units property can assert *correct conversion*, not just *same normalized string* — a strictly stronger check available nowhere else in this chapter. |
| Where it bites | UCUM's algebraic lawfulness is easy to over-generalize from: it tempts teams into assuming *all* terminology translation is this well-behaved, which Chapter 34's account of SNOMED/LOINC/ICD explicitly is not. |
| Entry last verified | 2026-07-21 |

### CVX

| Field | Value |
|---|---|
| What it is | Vaccines Administered — a CDC-maintained code system identifying vaccine products (as distinct from the manufacturer, which is MVX) for immunization messaging. |
| Publisher / steward | US Centers for Disease Control and Prevention (CDC) |
| Kind of object (in this book's terms) | terminology |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The code system for the vaccine-product field in immunization messages (v2 RXA-5) and FHIR Immunization resources; new codes are added as new vaccine formulations reach market, so a corpus's CVX coverage ages faster than most terminologies in this chapter. |
| Where it bites | A CVX code and its paired MVX (manufacturer) code answer different questions — *which product* versus *who made it* — and treating either as sufficient on its own drops half of what an immunization registry's purpose set needs. |
| Entry last verified | 2026-07-21 |

### ConceptMap (as a FHIR artifact type)

| Field | Value |
|---|---|
| What it is | A FHIR resource type that records a translation relation between codes in a source system and codes in a target system, with an explicit equivalence annotation per pair (equivalent, wider, narrower, inexact, ...) rather than asserting a bare function. |
| Publisher / steward | HL7 International (the resource type); individual ConceptMap instances are published by whoever maintains the mapping (terminology stewards, implementation guides, or local sites) |
| Kind of object (in this book's terms) | a span, made data — the formalized recording of the arrows-with-honesty-annotations relation Chapter 34 describes for terminologies that partition reality differently; distinct from the terminologies it maps between, which are this chapter's terminology entries proper |
| Interop mode (tools only) | n/a |
| Maintenance status (tools only) | n/a |
| Role in a test plan | The artifact a translation property test should validate *against* rather than reimplement — asserting equivalence via a published ConceptMap's own annotations, rather than hand-rolling an equality check that silently assumes an equivalence the map itself does not claim. |
| Where it bites | Reading a `wider`/`narrower`/`inexact` mapping entry as if it were `equivalent` is a common way a translation property passes when it should fail — the ConceptMap said the honest thing, and the code checking it wasn't listening. |
| Entry last verified | 2026-07-21 |
