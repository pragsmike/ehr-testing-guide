<!-- Every entry in this chapter follows manuscript/templates/reference-entry.md -->

# Clojure-Reachable Tools

> **Thesis:** This chapter is the reference companion to the tools used or
> referenced throughout the book — what each one is, how it's reached from
> Clojure (native, Java interop, subprocess, or HTTP), and its maintenance
> status, since a tool's usefulness in a test plan depends as much on
> whether it's still maintained as on what it claims to do.

### HAPI FHIR

| Field | Value |
|---|---|
| What it is | The reference Java implementation of FHIR: parsing, validation, and a full server, for R4 and other versions. |
| Publisher / steward | Smile Digital Health (formerly University Health Network) and community contributors |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | Java interop |
| Maintenance status (tools only) | active |
| Role in a test plan | The validator used at the gate described in Chapter 25; also usable to construct and serialize the FHIR side of the specimen. Pulled in via `companion/deps.edn`'s `:hapi` alias, not the base deps — see `companion/README.md`. |
| Where it bites | Heavyweight to resolve and slow to start; kept out of the base alias so `clojure -X:test` stays fast. |
| Entry last verified | 2026-07-21 |

### org.hl7.fhir.core / validator_cli

| Field | Value |
|---|---|
| What it is | HL7's official FHIR reference tooling: the canonical validator (`validator_cli.jar`), the reference FHIRPath engine, the IG Publisher infrastructure, CDA/C-CDA validation via a loaded implementation guide, and cross-version (STU3/R4/R5) conversion. |
| Publisher / steward | HL7 International (developed in the `hapifhir` GitHub organization) |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | subprocess (preferred) or Java interop |
| Maintenance status (tools only) | active |
| Role in a test plan | The profile-conformance gate of Chapter 25 for the FHIR (target) side: validate output against US Core or another pinned IG *upstream* of the semantic properties, never as a substitute for them. Also validates C-CDA when loaded with the `hl7.cda.ccda` package, and supplies the reference FHIRPath engine where a Clojure-native one falls short (see the `clojure-hl7-messaging-2-parser` and Blaze entries for the analogous native-vs-interop choice). |
| Where it bites | By default it reaches the network — IG packages from `packages.fhir.org`, terminology expansion from `tx.fhir.org` — so an unpinned run is not reproducible: the same resource can validate differently as remote packages move. Chapter 23's seeds-and-versions discipline extends here — pre-populate the package cache, pin IG versions, and pass `-tx n/a`, or the validator becomes the "different result across runs" failure mode itself. |
| Entry last verified | 2026-07-21 |

### HAPI HL7v2

| Field | Value |
|---|---|
| What it is | The standard JVM library for HL7 v2: tolerant and strict parsers, versioned message and segment models (v2.1–v2.8), MLLP transport, and custom Z-segment support. |
| Publisher / steward | Smile Digital Health and community contributors (the HAPI project, sibling to HAPI FHIR) |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | Java interop |
| Maintenance status (tools only) | stable |
| Role in a test plan | Parses the v2 ORU source side of the specimen from the JVM; its versioned segment models are how OBX-2's runtime datatype tag and OBX-5's dependent-sum value (Chapter 33) are reached in code. The tolerant-versus-strict parser choice is itself a gate decision (Chapter 25) — the tolerant parser admits non-conformant messages the strict one rejects. |
| Where it bites | Conformance-profile validation is limited: the parser checks structure against the version model, but site profiles, Z-segment semantics, and CE/CWE vocabulary bindings are not enforced — so "it parsed" is a structural fact well short of the observational correctness Chapters 21–22 require. Serious profile enforcement needs a custom validator layer over the parsed message, or the NIST tooling below. |
| Entry last verified | 2026-07-21 |

### clojure-hl7-messaging-2-parser

| Field | Value |
|---|---|
| What it is | A small, MIT-licensed Clojure-native HL7 v2 parser that returns messages as Clojure maps (segments, fields, delimiters), with helpers for extracting field values and building acknowledgments. |
| Publisher / steward | Christopher Miles (cmiles74); published as `com.nervestaple/hl7-parser` on Clojars |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | native Clojure |
| Maintenance status (tools only) | dormant (functional; last release October 2023) |
| Role in a test plan | The lightweight option for reading the v2 source side entirely in Clojure data, with no Java-interop boundary — well suited to the companion's parse-and-extract shape when conformance validation and MLLP are out of scope. |
| Where it bites | No conformance-profile validation, no MLLP, no versioned message models — it parses *structure*, not the *standard*, so it cannot be the gate of Chapter 25. For versioned models or profile checking, HAPI HL7v2 via Java interop is the more capable choice; treat this as a parser, not a validator. |
| Entry last verified | 2026-07-21 |

### NIST v2-validation

| Field | Value |
|---|---|
| What it is | NIST's HL7 v2 conformance-testing tooling: a JVM library (`gov.nist:hl7-v2-validation`) for parsing and validating v2 messages against HL7 conformance profiles, plus hosted validators (the General Validation Tool) and the domain test suites — Immunization, Syndromic Surveillance, Laboratory — used in US EHR certification. |
| Publisher / steward | US National Institute of Standards and Technology (NIST), Systems Interoperability Group; public domain |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | Java interop (the `gov.nist:hl7-v2-validation` library) or HTTP (the hosted GVT and test-suite portals) |
| Maintenance status (tools only) | active |
| Role in a test plan | The profile-conformance gate for the v2 (source) side: where HAPI HL7v2 checks structure against the version model, NIST validates against a *named* HL7 conformance profile, enforcing exactly the profile layer Chapter 25 insists you name before saying "it validates." Its context-free versus context-based split — technical conformance versus directed test cases with example messages — is prior art for Chapter 23's generated-versus-curated corpus distinction. Reachable as a JVM library, unusually for a conformance suite; most (Inferno, Gazelle) are external processes only. |
| Where it bites | Profile conformance is still structural: a message can pass a NIST profile and still misrepresent the source under a purpose set (Chapters 21–22) — the tool answers "does this conform to the profile?", never "is the transform correct?" And the profile is a version-pinned artifact — the certification suites are dated (2015 versus 2024 SVAP editions), so "passes NIST" means nothing without naming which suite and profile version, Chapter 23's versions-as-first-class-artifacts point again. The Maven coordinates have not been verified to resolve from Central — confirm them the first time the library is actually pulled in, and be prepared to hunt (CDC's `lib-hl7v2-nist-validator` wrapper bundles the NIST jars as a local Maven repository, which is one fallback route to the artifacts). |
| Entry last verified | 2026-07-21 |

### Inferno

| Field | Value |
|---|---|
| What it is | A FHIR conformance-testing framework with executable test suites for US Core, SMART on FHIR, and the ONC (g)(10) Standardized API certification requirements. |
| Publisher / steward | The Inferno Framework project, developed for the US ONC by MITRE |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | subprocess or HTTP (a Ruby application, driven as an external process or over its HTTP API — not a JVM library) |
| Maintenance status (tools only) | active |
| Role in a test plan | The conformance-suite layer above a FHIR endpoint: it exercises US Core, SMART, and (g)(10) behaviors against a running server, complementing — not replacing — the transform-level properties of Chapter 24, and standing as prior art for corpus design at the API-conformance level (Chapter 23). |
| Where it bites | It certifies structural and API conformance, not content correctness — a server can pass every Inferno test and still transform a source message into a profile-valid resource that fails a purpose set (Chapters 21–22). Being off-JVM (Ruby), it is reached as an external process, not embedded, so it lives in CI orchestration rather than in `companion/`. |
| Entry last verified | 2026-07-21 |

### Malli

| Field | Value |
|---|---|
| What it is | A data-driven schema library for Clojure — schemas are plain data (vectors/maps), not classes or macros, and are usable for validation, coercion, and generation. |
| Publisher / steward | Metosin |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | native Clojure |
| Maintenance status (tools only) | active |
| Role in a test plan | Defines the schemas in `companion/src/ehr_testing/schemas.clj` and drives the generator-based property in `properties.clj` via `malli.generator`. |
| Where it bites | Schema-as-data is powerful but means schema bugs are runtime data bugs, not compile-time type errors — lean on the generator round-trip property to catch them. |
| Entry last verified | 2026-07-21 |

### test.check

| Field | Value |
|---|---|
| What it is | Clojure's property-based testing library: randomized generators, the `for-all` property machinery, and automatic shrinking of a failing case to a minimal reproduction. |
| Publisher / steward | The Clojure core team (a `clojure/` contrib library) |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | native Clojure |
| Maintenance status (tools only) | stable |
| Role in a test plan | The engine under Chapter 24's property families — each purpose-set row compiles to a `for-all` over generated ORU inputs, which is how Chapter 21's `oru->observation` triangles run in bulk in the companion. Malli's generators feed it (see the Malli entry), and its shrinking turns a failing generated case into a minimal reproduction — the machinery that makes Chapter 23's generated layer actionable rather than merely voluminous. |
| Where it bites | A property is only as good as its generator: an under-constrained generator passes *vacuously* (it never produces the input that would break the transform), an over-constrained one hides real failures — the same "commuting triangles for the wrong queries" hazard Chapter 21 names, relocated to the generator. And test.check checks properties, not oracles: where transform and check share a rule (Chapter 23's oracle-circularity point), a green property proves consistency, not correctness — the golden layer is the complementary check. |
| Entry last verified | 2026-07-21 |

### Synthea

| Field | Value |
|---|---|
| What it is | An open-source synthetic patient population generator that produces realistic (but not real) longitudinal patient records in FHIR (R4, STU3, DSTU2), C-CDA, CSV, and CPCDS formats. |
| Publisher / steward | The MITRE Corporation and contributors |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | subprocess |
| Maintenance status (tools only) | active |
| Role in a test plan | The generated-cases layer of the corpus in Chapter 23, before controlled mutation is applied. |
| Where it bites | Synthea's clinical realism is bounded by its modules — it will not generate a case class its modules don't model, so mutation is still required for defect coverage. |
| Entry last verified | 2026-07-23 |

### XTDB

| Field | Value |
|---|---|
| What it is | A bitemporal, immutable JVM database with a native Clojure API: every row carries both valid time (when a fact was true in the world) and transaction time (when the system recorded it), queryable as-of any point on either axis. |
| Publisher / steward | JUXT |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | native Clojure |
| Maintenance status (tools only) | active |
| Role in a test plan | The concrete substrate for Part III's event-log-as-universal-object (Chapter 32): where the book argues the honest *U* is a bitemporal log and the formats are folds or views of it, XTDB is the reachable-from-Clojure realization — it stores the append-only history and answers "what did the system know at time T?" versus "what was clinically true at time T?" directly. Useful for building the amendment and correction cases (Chapter 23) that a current-state store cannot represent. |
| Where it bites | The v2 API changed substantially from v1 (the SQL-versus-Datalog interface, the operational model), so examples and operational knowledge don't transfer cleanly across that boundary — pin the major version in reproducibility metadata as you would an IG. And bitemporality is cheap to adopt in a fresh design but very expensive to retrofit onto a current-state store, which is precisely the trap Chapter 32 exists to warn against. |
| Entry last verified | 2026-07-21 |

### Blaze

| Field | Value |
|---|---|
| What it is | A production FHIR R4 server implemented natively in Clojure, with an embedded CQL engine, RocksDB storage, and Integrant-managed components; used in the German Medical Informatics Initiative. |
| Publisher / steward | Samply (German Biobank Alliance) |
| Kind of object (in this book's terms) | tool |
| Interop mode (tools only) | HTTP (run as a FHIR server and reached over REST; Clojure internally, but not factored as an embeddable library) |
| Maintenance status (tools only) | active |
| Role in a test plan | Two roles. As a *tool*, a real FHIR R4 endpoint to load a corpus into and evaluate CQL measures against — its `$evaluate-measure` path makes Chapter 22's quality-measure recipient executable rather than hypothetical. As *prior art*, the most significant native-Clojure evidence that this book's method is idiomatic in Clojure: pure functions, `clojure.spec`, anomalies instead of exceptions, and Integrant lifecycle — worth reading before building any Clojure healthcare service. |
| Where it bites | It is a complete server, not a library — the storage layer and CQL engine are not independently factored, so reuse means reading source, not adding a dependency. Running it in earnest requires RocksDB operational knowledge, and its R4 coverage, though high, is a subset — confirm the resources and search parameters your corpus needs are supported before relying on it as a fixture. |
| Entry last verified | 2026-07-21 |
