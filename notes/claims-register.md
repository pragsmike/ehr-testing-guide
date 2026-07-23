# Claims Register

<!-- The sharp things established in design conversations, each tagged with
     its destination chapter and whether it has reached prose yet. When a
     claim lands in a drafted chapter, change status to "in <file>". A claim
     at "transcript-only" exists nowhere durable but here — those are the
     ones to rescue first. Not part of the Pandoc build.

     A second table below, "External factual claims" (F-rows), tracks
     load-bearing, externally verifiable assertions about tools, licenses,
     and ecosystem capabilities — the C-table above tracks whether an
     *insight* has reached prose, not whether a *fact* is still true. Give
     an F-row to any such fact asserted anywhere in the repo (manuscript,
     research/, notes/), with its evidence and a last-verified date; see
     ADR-0013. -->

| # | Claim | Destination | Status |
|---|---|---|---|
| C1 | The naive *U* is a coproduct (maps out, no maps back); the real *U* is a colimit gluing representations along semantic overlaps. | ch 11 | in ch 11 |
| C2 | Retraction laws fail in practice (pandoc raw blocks; FHIR extensions; v2 Z-segments; CDA open templates) — the mapping IGs are a catalog of the breakage. | ch 11, 31 | in ch 11 |
| C3 | Two honest weakenings of the arrow: lenses (asymmetric get/put laws) and spans (shared core D). | ch 31 | in design-rationale |
| C4 | v2=event/delta, FHIR=state/sigma, C-CDA=attested snapshot; a state is a fold of events. Different *kinds*, not dialects. | ch 12 | in ch 12 |
| C5 | A v2→FHIR map is the action of a delta on a state; where a message needs context to determine state change, the arrow does not exist — the IG's struggles are the category reporting this. | ch 12, 34 | in ch 12 |
| C6 | *U* is the bitemporal event log (valid + transaction time); formats are views or ingestions; loss moves from hidden-in-translation to visible-in-views (CQRS). | ch 32 | in design-rationale |
| C7 | C-CDA has a dual-layer representation: authoritative narrative + optional entries, a span with *unchecked legs* — nothing verifies they agree. Any extraction reads only the entries leg and is lossy in the legally salient part. | ch 31, 34 | in notes/design-rationale.md |
| C8 | Regenerating a C-CDA from FHIR is a *new attestation event*, not an inverse — the two directions have different types and are not a lens pair. Some "transformations" are authorship acts that mint provenance. | ch 12, 31 | in ch 12 |
| C9 | C-CDA templates form a profiling tower (base CDA → document → section → entry templates by OID); objects are really (format, template-set, version) triples. | ch 13, 41 | transcript-only |
| C10 | v2 has two-level typing: structural datatypes owned by HL7 (ST, NM, CWE…) with terminologies plugged in as codomains via Table 0396; the coding *system* is part of the value, not the type. Four different authorities set type / system-registry / systems / binding. | ch 33 | transcript-only |
| C11 | OBX-2 makes OBX-5's datatype a runtime value — a dependent sum Σ(t:Datatype)⟦t⟧ in a 1989 wire format; FHIR's value[x] is the same tagged union made explicit. LOINC-in-OBX-3 and the datatype tag are an unchecked-coherence span. | ch 33 | in notes/design-rationale.md |
| C12 | Terminologies (SNOMED, LOINC, ICD, RxNorm) partition clinical reality differently on purpose; official maps are partial, n-to-m, context-dependent *relations* — spans with no section, not functions. | ch 34 | in notes/design-rationale.md |
| C13 | UMLS is the terminology-level universal object (CUIs as equivalence classes over source codes); FHIR ConceptMap is arrows-with-honesty-annotations (equivalent/wider/narrower/inexact). | ch 34, 42 | transcript-only |
| C14 | LOINC=questions, SNOMED often=answers; the question/answer split is the key design idea, and OBX-3(question)/OBX-5(answer) mirrors it. | ch 33, 42 | transcript-only |
| C15 | Correctness = observational (q∘f = q_source); purpose set = spec; correctness is a lattice; residue = declared-irrelevant info, enumerable and reviewed. | ch 21, 22 | in ch 21 |
| C16 | Worked lattice split in the specimen: reference-range/abnormal-flag is load-bearing for the clinician, invisible to the measure; order/result LOINC divergence (1554-5 vs 2345-7) moves a denominator but means nothing to the clinician. | ch 22, §94 | in ch 22 |
| C17 | Three corpus layers (curated golden / generated / controlled mutation) with distinct failure modes; expected outputs encode the spec's blind spots; seeds+versions recorded for reproducibility. | ch 23 | in ch 23 |
| C18 | Property families by arrow kind: round-trip/lens laws, metamorphic relations (order-invariance, translate-then-filter), replay determinism for folds, manual gate for narrative/entries. | ch 24 | in §94 (partial) |
| C19 | Validators (HAPI, NIST v2-validation, validator_cli) are structural conformance gates *upstream* of semantic property tests — necessary, not sufficient. | ch 25, 43 | in ch 43 (NIST/Inferno entries) |
| C20 | Essential vs incidental for the Python reader: schemas-as-data, generative testing, properties are essential; Malli/test.check spelling is incidental. | ch 26 | transcript-only |
| C21 | The meet of two purpose sets (shared queries at stricter tolerances) is the shared core of Part I's span — what recipients jointly require and what formats can be glued along are the same object approached from opposite ends. | ch 22, 31 | in ch 22 |
| C22 | When transform and q-source share a rule's definition, the purpose-set property is blind to bugs in that rule (both sides wrong in unison); golden cases with independently hand-written expected values are the complementary oracle. | ch 23, 24 | in ch 23 |
| C23 | A CQL quality measure is a formalized purpose set: the rare recipient whose queries exist as inspectable code rather than elicitation interviews, so the purpose set can be read from the measure's own logic. | ch 21, 41 | in ch 41 (CQL entry) |
| C24 | Within UCUM, unit arrows are lawful functions (units compose and convert algebraically) — the exception proving Chapter 34's rule that cross-terminology arrows are sectionless spans. | ch 24, 34, 42 | in ch 42 (UCUM entry) |
| C25 | Structural gates are per tower level: base standard, profile, and version are independently violable, so "validates" is meaningless without naming the layer. | ch 25, 13 | in ch 13 |
| C26 | A ConceptMap's per-code equivalence annotations (equivalent / wider / narrower / inexact) are test input: a translation property that asserts plain equality on a non-equivalent mapping passes when it should fail — the map said the honest thing and the check wasn't listening. | ch 24, 34, 42 | in ch 42 (ConceptMap entry) |
| C27 | Emitting deltas from a sigma is invention unless differenced against a baseline: a fold has forgotten its path, so a state-to-message interface with no baseline is minting history, not deriving it. | ch 12, 32 | in ch 12 |
| C28 | Round-trip idempotence (second pass is a no-op) is an executable law distinguishing stabilizing loss from eroding loss; runnable with no categorical vocabulary; its CT reading (closure operator; the retract→lens ladder as weakened adjunctions) is confined to a ch-31 aside. | ch 31 | in notes/design-rationale.md; ch 31 skeleton only |

## External factual claims

| # | Claim | Where asserted | Evidence | Last verified | Status |
|---|---|---|---|---|---|
| F1 | Synthea has no HL7 v2 exporter in mainline (native outputs: FHIR R4/STU3/DSTU2, C-CDA, CSV, CPCDS; the only v2 code was PR #862, closed unmerged; feature request #1561 open, uncommitted). | ch 23 (corrected), research/jvm-clojure-ehr-prior-art-research.md (corrected; see its Corrections section), Experiment 3 | https://github.com/synthetichealth/synthea (export dir); https://github.com/synthetichealth/synthea/pull/862 ; https://github.com/synthetichealth/synthea/issues/1561 | 2026-07-22 | corrected (was asserted as "exports HL7 v2.4 messages") |
| F2 | HAPI HL7v2 is dual-licensed MPL / GPL at the licensee's choice (per pom.xml; license version numbers not asserted). | research/jvm-clojure-ehr-prior-art-research.md (corrected; three locations) | https://raw.githubusercontent.com/hapifhir/hapi-hl7v2/master/pom.xml | 2026-07-22 | corrected (was asserted as "MPL 1.1 / Apache 2.0") |
| F3 | NIST v2-validation ships no license artifact (no LICENSE file, no source headers); public-domain status plausible but unverified. | ch 43 NIST entry (corrected) | https://github.com/usnistgov/v2-validation (root listing) | 2026-07-22 | unverified — pending EXP-SBOM (ehr-testing-tools project); update this row when that experiment resolves it |
| F4 | Synthea's native outputs are FHIR (R4/STU3/DSTU2), Bulk FHIR ndjson, C-CDA, CSV, CPCDS — not OMOP and not HL7 v2; OMOP is produced from the CSV output by OHDSI's ETL-Synthea. | ch 23 (corrected), ch 43 Synthea entry (corrected) | Synthea README; export directory; OHDSI ETL-Synthea repo (links above) | 2026-07-23 | corrected (was asserted as "exportable as … OMOP tables" / "FHIR, C-CDA, and other formats") |
