# JVM and Clojure EHR Prior Art and Open-Source Ecosystem

> **Research Date:** July 12, 2026
> **Audience:** Engineers evaluating reusable mechanisms, libraries, servers, validators, generators,
> reference implementations, and architectural patterns for implementing healthcare and EHR
> functionality in Clojure or Java on the JVM.
> **Scope:** This is a prior-art survey, not a product recommendation. It does not assume any
> particular organization, deployment environment, or existing architecture.

---

## Corrections

*(Added 2026-07-22, after spot-checking this survey against primary sources.
The body text below is corrected in place; this note records what changed
and why.)*

- **Synthea does not export HL7 v2.** Mainline Synthea (latest release
  v4.0.0, 2026-03-05) has no HL7 v2 exporter — its `export/` source
  directory contains FHIR R4/STU3/DSTU2, C-CDA, CSV, CPCDS, CDW, JSON,
  text, and RIF exporters only. The only v2 code ever proposed was
  [PR #862](https://github.com/synthetichealth/synthea/pull/862) (2021,
  ~626-line `HL7V2Exporter.java`), closed unmerged; its author later called
  it "a bit of a hack and not suitable for inclusion."
  [Issue #1561](https://github.com/synthetichealth/synthea/issues/1561)
  (opened 2025-02-12) requests v2 export and remains open with no
  maintainer commitment. This corrects the "HL7 v2.4 messages" bullet
  under Synthea's generated outputs and the Experiment 3 data source
  below.
- **HAPI HL7v2's license is dual MPL/GPL, not "MPL 1.1 / Apache 2.0."**
  The [HAPI HL7v2 `pom.xml`](https://raw.githubusercontent.com/hapifhir/hapi-hl7v2/master/pom.xml)
  states: "HAPI is dual licensed under both the Mozilla Public License and
  the GNU General Public License. What this means is that you may choose
  to use HAPI under the terms of either license." A consumer may elect the
  MPL terms, so GPL obligations are not mandatory for downstream users.
  Version numbers for either license are not asserted here, since the pom
  text fetched does not state them.

## Executive Summary

### Strongest Reusable Libraries
- **[HAPI FHIR](https://hapifhir.io/)** (Java, Apache 2.0): The most complete JVM FHIR library. Covers parsing, serialization, validation, FHIRPath, JPA server, client, SMART on FHIR scaffolding, terminology. Unavoidable for serious JVM FHIR work.
- **[HAPI HL7v2](https://hapifhir.github.io/hapi-hl7v2/)** (Java, dual MPL/GPL, licensee's choice): The standard JVM HL7 v2 library. Tolerant and strict parsers, MLLP, message models.
- **[org.hl7.fhir.core / validator_cli](https://github.com/hapifhir/org.hl7.fhir.core)** (Java, Apache 2.0): The official HL7 validator. Profile validation, IG loading, CDA validation via `validator_cli.jar`.
- **[CQL Evaluation Engine](https://github.com/cqframework/clinical_quality_language)** (Java/Kotlin, Apache 2.0): HL7-endorsed Java CQL compiler and ELM runtime for quality measures and CDS.
- **[dcm4che](https://github.com/dcm4che/dcm4che)** (Java, LGPL 2.1): The JVM standard for DICOM. DICOM parsing, network services, IHE actors.

### Strongest Reference Implementations
- **[Blaze](https://github.com/samply/blaze)** (Clojure, Apache 2.0): The most important native-Clojure prior art. A production FHIR R4 server with embedded CQL engine, used in German Medical Informatics Initiative.
- **[HAPI FHIR JPA Server](https://github.com/hapifhir/hapi-fhir-jpaserver-starter)** (Java, Apache 2.0): The most widely deployed open-source FHIR JPA persistence layer.
- **[Medplum](https://github.com/medplum/medplum)** (TypeScript/Node, Apache 2.0): FHIR-native developer platform. Not JVM, but demonstrates full FHIR application architecture including auth, subscriptions, scheduling, and compliance tooling.

### Strongest External Tools
- **[Synthea](https://github.com/synthetichealth/synthea)** (Java, Apache 2.0): The standard synthetic patient generator. Reproducible, modular, FHIR R4 export.
- **[Inferno](https://github.com/inferno-framework)** (Ruby, Apache 2.0): ONC-endorsed FHIR conformance testing framework for SMART on FHIR, US Core, and IGs.
- **[validator_cli.jar](https://github.com/hapifhir/org.hl7.fhir.core/releases)**: Official HL7 FHIR validator. Usable as a subprocess from Clojure.
- **[Gazelle](https://gazelle.ihe.net/)** (Java, Apache 2.0): IHE's web-based test bed for connectathon-level interoperability testing.

### Promising Clojure-Native Work
- **[Blaze](https://github.com/samply/blaze)**: Production Clojure FHIR server. Uses Integrant, Clojure spec, RocksDB, pure functions. Study its architecture.
- **[fhirpath.clj](https://github.com/HealthSamurai/fhirpath.clj)**: FHIRPath in Clojure by Health Samurai. Low activity but functional.
- **[clojure-hl7-messaging-2-parser](https://github.com/cmiles74/clojure-hl7-messaging-2-parser)**: Small, MIT-licensed Clojure HL7 v2 parser. Last release October 2023.
- **[sql-on-fhir.clj](https://github.com/HealthSamurai/sql-on-fhir.clj)**: SQL-on-FHIR ViewDefinition in Clojure by Health Samurai.
- **[fhir.clj](https://github.com/fhirbase/fhir.clj)**: Clojure FHIR client. Dormant but instructive.

### Projects to Study but Not Adopt
- **OpenMRS / Bahmni**: Comprehensive Java EHR systems; study their domain models, concept dictionaries, encounter models, and concept-to-standard-term mapping patterns.
- **VistA / WorldVistA**: Historically influential; the data model is instructive for longitudinal clinical records, but the MUMPS runtime is irrelevant to JVM work.
- **EHRbase**: Java openEHR server with REST API; study for archetype-driven clinical modeling as a contrast to FHIR resources.
- **LinuxForHealth FHIR Server**: Last release December 2022 (5.1.1). Dormant. Study its modular `fhir-model` and `fhir-search` library architecture as prior art.
- **Aidbox / fhirbase**: Study Health Samurai's approach to FHIR-on-PostgreSQL, FHIRPath-to-SQL, and ZTDB-style versioning. Aidbox is commercial; fhirbase is open source.

### Projects to Avoid or Treat as Dormant
- **clj-hl7-fhir** (gered/clj-hl7-fhir): Last commit 2014. Abandoned.
- **Eclipse OHF**: Eclipse's Open Healthcare Framework. Effectively abandoned post-2009.
- **LinuxForHealth FHIR Server**: No releases since December 2022.
- **OpenCDS**: Website last updated January 2026 but no active open-source release cadence visible.

### Major Gaps in the Ecosystem
- No mature, maintained, purely native Clojure FHIR parsing/validation library exists. Java interop via HAPI FHIR is the responsible choice.
- No Clojure-native FHIRPath implementation with active maintenance.
- No Clojure-native HL7 v2 library covering conformance profile validation.
- No Clojure-native terminology server or embedded SNOMED/LOINC client.
- No Clojure SMART on FHIR / OAuth2 / OpenID Connect reference implementation.
- No Clojure-native bitemporal clinical record library (though XTDB provides the substrate).

---

## Scope and Method

**Research boundaries:** This survey covers public repositories, official documentation, Maven Central, Clojars, HL7 International specifications, IHE, NIST, and secondary sources as of July 12, 2026. No private systems, paid access repositories, or proprietary vendor systems were accessed. The report focuses on what is reusable, embeddable, or instructive for JVM/Clojure healthcare engineering. It does not recommend building a complete EHR from scratch.

**Source quality standard:** Primary sources (official GitHub repositories, official documentation, Maven Central release metadata, HL7 and IHE specifications) are preferred. Secondary sources (blog posts, conference slides, secondary adoption analyses) are used only for implementation experience and known limitations.

**Maintenance classification labels used:**
- **active**: Regular releases, active maintainer engagement, open issues being closed.
- **stable and maintained**: Infrequent but deliberate releases; a mature project in a stable state.
- **maintenance-only**: Bug fixes and security patches only; no new features.
- **low activity but viable**: Sparse commits but technically sound and usable.
- **dormant**: No releases in 18–36 months; may still work.
- **abandoned**: No activity, maintainer unresponsive, or explicitly archived.
- **unclear**: Insufficient evidence to classify.

---

## Ecosystem Map

| Category | Key Projects |
|---|---|
| Standards libraries (Java) | HAPI FHIR, HAPI HL7v2, org.hl7.fhir.core, LinuxForHealth fhir-model (dormant), dcm4che |
| FHIR validators | org.hl7.fhir.core / validator_cli, HAPI FHIR validator module, Matchbox, Gazelle FHIR Validator |
| CDA/C-CDA tooling | org.hl7.fhir.core (C-CDA via IG), MDHT (dormant), HL7 C-CDA Schematron |
| Terminology tooling | HAPI FHIR terminology module, Ontoserver (commercial), tx.fhir.org (hosted), HAPI JPA built-in terminology |
| Synthetic data | Synthea (Java), test data generators from cqframework, ETL-Synthea |
| Integration engines | Apache Camel (FHIR component), Mirth/NextGen Connect (Java), OpenHIM (Node) |
| FHIR servers | HAPI FHIR JPA Server, Blaze (Clojure), Medplum (TypeScript), LinuxForHealth (dormant) |
| Clinical decision support | CQL engine (Java/Kotlin), cqf-ruler (HAPI plugin), OpenCDS (unclear status), Drools |
| EHR platforms | OpenMRS (Java), Bahmni (Java/OpenMRS), OpenEMR (PHP), VistA (MUMPS), EHRbase (Java) |
| Storage / temporal | XTDB (Clojure/JVM), Datomic (proprietary/Clojure), HAPI JPA (SQL), fhirbase (PostgreSQL) |
| Clojure-native | Blaze, fhirpath.clj, clojure-hl7-messaging-2-parser, sql-on-fhir.clj, fhir.clj (abandoned) |
| JVM infrastructure | Integrant, Component, Ring, Reitit, Malli, clojure.spec, XTDB, Kafka, Jackson, Transit |
| Testing / conformance | Inferno, Touchstone, Crucible (dormant), Gazelle, validator_cli, NIST FHIR tools |

---

## Candidate Matrix

| Project | Category | Primary Language | Purpose | Current Release | Maintenance Status | License | Standards Support | Deployment | Clojure Interop | Likely Reusable Parts | Major Risks | Disposition |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| HAPI FHIR | Library / Server | Java | FHIR parsing, validation, server, client | 8.6.0 (Nov 2025) | Active | Apache 2.0 | R4, R4B, R5, DSTU3, STU3 | Library / Docker | Via Java interop | Parser, validator, client, JPA server, FHIRPath | Builder-heavy API; global FhirContext; large transitive deps | Adopt |
| HAPI HL7v2 | Library | Java | HL7 v2 parsing, MLLP, encoding | 2.5.1 (2024) | Stable and maintained | Dual MPL/GPL (licensee's choice) | v2.1–2.8 | Library | Via Java interop | Parser, message builder, MLLP transport | Conformance profile validation is limited | Adopt |
| org.hl7.fhir.core | Library / CLI | Java | FHIR validation, FHIRPath, IG, CDA | Latest 2025/2026 | Active | Apache 2.0 | FHIR R2–R6, CDA, C-CDA | Library / CLI jar | Via Java interop or subprocess | Validator, FHIRPath engine, IG tools | Network calls for IG/terminology by default; requires offline configuration | Adopt (subprocess preferred) |
| Blaze | FHIR Server | Clojure | FHIR R4 server + CQL engine | 1.10.1 (2026) | Active | Apache 2.0 | FHIR R4, CQL | Docker / uberjar | Native Clojure | Architecture, module design, Integrant patterns, FHIR storage | Production use requires RocksDB ops knowledge | Adopt / study |
| Medplum | FHIR Platform | TypeScript / Node | FHIR-native dev platform, auth, subscriptions | v5.1.22 (Jun 2026) | Active | Apache 2.0 | FHIR R4, SMART, US Core | SaaS / self-hosted | HTTP / REST | Full FHIR application architecture patterns | Not JVM; SaaS model; self-hosting requires ops | Study (architectural prior art) |
| Synthea | Generator | Java | Synthetic patient population | v3.x (2025) | Active | Apache 2.0 | FHIR R4, C-CDA, OMOP | CLI / library | Via Java or subprocess | Realistic FHIR R4 bundles, OMOP export, custom modules | Not suitable for exact test-case control without filtering | Adopt |
| HAPI FHIR JPA Server | Server | Java | FHIR JPA persistence + search | 8.6.0 | Active | Apache 2.0 | FHIR R4, R4B, R5 | WAR / Docker | Via HTTP or Java | Search implementation patterns, subscription handling | Requires relational DB; stateful; complex configuration | Adopt (as reference) |
| CQL Engine | Library | Java / Kotlin | CQL compiler + ELM runtime | 2025 (active) | Active | Apache 2.0 | CQL 1.5, FHIR R4 | Library | Via Java interop | CQL-to-ELM compiler, ELM evaluator | Requires FHIR model adapter; complex setup | Evaluate |
| cqf-ruler | FHIR Plugin | Java | CQL + Clinical Reasoning on HAPI | Dec 2025 | Stable and maintained | Apache 2.0 | FHIR R4, CQL, CDS Hooks | HAPI plugin | Via HAPI FHIR | CDS Hooks endpoint, $evaluate-measure | Tightly coupled to HAPI FHIR | Evaluate |
| dcm4che | Library | Java | DICOM toolkit | 5.34.3 (Apr 2026) | Active | LGPL 2.1 | DICOM, IHE actors | Library | Via Java interop | DICOM parsing, network services | LGPL; large; DICOM-specific | Adopt if DICOM needed |
| Inferno | Test Framework | Ruby | FHIR conformance testing | Active 2025/2026 | Active | Apache 2.0 | US Core, SMART, g(10) | Web app / CLI | Subprocess / HTTP | Test suites for US Core, SMART on FHIR | Not JVM; requires Ruby runtime | Adopt as external tool |
| Gazelle | Test Platform | Java | IHE interoperability test bed | 4.1.8 (2024) | Stable and maintained | Apache 2.0 | HL7, DICOM, FHIR, IHE | Web app | HTTP | IHE conformance testing patterns | Complex deployment; IHE-focused | Use as external tool |
| NextGen Connect (Mirth) | Integration Engine | Java | Healthcare integration engine | 4.x (2024) | Stable and maintained | MPL 2.0 | HL7 v2, FHIR, CDA, X12 | Server | Via HTTP | Integration patterns, HL7 routing | UI-driven; embedding is complex | Study / external process |
| Apache Camel FHIR | Integration Library | Java | EIP-based FHIR integration | 4.x (2025/2026) | Active | Apache 2.0 | FHIR R4 via HAPI FHIR | Library | Via Java interop or Clojure | FHIR endpoint routing, transformation | Adds Camel overhead to HAPI FHIR | Evaluate |
| OpenMRS | EHR Platform | Java | Open-source EMR for LMIC | 2.x (active) | Active | MPL 2.0 | OpenMRS data model, FHIR module | Web app | HTTP | Concept dictionary model, encounter schema | Monolithic platform | Study (domain model only) |
| EHRbase | openEHR Server | Java | openEHR clinical data repository | 2.x (2025) | Active | Apache 2.0 | openEHR REST API, AQL | Docker | HTTP | openEHR archetype patterns | Different paradigm from FHIR | Study (data model comparison) |
| XTDB | Temporal DB | Clojure / JVM | Bitemporal immutable database | 2.x (active 2025/2026) | Active | MIT | SQL, Datalog, Arrow, JDBC | Library / server | Native Clojure | Bitemporal storage, provenance, audit trail | Operational maturity; v2 API changes | Adopt (evaluate) |
| fhirpath.clj | Library | Clojure | FHIRPath evaluation | 2021/2022 | Low activity | MIT / unclear | FHIRPath R4 | Library | Native Clojure | FHIRPath in Clojure | Largely unmaintained; limited coverage | Study; wrap in abstraction |
| LinuxForHealth FHIR | Server / Library | Java | FHIR R4/R4B modular server | 5.1.1 (Dec 2022) | Dormant | Apache 2.0 | FHIR R4, R4B | Library / WAR | Via Java interop | fhir-model (immutable FHIR objects), fhir-search patterns | No releases since 2022; IBM divestiture | Architectural prior art only |
| fhirbase | Utility | Go / SQL | FHIR-on-PostgreSQL | 2018 stable | Low activity / dormant | MIT | FHIR STU3/R4 | CLI | Via PostgreSQL JDBC | FHIR storage in PostgreSQL patterns | Go-based CLI; limited ongoing development | Study (architectural reference) |
| clojure-hl7-messaging-2-parser | Library | Clojure | HL7 v2 parsing | Oct 2023 | Low activity but viable | MIT | HL7 v2.x | Library | Native Clojure | Clojure-idiomatic HL7 v2 map | No conformance profile support | Evaluate / thin wrapper |
| sql-on-fhir.clj | Library | Clojure | SQL-on-FHIR ViewDefinitions | 2023 | Low activity | MIT / unclear | SQL-on-FHIR spec | Library | Native Clojure | FHIR flattening for analytics | Limited coverage | Evaluate |
| OpenEMR | EHR Platform | PHP | Open-source EMR | v8.x (2025) | Active | GPL 2+ | FHIR R4, US Core | Web app | HTTP | FHIR API patterns, ONC certification experience | PHP; not JVM | Architectural study only |

---

## Java and JVM Healthcare Libraries

### HAPI FHIR

**Repository:** [https://github.com/hapifhir/hapi-fhir](https://github.com/hapifhir/hapi-fhir)
**Documentation:** [https://hapifhir.io/hapi-fhir/docs/](https://hapifhir.io/hapi-fhir/docs/)
**Current release:** 8.6.0 (August 2025). Snapshot 8.11.4-SNAPSHOT visible on [https://hapi.fhir.org/](https://hapi.fhir.org/) as of May 2026.
**License:** Apache 2.0
**FHIR versions:** DSTU2, STU3, R4, R4B, R5
**Java support:** Java 11+; Java 17 recommended; tested with Java 21
**Maintenance:** Active. Smile Digital Health (formerly Smile CDR) is the primary commercial sponsor. Community contributors are numerous.
**Distribution:** Maven Central (`ca.uhn.hapi.fhir`)
**CVE note:** CVE-2026-33180 (improper redirect credential leak in HTTP client) identified in 2026. Monitor security advisories.

HAPI FHIR is a family of modules:

| Module | Artifact ID | Purpose |
|---|---|---|
| Core | `hapi-fhir-base` | Common interfaces, FhirContext, parsers |
| Model (R4) | `hapi-fhir-structures-r4` | FHIR R4 Java model classes |
| Model (R5) | `hapi-fhir-structures-r5` | FHIR R5 Java model classes |
| Validation | `hapi-fhir-validation` | Base validator, narrative generator |
| Validation resources | `hapi-fhir-validation-resources-r4` | Bundled profiles for R4 |
| FHIRPath | Included in core | FHIRPath engine (FhirPathEngine) |
| JPA server | `hapi-fhir-jpaserver-base` | JPA persistence, search, subscriptions |
| Server | `hapi-fhir-server` | Plain FHIR REST server without JPA |
| Client | Included in `hapi-fhir-base` | RESTful FHIR client |
| Terminology | `hapi-fhir-terminology` | In-memory terminology service |

**Minimum dependency for FHIR parsing only:**
```xml
<dependency>
  <groupId>ca.uhn.hapi.fhir</groupId>
  <artifactId>hapi-fhir-base</artifactId>
  <version>8.6.0</version>
</dependency>
<dependency>
  <groupId>ca.uhn.hapi.fhir</groupId>
  <artifactId>hapi-fhir-structures-r4</artifactId>
  <version>8.6.0</version>
</dependency>
```

**For profile validation** add `hapi-fhir-validation` and `hapi-fhir-validation-resources-r4`.
**For terminology validation** add a terminology server or the bundled `hapi-fhir-terminology` module and load ValueSets.
**For FHIRPath** it is included in the core; no additional dependency.
**For server capabilities** add `hapi-fhir-jpaserver-base` and a JPA provider.

**Known limitations:**
- `FhirContext` is expensive to create; it should be a singleton. This is awkward in REPL-driven Clojure development.
- The JPA server uses static registries and background thread pools that require explicit shutdown.
- Builder-style Java API is verbose from Clojure.
- Default validator makes network calls to load IGs unless explicitly configured offline.
- US Core and other profile package loading requires correct package cache management; caching behavior can cause version-skew bugs (see `org.hl7.fhir.core` issue #564).

---

### HAPI HL7v2

**Repository:** [https://github.com/hapifhir/hapi-hl7v2](https://github.com/hapifhir/hapi-hl7v2)
**Documentation:** [https://hapifhir.github.io/hapi-hl7v2/](https://hapifhir.github.io/hapi-hl7v2/)
**Current release:** 2.5.1 (2024)
**License:** Dual-licensed MPL / GPL, at the licensee's choice (per the project `pom.xml`; version numbers not asserted — not stated in the source checked)
**HL7 versions:** v2.1 through v2.8, plus HL7 v3 legacy
**Maintenance:** Stable and maintained.
**Distribution:** Maven Central (`ca.uhn.hapi`)

The HAPI HL7v2 library provides:
- **Tolerant parser:** Parses non-conformant messages. Ignores unknown segments.
- **Strict parser:** Enforces message structure per the version model.
- **Message models:** Generated Java classes for each segment type per HL7 version.
- **MLLP transport:** `MinLowerLayerProtocol` for TCP-based HL7 message framing.
- **Custom Z-segments:** `CustomModelClassFactory` for organization-specific segments.
- **TestPanel:** A Swing GUI for HL7 message construction and testing (not for embedding).

**What HAPI HL7v2 validates:**
- Message structure against the HL7 version specification model.
- Required fields in mandatory segments.
- Data type format (dates, coded values in theory).

**What HAPI HL7v2 does not validate:**
- Conformance profiles beyond the base standard. For local implementation profile validation, a separate profile-based validator layer is needed (e.g., HL7 conformance profile XML files processed manually).
- Terminology binding.
- Semantic clinical correctness.

**Limits of base-standard parsers:** Base HL7 v2 parsers know the standard message structures. Organization-specific constraints (required optional fields, value set restrictions for Z-segments, custom cardinality) are not enforced by the parser alone. Runtime conformance profile checking requires custom validator logic.

---

### org.hl7.fhir.core and validator_cli

**Repository:** [https://github.com/hapifhir/org.hl7.fhir.core](https://github.com/hapifhir/org.hl7.fhir.core)
**Current release:** Active (2025/2026; see releases page).
**License:** Apache 2.0
**FHIR versions:** R2–R6, CDA, C-CDA

This library is the reference implementation of the FHIR tooling infrastructure used by HL7. It includes:
- The canonical **FHIR validator** (`validator_cli.jar`): Validates FHIR resources against the base spec, loaded IGs, and profiles.
- **FHIRPath engine** (`FHIRPathEngine`): The reference FHIRPath implementation in Java.
- **IG Publisher infrastructure**: Used by HL7 for building implementation guides.
- **CDA/C-CDA validation**: Via the HL7 CDA IG and Schematron-equivalent validation.
- **FHIR version conversion**: STU3 ↔ R4 ↔ R5 conversion utilities.
- **Snapshot generator**: `ProfileUtilities` for generating profile snapshots.

**Offline use:** The validator downloads IG packages from `packages.fhir.org` by default. To run reproducibly offline:
1. Pre-populate the package cache (`~/.fhir/packages/`).
2. Pass `-tx n/a` to disable terminology server calls.
3. Pass explicit `-ig` parameters to load pre-downloaded IG packages.

**Hidden network calls:** By default the validator and FHIRPathEngine will call `tx.fhir.org` for terminology expansion. This must be explicitly disabled for offline or reproducible builds.

**C-CDA validation:** The validator can validate CDA/C-CDA documents when passed the appropriate HL7 CDA implementation guide (`hl7.cda.ccda`). The HL7 C-CDA 2.1 Schematron is available at [https://github.com/HL7/CDA-ccda-2.1](https://github.com/HL7/CDA-ccda-2.1). Schematron validation requires an XSLT/Schematron processor such as Saxon; the `validator_cli.jar` handles this internally.

---

### CQL Evaluation Engine

**Repository:** [https://github.com/cqframework/clinical_quality_language](https://github.com/cqframework/clinical_quality_language)
**eCQI page:** [https://ecqi.healthit.gov/tool/cql-evaluation-engine-java](https://ecqi.healthit.gov/tool/cql-evaluation-engine-java)
**License:** Apache 2.0
**Language:** Java (migrating to Kotlin per activity in the repo)
**Maintenance:** Active (last commit June 2026 per GitHub topics page)

The CQL ecosystem consists of:
- **CQL-to-ELM translator** (`cql-to-elm`): Compiles CQL source to Expression Logical Model (ELM) XML or JSON.
- **ELM evaluation engine** (`engine.fhir`): Evaluates ELM against FHIR data.
- **CQL translation service** ([https://github.com/cqframework/cql-translation-service](https://github.com/cqframework/cql-translation-service)): REST microservice wrapping the translator (active, May 2026).
- **cqf-ruler** ([https://github.com/cqframework/cqf-ruler](https://github.com/cqframework/cqf-ruler)): HAPI FHIR server plugin adding `$evaluate-measure`, `$apply`, CDS Hooks endpoints, `PlanDefinition/$apply`. Last release December 2025.

**Standards:** CQL 1.5.3 (normative HL7 standard); ELM 1.5. Used in eCQMs for CMS reporting, HEDIS, Da Vinci, and clinical reasoning IGs.

**Risks:**
- CQL is expressive but requires clinical domain expertise to author correctly.
- The ELM runtime requires a FHIR data adapter; constructing one for a non-HAPI data source requires significant work.
- Generic business-rule engines (pure Drools) lack clinical context awareness, temporal reasoning, and FHIR model knowledge. Using Drools for clinical logic without domain-specific wrappers risks clinical logic errors that appear syntactically valid.

---

### dcm4che

**Repository:** [https://github.com/dcm4che/dcm4che](https://github.com/dcm4che/dcm4che)
**Distribution:** [https://sourceforge.net/projects/dcm4che/files/dcm4che3/](https://sourceforge.net/projects/dcm4che/files/dcm4che3/)
**Current release:** 5.34.3 (April 2026)
**License:** LGPL 2.1 (core); some components MPL
**Maintenance:** Active

dcm4che is the JVM standard library for DICOM. Its modules include:
- DICOM data set parsing and encoding.
- DICOM network services (C-STORE, C-FIND, C-GET, C-MOVE, WADO, STOW-RS).
- IHE actor implementations (XDS, PIXv3, PDQv3).
- JPEG compression, pixel data handling.
- HL7 v2 messaging for DICOM workflow integration.

**Clojure note:** LGPL means the library can be used as a dependency in a proprietary application. Clojure code calling it as a library does not trigger LGPL copyleft. Verify with counsel for specific deployment models.

---

### Apache Camel FHIR Component

**Documentation:** [https://camel.apache.org/components/4.18.x/fhir-component.html](https://camel.apache.org/components/4.18.x/fhir-component.html)
**License:** Apache 2.0
**FHIR versions:** R4 via HAPI FHIR
**Maintenance:** Active (part of Apache Camel main release, 4.18.x current 2025/2026)

Apache Camel's FHIR component wraps HAPI FHIR to provide:
- FHIR endpoint routing in Camel routes.
- FHIR resource marshaling/unmarshaling to/from JSON.
- Operations: create, read, update, delete, search, validate, transaction.
- Integration with Camel's broader enterprise integration pattern ecosystem (content-based routing, transformation, retry, error handling).

**Clojure note:** Camel can be invoked from Clojure via Java interop. The main value is when a Camel integration bus is already in use; for pure Clojure FHIR client use, direct HAPI FHIR interop is simpler.

---

### Drools and Clinical Decision Support Rule Engines

**Repository:** [https://github.com/kiegroup/drools](https://github.com/kiegroup/drools)
**License:** Apache 2.0
**Maintenance:** Active (part of Red Hat / KIE group)

Drools is a JVM RETE-based rule engine that has been used in clinical decision support. Examples include:
- **OpenMRS CDS Engine powered by Drools** ([https://openmrs.atlassian.net/wiki/spaces/projects/pages/603750673](https://openmrs.atlassian.net/wiki/spaces/projects/pages/603750673)): CDS session management on top of Drools.
- Pharmacy OneSource used Drools for patient surveillance.
- Published literature demonstrates Drools for telecardiology alerts, sedation guidelines, and medication rules.

**Risks of embedding generic rule engines in clinical logic:**
- Business-rule DSLs (DRL) lack clinical context encoding; authors must encode clinical semantics manually.
- Rule ordering and conflict resolution in clinical contexts is safety-critical.
- Drools sessions are stateful and must be carefully managed for concurrent requests.
- IEC 62304 / IEC 82304-1 requirements may complicate validation of dynamically loaded rule sets.
- CQL and CDS Hooks are more clinically idiomatic than raw Drools for FHIR-native workflows.

---

### Eclipse Open Healthcare Framework (OHF)

**Status:** Abandoned. The Eclipse OHF was an early healthcare interoperability framework (HL7 v2, CDA) that was active roughly 2005–2009. The wiki is still accessible at [https://wiki.eclipse.org/OHF](https://wiki.eclipse.org/OHF) but there has been no development activity for over a decade. Do not use.

---

### OpenCDS

**Website:** [https://www.opencds.org/](https://www.opencds.org/)
**Status:** Unclear. The website was updated January 2026 but there is no visible open-source release cadence, active GitHub repository with recent commits, or Maven Central artifact. OpenCDS was an HL7-aligned multi-institutional CDS infrastructure project. As of this writing its practical open-source status is unclear; treat as dormant until verified by direct maintainer contact.

---

## Clojure-Native and Clojure-Adjacent Prior Art

### Blaze (Primary Clojure Healthcare Prior Art)

**Repository:** [https://github.com/samply/blaze](https://github.com/samply/blaze)
**License:** Apache 2.0
**Current release:** v1.10.1 (2026, confirmed active)
**Language:** Clojure
**FHIR versions:** R4 (large subset)
**Maintenance:** Active. Maintained by [Samply / German Biobank Alliance](https://www.samply.de/). Used in the German Medical Informatics Initiative and European biobanks.
**Standards support:** FHIR R4, CQL (internal CQL engine), `$evaluate-measure`, `ValueSet/$expand`, `Patient/$everything`, asynchronous FHIR requests.
**Java requirements:** Java 17 or 21 (per DEVELOPMENT.md).

Blaze is the most significant native-Clojure prior art in the FHIR ecosystem. Architectural lessons:

- **Uses [Integrant](https://github.com/weavejester/integrant)** for component lifecycle management (`ig/init-key`, `ig/halt-key!`). This is the recommended Clojure system-composition approach for healthcare services with stateful FHIR databases.
- **Uses [clojure.spec.alpha](https://clojure.org/reference/spec)** for function specs (in `-spec` namespaces). Specs are excluded from the production uberjar to reduce footprint.
- **Uses pure functions** extensively; stateful components are explicitly identified and managed through Integrant.
- **Uses [anomalies](https://github.com/cognitect-labs/anomalies)** for error handling instead of exceptions.
- **Uses RocksDB** as its primary persistent store (FHIR resources stored as serialized FHIR bundles with content-addressable indexing).
- **Avoids reflection** (sets `*warn-on-reflection* true`).
- Emacs (CIDER), IntelliJ/Cursive, and VS Code/Calva are the developer IDEs of record per DEVELOPMENT.md.
- The CQL engine is implemented in Clojure, demonstrating that clinical reasoning in a functional style is feasible.
- Paging sessions use encrypted next-link URLs (confidentiality and integrity of query parameters).
- 91% FHIR R4 conformance score (mock.health 2026-Q2 scorecard).

**Reusable patterns:** Integrant-based component architecture for FHIR servers; module-level REPL design; pure-function-first database abstraction; anomaly-based error handling.

**Limitation:** Blaze is a complete server, not a library. The FHIR storage layer, CQL engine, and REST API are not independently factored as embeddable libraries. To reuse architectural patterns requires reading the source code rather than depending on Maven artifacts.

---

### fhirpath.clj

**Repository:** [https://github.com/HealthSamurai/fhirpath.clj](https://github.com/HealthSamurai/fhirpath.clj)
**Organization:** Health Samurai (builders of Aidbox)
**Language:** Clojure
**Purpose:** FHIRPath evaluation in Clojure. Design based on `fhirpath.js`.
**Maintenance:** Low activity. Last meaningful commits appear to be 2021–2022. Health Samurai's primary investment is in Aidbox (commercial) and their JavaScript/Go tooling.
**License:** Not clearly stated in visible README; assume MIT or Apache 2.0 (verify before use).

This is a thin implementation rather than a production-grade FHIRPath engine. Coverage of the full FHIRPath spec is incomplete. For production use, consider calling the Java FHIRPathEngine from `org.hl7.fhir.core` via interop. `fhirpath.clj` is useful as a model for building a Clojure wrapper around the Java engine.

---

### clojure-hl7-messaging-2-parser

**Repository:** [https://github.com/cmiles74/clojure-hl7-messaging-2-parser](https://github.com/cmiles74/clojure-hl7-messaging-2-parser)
**Clojars:** [https://clojars.org/com.nervestaple/hl7-parser](https://clojars.org/com.nervestaple/hl7-parser)
**License:** MIT
**Last release:** October 2023
**Maintenance:** Low activity but viable.
**Language:** Clojure

Provides Clojure-idiomatic HL7 v2 parsing:
- Returns parsed messages as Clojure maps (`:segments`, `:delimiters`, `:fields`).
- Functions for extracting field values, creating acknowledgment messages, emitting HL7 text.
- Does **not** validate conformance profiles.
- Does **not** handle MLLP transport.

This is a small, clean library suitable for basic HL7 v2 parsing in a Clojure application. For serious HL7 v2 work requiring conformance checking, MLLP, or message construction with versioned schemas, Java interop with HAPI HL7v2 is the more capable choice.

---

### sql-on-fhir.clj

**Repository:** [https://github.com/HealthSamurai/sql-on-fhir.clj](https://github.com/HealthSamurai/sql-on-fhir.clj)
**Language:** Clojure
**Purpose:** Implementation of the [SQL-on-FHIR ViewDefinition](https://build.fhir.org/ig/FHIR/sql-on-fhir-v2/) spec in Clojure.
**Maintenance:** Low activity (2023).
**License:** MIT / Apache 2.0 (verify).

SQL-on-FHIR ViewDefinitions define how to project FHIR resources into flat relational views using FHIRPath column expressions. This Clojure implementation allows computing such views in-process. Health Samurai also maintains a production implementation in Aidbox (PostgreSQL side).

---

### fhir.clj

**Repository:** [https://github.com/fhirbase/fhir.clj](https://github.com/fhirbase/fhir.clj)
**Language:** Clojure
**Purpose:** FHIR client in Clojure. Intended to parse/serialize FHIR, validate resources.
**Last meaningful commit:** 2018.
**Maintenance:** Abandoned. Do not use.

---

### clj-hl7-fhir (gered)

**Repository:** [https://github.com/gered/clj-hl7-fhir](https://github.com/gered/clj-hl7-fhir)
**Language:** Clojure
**Purpose:** FHIR DSTU2 client.
**Last commit:** 2014.
**Maintenance:** Abandoned. Do not use.

---

### Health Samurai Clojure Ecosystem

Health Samurai is a healthcare software company whose core product is [Aidbox](https://www.health-samurai.io/fhir-server), a commercial FHIR server built in Clojure. Their open-source contributions to the Clojure healthcare ecosystem include `fhirpath.clj`, `sql-on-fhir.clj`, and `fhir.clj`. In November 2025 they announced a Clojure online meetup series. Their Clojure implementations are instructive prior art even where maintenance is low.

**Aidbox licensing note:** Aidbox itself is a commercial product ($19,000/year for Aidbox Core). The development tier is free but explicitly not for PHI. Aidbox is not open source in any meaningful sense; its source is not publicly available. The open-source `fhirbase` (PostgreSQL FHIR CLI) and Clojure libraries are separately available under permissive licenses.

---

## Open-Source EHR and Clinical Platforms

### OpenMRS

**Website:** [https://openmrs.org/](https://openmrs.org/)
**Repository:** [https://github.com/openmrs/openmrs-core](https://github.com/openmrs/openmrs-core)
**License:** MPL 2.0
**Language:** Java (Spring, Hibernate, MySQL/PostgreSQL)
**Maintenance:** Active
**Architecture:** Layered: database → Java service layer → REST API → module system.

**Reusable prior art from OpenMRS:**
- **Concept Dictionary:** A general-purpose clinical concept model linking local codes to SNOMED, LOINC, and other standard terminologies. Instructive for designing a concept-to-standard-term mapping layer.
- **Encounter model:** Encounters containing ordered lists of Obs (observations). The design separates encounter metadata from individual clinical observations.
- **Patient identity and deduplication:** Built-in support for multiple identifiers and identity merging.
- **Module system:** OSGi-like runtime module loading. Prior art for extensible clinical service architectures.
- **FHIR module:** The `openmrs-module-fhir2` module maps OpenMRS data to FHIR R4 resources. Study for FHIR↔domain-model translation patterns.

Bahmni is a distribution of OpenMRS with an OpenELIS lab system, Odoo ERP, and a custom UI. It adds supply chain and pharmacy domain logic on top of OpenMRS.

---

### OpenEMR

**Website:** [https://www.open-emr.org/](https://www.open-emr.org/)
**License:** GPL 2+
**Language:** PHP
**Current release:** Version 8 (released 2025; ONC certified; upgrade deadline March 2026).
**Maintenance:** Active.

OpenEMR is not JVM-based, but its FHIR R4 API and ONC certification process are useful prior art for understanding US regulatory requirements. Its FHIR API implementation patterns are instructive for understanding what ONC-certified implementations are required to expose. GPL 2+ complicates embedding in proprietary systems.

---

### VistA / WorldVistA

**WorldVistA:** [https://worldvista.org/](https://worldvista.org/)
**Repository:** [https://github.com/WorldVistA/VistA](https://github.com/WorldVistA/VistA)
**License:** Apache 2.0 (WorldVistA), AGPL (OSEHRA VistA)
**Language:** MUMPS (M)
**Maintenance:** Low activity. No meaningful recent releases.

VistA is the VA's EHR system, historically one of the most influential EHR platforms. Its data model—particularly the longitudinal patient record model, file-based data dictionary, and temporal record structure—is instructive prior art. However, the MUMPS runtime is not JVM-based and there is no reusable JVM library from VistA.

**Lesson for JVM work:** VistA's FileMan global structure represents an early form of append-only, temporally versioned clinical records. This pattern maps conceptually to bitemporal databases like XTDB.

---

### EHRbase

**Repository:** [https://github.com/ehrbase/ehrbase](https://github.com/ehrbase/ehrbase)
**License:** Apache 2.0
**Language:** Java (Spring Boot, PostgreSQL)
**Current release:** 2.x (2025, active releases per GitHub)
**Maintenance:** Active. Backed by Vitasystems GmbH and others.

EHRbase is an [openEHR](https://openehr.org/) clinical data repository. OpenEHR is an alternative to FHIR for clinical data modeling:
- **Archetypes:** Formal clinical knowledge models authored in Archetype Definition Language (ADL). Governed by clinical communities, not implementers.
- **Templates:** Compositions of archetypes for specific use cases.
- **AQL:** Archetype Query Language, an SQL-like query language for openEHR data.

**Contrast with FHIR:**
- FHIR resources are wire-format-oriented and designed for exchange; openEHR archetypes are designed for long-term storage and clinical completeness.
- openEHR has stronger support for retrospective correction, versioning, and provenance within a single document framework.
- FHIR is more widely adopted for US and international regulatory compliance.
- Hybrid approaches (store in openEHR, expose via FHIR) are used in NHS England and Brazil RNDS.

**Reusable:** EHRbase's REST API and AQL engine are reusable as an external service. The archetype model provides prior art for clinically rich data structures.

---

### GNU Health

**Website:** [https://www.gnuhealth.org/](https://www.gnuhealth.org/)
**License:** GPL 3+
**Language:** Python (Tryton ERP framework)
**Maintenance:** Active.

GNU Health is not JVM-based. GPL 3+ complicates embedding. It is relevant as prior art for global health information systems in low-resource settings. Not recommended for JVM adoption.

---

### Medplum

**Website:** [https://www.medplum.com/](https://www.medplum.com/)
**Repository:** [https://github.com/medplum/medplum](https://github.com/medplum/medplum)
**License:** Apache 2.0
**Language:** TypeScript / Node.js
**Current release:** v5.1.22 (June 2026). HITRUST certified (June 2026).
**Maintenance:** Active. VC-backed, 25+ contributors, 100+ commits per month.

Medplum is a FHIR-native developer platform with an open-source core and a commercial hosted service. Its architecture is instructive:
- FHIR-native resource storage (all data is FHIR R4 resources).
- SMART on FHIR / OAuth2 / OIDC baked in.
- WebSocket subscriptions, bulk export, scheduling API.
- React component library for provider and patient UI.
- HITRUST-certified hosted platform.
- Open-source under Apache 2.0: FHIR server, Core APIs, SDKs.

**For JVM work:** Medplum is not JVM. However, its architecture demonstrates end-to-end FHIR application design including auth, audit, subscriptions, and clinical workflow. Use as a reference architecture. Medplum's 2026 developer conference (PlumCon) suggests continued investment.

---

### HAPI FHIR JPA Server

**Repository:** [https://github.com/hapifhir/hapi-fhir-jpaserver-starter](https://github.com/hapifhir/hapi-fhir-jpaserver-starter)
**Documentation:** [https://hapifhir.io/hapi-fhir/docs/server_jpa/get_started.html](https://hapifhir.io/hapi-fhir/docs/server_jpa/get_started.html)
**License:** Apache 2.0
**Current release:** Tracks HAPI FHIR releases (8.6.0)
**Maintenance:** Active

The JPA server provides:
- SQL-backed FHIR R4/R5 persistence.
- FHIR search implementation (including chaining, includes, modifiers).
- Subscription engine (websocket, rest-hook, email).
- Bulk export (`$export`).
- SMART on FHIR support (via Keycloak or other OIDC providers).
- Terminology service (ValueSet expansion, code validation).
- MDM (Master Data Management / patient linking).

**Architecture lessons:**
- HAPI JPA server uses a custom SQL schema: separate tables for resource storage, search parameter indexes, history, and terminology.
- The schema is generated by `hapi-fhir-jpaserver-base` and requires migration tooling for schema updates.
- Multi-tenancy via HAPI JPA server partitioning.
- The `IValidationModule` interface provides a clean extension point for external validators.

---

### LinuxForHealth FHIR Server

**Repository:** [https://github.com/LinuxForHealth/FHIR](https://github.com/LinuxForHealth/FHIR)
**Documentation:** [https://linuxforhealth.github.io/FHIR/](https://linuxforhealth.github.io/FHIR/)
**License:** Apache 2.0
**Last release:** 5.1.1 (December 2022)
**Maintenance:** Dormant. No releases since December 2022. This is the former IBM FHIR Server.

The LinuxForHealth FHIR Server provides reusable prior art as library modules (available on Maven Central under `org.linuxforhealth.fhir`):
- **`fhir-model`**: Immutable, visitable FHIR object model with high-performance JSON/XML parsers. Thread-safe. Architecturally clean.
- **`fhir-search`**: Search parameter resolution and indexing abstractions.
- **`fhir-validation`**: Profile validation framework.
- **`fhir-term`**: Terminology service abstraction layer.

**Disposition:** Dormant as a server. The `fhir-model` library is architecturally instructive as prior art for immutable FHIR object design. Do not adopt for new production work.

---

### Aidbox / fhirbase

**Aidbox:** [https://www.health-samurai.io/fhir-server](https://www.health-samurai.io/fhir-server)
**fhirbase:** [https://github.com/fhirbase/fhirbase](https://github.com/fhirbase/fhirbase)
**License:** Aidbox is commercial ($19,000/year); fhirbase is MIT.
**Language:** Clojure (Aidbox internal, not open source), Go (fhirbase CLI)

**Aidbox** is a commercial FHIR server built by Health Samurai. Its FHIR-on-PostgreSQL architecture (JSONB storage with FHIRPath-to-SQL translation) is instructive for JVM implementations. The Aidbox SQL-on-FHIR approach is the basis for the open-source `sql-on-fhir.clj` library.

**fhirbase** is an open-source CLI tool for importing FHIR resources into PostgreSQL and querying them. Version history shows active development in 2018 and limited activity since. The PostgreSQL schema design (JSONB resource storage with versioning tables) is useful prior art.

---

## Healthcare Integration Engines

### NextGen Connect (Mirth Connect)

**Repository:** [https://github.com/nextgenhealthcare/connect](https://github.com/nextgenhealthcare/connect)
**License:** MPL 2.0
**Language:** Java
**Current release:** 4.x (2024)
**Maintenance:** Stable and maintained. NextGen Healthcare is the commercial sponsor.

Mirth Connect is the most widely deployed open-source healthcare integration engine. Features:
- Visual channel designer (drag-and-drop message routing).
- JavaScript transformation scripting engine (Rhino/Nashorn).
- Connectors for HL7 v2 (MLLP), FHIR, DICOM, CDA, X12 EDI, database, HTTP, file.
- Message filtering, routing, error handling.

**Embedding note:** The Mirth Connect server is a standalone Java application. It is not designed to be embedded as a library in another JVM application. The primary reuse model is running Mirth Connect as an external integration process and connecting to it via its REST API or by calling the MLLP/HTTP endpoints it exposes.

**For Clojure:** Mirth Connect is most useful as an external integration process handling HL7 v2 normalization and routing, with a Clojure service consuming normalized data via REST or queue.

---

### Apache Camel

**Website:** [https://camel.apache.org/](https://camel.apache.org/)
**License:** Apache 2.0
**Current release:** 4.18.x (2025/2026)
**Maintenance:** Active (Apache top-level project)

Apache Camel provides enterprise integration patterns (EIP) as a Java library. It includes:
- FHIR component (backed by HAPI FHIR): [https://camel.apache.org/components/4.18.x/fhir-component.html](https://camel.apache.org/components/4.18.x/fhir-component.html)
- HL7 component (backed by HAPI HL7v2).
- 350+ connectors.

**Embedding options:** Camel can be embedded as a library in a JVM application (via `CamelContext`), run standalone (Camel CLI), or deployed in Spring Boot or Quarkus. The FHIR component uses HAPI FHIR's fluent client under the hood.

**Clojure note:** Camel routes can be constructed via Java interop from Clojure. The EIP DSL is builder-heavy but functional once assembled. A thin Clojure wrapper around route construction is feasible.

---

### OpenHIM

**Website:** [https://openhim.org/](https://openhim.org/)
**Repository:** [https://github.com/jembi/openhim-core-js](https://github.com/jembi/openhim-core-js)
**License:** MPL 2.0
**Language:** Node.js
**Maintenance:** Active (Jembi Health Systems). Version 8.x current.

OpenHIM is the open-source Health Information Mediator originally developed for the OpenHIE architecture. It provides:
- Middleware for routing, transforming, and auditing health messages.
- IHE ATNA audit logging.
- Orchestration of HL7 v2, FHIR, and custom messages.
- Role-based access control at the channel level.

**Embedding note:** OpenHIM is a Node.js server, not a JVM library. Integration model is via HTTP/HTTPS calls to the OpenHIM core. Useful as an external integration and audit layer.

---

## FHIR Tooling

### FHIR Versions and JVM Support Matrix

| Library | DSTU2 | STU3 | R4 | R4B | R5 | R6 |
|---|---|---|---|---|---|---|
| HAPI FHIR | ✓ | ✓ | ✓ | ✓ | ✓ | Planned |
| org.hl7.fhir.core / validator_cli | ✓ | ✓ | ✓ | ✓ | ✓ | In progress |
| LinuxForHealth fhir-model | — | — | ✓ | ✓ | — | — |
| Kotlin FHIR (Google) | — | — | ✓ | ✓ | ✓ | — |
| Blaze | — | — | ✓ | — | — | — |

### FHIR Parsing: Minimum Responsible Dependency

For a JVM application that **only** needs FHIR R4 parsing and serialization (no validation, no server):
```xml
<dependency>
  <groupId>ca.uhn.hapi.fhir</groupId>
  <artifactId>hapi-fhir-base</artifactId>
  <version>8.6.0</version>
</dependency>
<dependency>
  <groupId>ca.uhn.hapi.fhir</groupId>
  <artifactId>hapi-fhir-structures-r4</artifactId>
  <version>8.6.0</version>
</dependency>
```
This pulls in approximately 10–15 MB of transitive dependencies including Jackson, SLF4J, and the FHIR R4 model.

For **profile validation** add: `hapi-fhir-validation`, `hapi-fhir-validation-resources-r4`
For **US Core** validation: load the US Core IG package via `FilesystemPackageCacheManager`
For **terminology validation**: `hapi-fhir-terminology` or an external FHIR terminology server (tx.fhir.org, Ontoserver)
For **FHIRPath**: included in `hapi-fhir-base` (uses `FhirPathEngine`)
For **server capabilities** (search, subscriptions, JPA): `hapi-fhir-jpaserver-base` + JPA provider

### Offline and Reproducible Validation

The `org.hl7.fhir.core` validator is the most capable for profile validation. To run offline:
1. Pre-download IG packages: `java -jar validator_cli.jar -ig hl7.fhir.us.core#6.1.0 -tx n/a dummy.json`
2. Populate `~/.fhir/packages/` before validation.
3. Pass `-tx n/a` to all subsequent validation runs.
4. Pin IG versions explicitly in all invocations.
5. Store the package cache in a reproducible location for CI.

**Which validators make hidden network calls:** `org.hl7.fhir.core` (by default calls `tx.fhir.org`), HAPI FHIR validator (if configured with a remote terminology server), Inferno (calls FHIR server endpoints). All can be configured for offline use with care.

### FHIR Shorthand and SUSHI

**Repository:** [https://github.com/FHIR/sushi](https://github.com/FHIR/sushi)
**License:** Apache 2.0
**Maintenance:** Active (HL7-maintained)

FHIR Shorthand (FSH) is a domain-specific language for authoring FHIR implementation guide artifacts (profiles, extensions, value sets). SUSHI (SUSHI Unshortens SHorthand Inputs) compiles FSH to FHIR JSON/XML. It is a Node.js tool. For JVM teams, it is used to generate profile artifacts that are then consumed by the FHIR IG Publisher or loaded into validators. It is not embedded in the JVM application.

---

## HL7 v2 Tooling

### HAPI HL7v2 in Detail

**What it validates:**
- Message structure (segments and their order per the message type specification).
- Segment field cardinality.
- Basic datatype validation (TS timestamp format, NM numeric, etc.).

**What it does not validate:**
- Organization-specific profile constraints.
- Z-segment content semantics.
- Vocabulary bindings for CE/CWE fields.
- Cross-field consistency rules.

**Profile validation options:**
- HAPI HL7v2 includes a basic `ConformanceProfileValidator` that can load HL7 conformance profile XML files. This is limited; it validates structure against the profile but not semantic constraints.
- For serious conformance profile enforcement, a custom validator layer on top of the parsed HAPI message is the typical approach.

**Custom Z-segments:**
```java
// Register a custom model factory
HapiContext ctx = new DefaultHapiContext();
ctx.setModelClassFactory(new CustomModelClassFactory("com.example.hl7"));
Message msg = ctx.getGenericParser().parse(rawMessage);
```

**MLLP:**
```java
MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
Connection connection = ctx.newClient("hostname", 2575, false);
Initiator initiator = connection.getInitiator();
Message ack = initiator.sendAndReceive(msg);
```

**Clojure-native HL7 v2:** The `cmiles74/clojure-hl7-messaging-2-parser` provides map-based HL7 v2 parsing but lacks MLLP and conformance validation. For production HL7 v2 work in Clojure, HAPI HL7v2 via Java interop is the more capable choice.

---

## CDA and C-CDA Tooling

### Available Tooling

| Tool | Type | Language | Status | Purpose |
|---|---|---|---|---|
| [HL7 C-CDA Schematron](https://github.com/HL7/CDA-ccda-2.1) | Schematron rules | XSLT/Schematron | Active (C-CDA 2.1) | Template conformance validation |
| [org.hl7.fhir.core / validator_cli](https://github.com/hapifhir/org.hl7.fhir.core) | CLI / Library | Java | Active | C-CDA validation via IG |
| [NIST CDA Validation](https://cda-validation.nist.gov/cda-validation/) | Web tool | — | Unclear (last update unclear) | CDA/C-CDA online validation |
| MDHT (Model Driven Health Tools) | Library | Java / Eclipse | Abandoned | CDA/C-CDA Java parsing and generation |
| Matchbox | Library / Server | Java | Active (ahdis) | FHIR StructureMap + CDA transformation |

**Schematron:** The HL7 C-CDA 2.1 Schematron at [https://github.com/HL7/CDA-ccda-2.1](https://github.com/HL7/CDA-ccda-2.1) contains the normative validation rules for C-CDA. To apply them in JVM code, use a Saxon-based Schematron processor. The `validator_cli.jar` applies C-CDA validation internally when loaded with the `hl7.cda.ccda` package.

**MDHT:** The Model-Driven Health Tools Eclipse project generated Java APIs for CDA template types. It is no longer actively maintained and should not be adopted for new work. The `validator_cli.jar` approach is the current recommended path.

**C-CDA to FHIR conversion:** The HAPI FHIR JPA server Smile CDR release (8.4.0 "Amplification", August 2025) introduced a `$sdh.cda-to-fhir` operation for CDA-to-FHIR conversion. This is in Smile CDR (commercial) but the underlying mapping logic in HAPI FHIR is accessible.

**Risks:** C-CDA version fragmentation (C-CDA 2.0, 2.1, C-CDA R3) means template version must be tracked per document. The HL7 IG for C-CDA 3.0 introduced FHIR StructureDefinitions for CDA template modeling.

---

## Terminology Tooling

### Code Systems and Licensing

| Code System | Distribution | License / Restrictions | Embedded? |
|---|---|---|---|
| LOINC | [loinc.org](https://loinc.org/) | Free use, attribution required; redistribution with attribution; no commercial redistribution without license | Subset can be embedded |
| SNOMED CT | [snomed.org](https://www.snomed.org/) | SNOMED International member country license required; NRC licensing for US | Limited embedding; must verify license |
| RxNorm | [nlm.nih.gov](https://www.nlm.nih.gov/research/umls/rxnorm/index.html) | US federal government work; no copyright restrictions within US | Can be embedded |
| ICD-10 | CMS / WHO | US ICD-10-CM is public domain; ICD-10 WHO version has license | US CM can be embedded |
| UCUM | [unitsofmeasure.org](https://unitsofmeasure.org/) | Open (similar to BSD) | Can be embedded |
| CPT | AMA | Commercial license required | Cannot embed without license |

**LOINC licensing:** LOINC is available free of charge for use and redistribution with attribution. Redistribution in a commercial product requires compliance with the LOINC license terms at [https://loinc.org/kb/license/](https://loinc.org/kb/license/). Verify distribution rights before embedding LOINC content in a deployed system.

**SNOMED CT:** US-based deployment requires a National Release Center (NRC) license through SNOMED International's member country framework. Contact [https://www.snomed.org/](https://www.snomed.org/) for licensing. SNOMED CT data cannot be redistributed without an appropriate license.

### Open-Source Terminology Servers

| Server | Language | License | Standards Support | Status |
|---|---|---|---|---|
| [HAPI FHIR Terminology Module](https://hapifhir.io/hapi-fhir/docs/server_jpa/terminology.html) | Java | Apache 2.0 | FHIR R4/R5 ValueSet/$expand, $validate-code | Active |
| [Ontoserver](https://ontoserver.csiro.au/) | Java (CSIRO) | Commercial (research/free tiers) | FHIR terminology services, SNOMED CT, LOINC | Commercial |
| [tx.fhir.org](https://tx.fhir.org/) | Hosted service | Free use (HL7 hosted) | FHIR R4/R5 terminology | Active (network dependency) |
| Blaze | Clojure | Apache 2.0 | `$validate-code`, `$expand`, LOINC, SNOMED | Active |

**Making terminology validation reproducible:** Pin IG package versions + code system versions. Use an offline-capable FHIR terminology server (HAPI FHIR JPA with preloaded code systems). Store terminology package snapshots in artifact management (Nexus, Artifactory). Document the SNOMED CT release version in reproducibility metadata.

---

## Clinical Decision Support

### CDS Hooks

**Specification:** [https://cds-hooks.org/](https://cds-hooks.org/)
**HL7 repository:** [https://github.com/HL7/cds-hooks](https://github.com/HL7/cds-hooks)
**License:** Creative Commons

CDS Hooks is a vendor-agnostic specification for invoking decision support from within a clinician's workflow. The pattern:
1. EHR sends a JSON hook request (e.g., `patient-view`, `order-select`) to a CDS service.
2. CDS service returns cards (suggestions, alerts, links).
3. EHR presents cards to the clinician.

**Java implementations:**
- `cqf-ruler` (Java) implements the CDS Hooks endpoint on HAPI FHIR.
- `HealthLX/cds-hooks` (Java, 2019): Models CDS Hooks v1 data structures in Java. Low activity.
- AHRQ CDS Connect provides reference CDS Hooks implementations in JavaScript.

**Clojure:** There is no maintained Clojure CDS Hooks server implementation. A Clojure implementation would be straightforward: parse the JSON hook request (Transit/Cheshire), evaluate clinical logic (CQL via Java interop or custom rules), return cards as JSON. The CDS Hooks request/response is pure JSON with a well-defined schema.

---

## Clinical Data Models and Storage Patterns

### Comparison of Clinical Data Models

| Model | Best For | Not Suitable For | Governance |
|---|---|---|---|
| FHIR Resources (R4) | Wire format, exchange, REST API, regulatory compliance | Long-term archival semantics, complex temporal queries | HL7 International |
| openEHR Archetypes | Long-term clinical storage, clinical completeness, retrospective correction | Rapid development, US regulatory compliance out of the box | openEHR International |
| OMOP CDM | Analytics, observational research, federated studies | Primary clinical care workflows, FHIR exchange | OHDSI |
| HL7 RIM (v3) | Legacy standards, CDA document model | New development | HL7 International (legacy) |
| Custom immutable domain maps | Internal processing, test fixtures, transformation staging | Exchange, regulatory reporting | Internal |
| Event-sourced clinical records | Audit trail, provenance, temporal query, amendment history | Direct FHIR exchange | Internal / design pattern |
| Bitemporal (XTDB) | Longitudinal records with both clinical time and system time | Simple CRUD applications | Internal / XTDB |

**Wire formats** should be FHIR R4 (or R5 for newer implementations). FHIR R4 is the current US regulatory baseline (21st Century Cures Act, ONC).

**Storage models** do not need to be FHIR resources. A bitemporal store keyed on patient/resource-type/id with FHIR JSON blobs and temporal indexing is a valid pattern. XTDB provides this infrastructure.

**Analytics models** are best served by OMOP CDM (for research) or SQL-on-FHIR ViewDefinitions (for operational FHIR analytics).

**What should not be represented as generic untyped maps:**
- Clinical logic predicates (allergy severity, medication route, vital sign units) where type discipline prevents clinical logic errors.
- Identity (patient ID types, MRN vs. national ID) — untyped maps lose the meaning of the identifier system.
- Temporal events (onset vs. recorded vs. effective date) — Clojure keyword maps without specs/schemas cause temporal confusion.
- FHIR Reference values — loose strings vs. typed references cause provenance loss.

Use Malli schemas or clojure.spec to enforce type discipline on domain maps at service boundaries.

---

### Bitemporal Modeling for Clinical Records

A clinically and legally defensible record system must distinguish:

1. **Clinical truth (valid time):** When the clinical fact was true in the patient's life (e.g., diagnosis onset date).
2. **System ingestion time (transaction time):** When the fact was recorded in the system.
3. **Corrected/amended records:** When a clinician corrects a previously recorded fact. The original record must be preserved.
4. **Source-system history:** When the fact was recorded in the originating system (pre-FHIR legacy).
5. **Audit history:** Who read, wrote, amended, or accessed a record and when.

XTDB natively maintains transaction time (system time) and valid time (application time) for every row. This makes it well-suited for clinical records where:
- You need to answer "what did the system know about this patient at time T?"
- You need to answer "what was clinically true about this patient at time T?"
- Amendment history must be preserved without deletion.
- Provenance of corrections must be auditable.

**Datomic** also provides immutable, append-only storage with as-of queries, but its valid-time support is not built-in (transaction time only in Datomic). Datomic is commercial (Nubank/Cognitect license). Its immutable log model is instructive for clinical record design.

**PostgreSQL with custom temporal tables** can implement bitemporal patterns using period columns and history tables, but requires more application-level engineering than XTDB.

---

## Synthetic Data and Test Tooling

### Synthea

**Repository:** [https://github.com/synthetichealth/synthea](https://github.com/synthetichealth/synthea)
**License:** Apache 2.0
**Language:** Java
**Current release:** Active (2025 releases per GitHub)
**Maintenance:** Active. Maintained by MITRE with ONC and NIH support.

Synthea generates synthetic but realistic patient populations using a state-machine-based disease and lifecycle model.

**What Synthea generates reliably:**
- FHIR R4 Bundles (Patient, Encounter, Condition, Observation, Procedure, MedicationRequest, Immunization, DiagnosticReport, AllergyIntolerance, CarePlan).
- C-CDA documents.
- OMOP CDM CSV files.
- CSV files with patient demographics.
- Realistic vital signs, lab results, and medication histories.

**What Synthea controls directly:**
- Population demographics (age, gender, race, ethnicity, geographic distribution).
- Disease prevalence via module configuration.
- Date/time reproducibility via a fixed random seed.
- Export format selection.

**What requires seed search, filtering, or post-processing:**
- Exact patient profiles (specific lab values at specific dates).
- Edge cases in data quality (missing mandatory fields, invalid codes).
- Multi-system interaction scenarios (patient seen at two hospitals).
- Exact conformance to implementation guide profiles beyond US Core.

**Defects to introduce after generation:** Invalid SNOMED codes, missing required extensions, date range violations, and profile-specific constraint failures should be introduced in post-processing mutation steps rather than modeled in Synthea. This ensures the base synthetic data is valid and the test data set has controlled, documented defects.

**Reproducibility:** Synthea supports a `-s` seed parameter. The seed value, Synthea version, module set version, and population configuration must all be recorded as reproducibility metadata.

### Other Synthetic Data and Test Tools

| Tool | Language | Purpose | Status |
|---|---|---|---|
| [Inferno](https://github.com/inferno-framework) | Ruby | FHIR conformance testing | Active |
| [FHIR IG Publisher](https://github.com/HL7/fhir-ig-publisher) | Java | Build FHIR IGs | Active |
| [Gazelle FHIR Validator](https://gazelle.ihe.net/) | Java | IHE FHIR validation | Active |
| [Matchbox](https://github.com/ahdis/matchbox) | Java | FHIR validator + StructureMap | Active |
| [NIST FHIR Validator](https://github.com/usnistgov/asbestos) | Java | IHE MHD testing | Low activity |
| [Crucible](https://github.com/fhir-crucible) | Ruby | FHIR conformance | Maintenance-only / low activity |
| [Touchstone](https://touchstone.aegis.net/) | Proprietary | FHIR TestScript engine | Proprietary service; some free tiers |
| [cqframework FHIR patient generator](https://github.com/projecttacoma/fhir-patient-generator) | JavaScript | eCQM test data | Low activity |

**Healthcare fuzzing / mutation testing:** No dedicated JVM-specific healthcare fuzzing framework exists as of this writing. Property-based testing with [test.check](https://github.com/clojure/test.check) can generate mutated FHIR resources from Malli schemas. Post-processing Synthea output with a Clojure mutation step (randomly replacing SNOMED codes, truncating date fields, injecting null values) is a practical approach.

---

## Security, Privacy, Consent, and Audit

### SMART on FHIR

**Specification:** [https://smarthealthit.org/](https://smarthealthit.org/)
**License:** Creative Commons

SMART on FHIR defines:
- **App Launch Framework:** How browser-based and native apps authorize against a FHIR server using OAuth 2.0 authorization code flow.
- **Backend Services:** Server-to-server authorization using JWT and JWKS.
- **Scopes:** `patient/*.read`, `user/*.write`, etc. mapped to FHIR resource types and operations.

**Java implementations:**
- HAPI FHIR JPA server supports SMART on FHIR authorization via interceptors.
- Keycloak (Java, Apache 2.0) as the OAuth2/OIDC provider with SMART scope mapping.
- Example: [https://rob-ferguson.me/add-authz-to-hapi-fhir-with-apisix-and-keycloak/](https://rob-ferguson.me/add-authz-to-hapi-fhir-with-apisix-and-keycloak/)
- Smile CDR implements full SMART on FHIR with fine-grained access control (commercial).

**For Clojure:** SMART on FHIR is an OAuth2 flow; Clojure HTTP servers (Ring, Pedestal, Reitit) can integrate any compliant OAuth2/OIDC library. The application-level policy (which resources a token can access) requires custom implementation for fine-grained FHIR compartment access.

### IHE ATNA Audit Logging

IHE ATNA (Audit Trail and Node Authentication) defines audit message format and secure transport for healthcare audit logs. The Gazelle test bed and OpenHIM implement ATNA. For JVM applications, audit events should be emitted as FHIR `AuditEvent` resources (which map to ATNA events).

### Break-Glass Access and Data Segmentation

Break-glass access (emergency override of access controls) and data segmentation for privacy (DS4P) are policy-level mechanisms that require application-level implementation. No JVM library provides these out of the box. The FHIR consent model (`Consent` resource) provides the data representation; enforcement is application logic. Aidbox's access policy DSL provides an example of fine-grained FHIR access control.

### De-identification and Pseudonymization

| Tool | Language | License | Purpose |
|---|---|---|---|
| [ARX Data Anonymization Tool](https://arx.deidentifier.org/) | Java | Apache 2.0 | Statistical de-identification |
| [HAPI FHIR De-identification](https://smilecdr.com/) | Java | Commercial (Smile CDR) | FHIR resource de-identification |
| Synthea (output) | Java | Apache 2.0 | Synthetic (not de-identified) data |

---

## Clojure Integration Patterns

### When to Use Each Integration Approach

| Approach | When to Use | Risks |
|---|---|---|
| Direct Java interop | HAPI FHIR parsing, HL7 v2 parsing, terminology lookups | Builder APIs, mutable state, global registries |
| Thin Clojure wrapper | Wrapping HAPI `FhirContext` lifecycle, wrapping HL7 parser output as maps | Wrapper rot when Java API changes |
| Subprocess CLI | `validator_cli.jar` validation, SUSHI, Synthea generation | Subprocess latency, classpath isolation required |
| Embedded server | HAPI FHIR JPA server for full FHIR persistence | Complex lifecycle, thread pools, shutdown hooks |
| HTTP integration | Inferno, OpenHIM, external terminology server | Network latency, availability dependency |
| Separate JVM service | Blaze (via HTTP), CQL translation service, terminology server | Operational complexity |

### Java APIs Awkward from the Clojure REPL

| API | Problem | Mitigation |
|---|---|---|
| `FhirContext.forR4()` | Expensive; creates global caches and static registries | Create once; store in a var or Integrant component |
| HAPI JPA `DaoConfig` | Mutable builder with many setters | Initialize in an `ig/init-key` method; never mutate after init |
| HAPI JPA thread pools | Background reindexing threads, subscription workers | Must be explicitly stopped; use `ig/halt-key!` |
| HAPI validation package cache | Writes to `~/.fhir/packages/` | Pre-populate at build time; set `FHIR_PACKAGE_CACHE_PATH` |
| HAPI `IParser` | Stateful (pretty-print, narrative settings) | Parser is cheap to create; create per-use or per-thread |
| Drools `KieSession` | Stateful; not thread-safe | Use stateless sessions or one session per request |
| `MinLowerLayerProtocol` / HAPI MLLP | TCP connections with reconnect logic | Wrap in a Clojure component with explicit lifecycle |

### Illustrative Clojure Snippets

**Note:** These snippets are illustrative of patterns and are not guaranteed copy-paste production code. Verify API compatibility with the HAPI FHIR version in use.

#### Parsing a FHIR Resource via Java Interop

```clojure
(ns example.fhir
  (:import [ca.uhn.fhir.context FhirContext]
           [org.hl7.fhir.r4.model Patient]))

;; Create once at system startup; expensive to construct
(def fhir-context (FhirContext/forR4))

(defn parse-patient [json-string]
  ;; Returns a HAPI Patient Java object
  (let [parser (.newJsonParser fhir-context)]
    (.parseResource parser Patient json-string)))

(defn patient->map [^Patient patient]
  {:id (.getIdPart (.getIdElement patient))
   :family (-> patient .getName first .getFamily)
   :given  (-> patient .getName first .getGivenAsSingleString)
   :gender (-> patient .getGender .toCode)})
```

#### Parsing an HL7 v2 Message via Java Interop

```clojure
(ns example.hl7
  (:import [ca.uhn.hl7v2 DefaultHapiContext]
           [ca.uhn.hl7v2.model.v24.message ADT_A01]))

(def hl7-context (DefaultHapiContext.))

(defn parse-hl7v2 [raw-message]
  (.parse (.getGenericParser hl7-context) raw-message))

(defn extract-patient-id [message]
  ;; Illustrative; real code must navigate message structure
  (-> message .getPID (.getPatientIdentifierList 0) .getID .getValue))
```

#### Using clojure-hl7-messaging-2-parser (Clojure-native)

```clojure
(ns example.hl7-clj
  (:require [com.nervestaple.hl7-parser.parser :as parser]
            [com.nervestaple.hl7-parser.message :as message]))

(defn parse-and-extract [raw-message]
  (let [parsed (parser/parse raw-message)]
    {:message-id  (message/get-field-first-value parsed "MSH" 10)
     :patient-id  (message/get-field-first-value parsed "PID" 3)
     :patient-name (message/get-field-first-value parsed "PID" 5)}))
```

#### Validating a Canonical Map with Malli

```clojure
(ns example.validation
  (:require [malli.core :as m]
            [malli.error :as me]))

;; Define a schema for a clinical observation
(def ObservationSchema
  [:map
   [:id :string]
   [:subject [:map [:reference :string]]]
   [:status [:enum "preliminary" "final" "corrected" "cancelled"]]
   [:code [:map
           [:coding [:vector [:map
                               [:system :string]
                               [:code :string]
                               [:display {:optional true} :string]]]]]]
   [:valueQuantity {:optional true}
    [:map
     [:value :double]
     [:unit :string]
     [:system :string]
     [:code :string]]]])

(defn validate-observation [obs]
  (if (m/validate ObservationSchema obs)
    {:valid? true}
    {:valid? false
     :errors (me/humanize (m/explain ObservationSchema obs))}))
```

#### Calling a FHIR Terminology Service via HTTP

```clojure
(ns example.terminology
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

;; Illustrative call to a FHIR terminology server
(defn validate-code
  "Returns true if the code is valid in the given system."
  [tx-server-base system code]
  (let [url (str tx-server-base "/CodeSystem/$validate-code")
        params {:parameters
                {:resourceType "Parameters"
                 :parameter [{:name "system" :valueUri system}
                             {:name "code" :valueCode code}]}}
        response (http/post url
                   {:body (json/generate-string params)
                    :content-type :json
                    :accept :json})]
    (-> response :body (json/parse-string true)
        :parameter
        (->> (filter #(= "result" (:name %))))
        first
        :valueBoolean)))
```

#### Launching the FHIR Validator CLI as a Subprocess

```clojure
(ns example.validator
  (:require [clojure.java.shell :refer [sh]]))

;; Illustrative; adjust paths for your environment
(defn validate-resource
  "Runs the HL7 FHIR validator CLI on a resource file.
   Returns {:exit int :out string :err string}"
  [resource-file ig-package tx-url]
  (sh "java" "-jar" "/tools/validator_cli.jar"
      resource-file
      "-ig" ig-package
      "-tx" (or tx-url "n/a")
      "-version" "4.0.1"))
```

#### Querying a Clinical Table via JDBC

```clojure
(ns example.jdbc
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(def db {:dbtype "postgresql" :host "localhost" :dbname "fhirdb"
         :user "fhir" :password "changeme"}) ;; placeholder, not a credential

(defn get-patient-conditions [patient-id]
  (jdbc/execute!
    (jdbc/get-datasource db)
    ["SELECT resource->>$.code.coding[0].code AS code,
             resource->>$.clinicalStatus.coding[0].code AS status
      FROM hfj_resource
      WHERE res_type = 'Condition'
        AND resource->>$.subject.reference LIKE ?
      ORDER BY res_updated DESC"
     (str "%/Patient/" patient-id "%")]))
```

---

## Architectural Lessons from Prior Art

### Recurring Successful Patterns

1. **FHIR as wire format, not storage format.** Successful systems (Blaze, HAPI JPA, Aidbox) use FHIR for API exposure and ingestion but maintain internal representations optimized for storage and query.

2. **Component lifecycle management.** Blaze's Integrant-based architecture, OpenMRS's module system, and HAPI's JPA server all demonstrate explicit component lifecycle (init, halt) as essential for systems with thread pools, connection pools, and caches.

3. **Separate validation from parsing.** Parser success does not imply conformance. Every successful system separates syntax parsing from profile validation from terminology validation.

4. **External terminology service.** Production systems (Aidbox, HAPI JPA, Blaze) all integrate with a FHIR terminology server rather than embedding terminology logic in the application.

5. **Immutable append-only record logs.** VistA (historically), Datomic-based clinical systems, and XTDB-based designs demonstrate that append-only records with temporal queries are both feasible and clinically important.

6. **Modular library design.** LinuxForHealth FHIR Server's `fhir-model` / `fhir-search` / `fhir-validation` separation and HAPI FHIR's module decomposition allow partial adoption. Tightly coupled EHR platforms (OpenMRS, OpenEMR) are harder to partially reuse.

7. **Reproducible test data via seed-controlled generation.** Synthea's `-s` seed support and deterministic module execution are the model for reproducible healthcare test data.

### Recurring Unsuccessful Patterns

1. **Building a generic rule engine before a clinical model.** Drools in healthcare has delivered value when wrapped in a clinical domain layer but has caused maintenance problems when clinical knowledge is encoded directly in DRL without clinical governance.

2. **Treating FHIR conformance as clinical correctness.** A FHIR-valid resource can represent clinically nonsensical data. Profile validation is not a substitute for clinical review of data quality rules.

3. **Monolithic EHR platform adoption for component use.** OpenMRS, OpenEMR, and VistA provide useful domain models but adopting the full platform to reuse one module is almost always the wrong choice.

4. **Ignoring temporal complexity.** Systems that model only "current" clinical state encounter insurmountable problems when amendment history, effective dates, and ingestion times diverge. Retrofitting bitemporal semantics onto a current-state-only store is extremely expensive.

5. **Premature migration away from FHIR JSON.** FHIR JSON is verbose but self-describing. Systems that convert FHIR to custom internal schemas early in the pipeline lose provenance and create mapping maintenance burden.

---

## Dependency and Adoption Risks

| Risk | Examples | Mitigation |
|---|---|---|
| Project abandonment | LinuxForHealth FHIR Server, clj-hl7-fhir, OpenCDS | Evaluate release cadence; prefer active projects; abstract behind thin wrappers |
| CVE burden | HAPI FHIR CVE-2026-33180 | Monitor GitHub security advisories; subscribe to HAPI releases; pin versions |
| Transitive dependency weight | HAPI FHIR pulls Jackson, Spring, Hibernate | Use module-level dependencies; avoid pulling in JPA if only parsing is needed |
| Standards-version skew | FHIR R4 vs R5 profile packages | Pin IG versions; version-lock package caches in CI |
| Terminology licensing | SNOMED CT, LOINC, CPT redistribution | Verify redistribution rights before deploying; do not bundle SNOMED CT without NRC license |
| Hidden network dependencies | `org.hl7.fhir.core` calls tx.fhir.org by default | Configure offline mode; test with `-tx n/a` |
| Stateful Java APIs in REPL | `FhirContext`, Drools sessions, MLLP connections | Wrap in Integrant components; never mutate after init |
| Validator package drift | IG packages loaded from package cache at runtime | Pre-populate cache; pin versions; include cache in CI artifact |
| Open-core restrictions | Aidbox (commercial core, open accessories), Smile CDR | Clearly distinguish open-source from commercial features; budget for commercial licensing if needed |
| Cloud-service dependence | Aidbox hosted service, Medplum hosted service | Verify self-hosting paths; test with self-hosted deployment |
| Operational complexity | HAPI FHIR JPA server (requires DB, migrations, thread management) | Start with plain FHIR server before JPA; evaluate XTDB or fhirbase as simpler persistence |

---

## General Clojure/JVM Infrastructure for EHR Mechanisms

This section explains where general-purpose Clojure/JVM libraries fit into healthcare infrastructure. These are not healthcare libraries; they provide underlying mechanisms.

| Library | Healthcare Role |
|---|---|
| [Malli](https://github.com/metosin/malli) | Schema validation at service boundaries; validate FHIR-derived domain maps; generate test data |
| [clojure.spec](https://clojure.org/reference/spec) | Function specs for clinical service functions (used in Blaze); validate FHIR map shapes |
| [test.check](https://github.com/clojure/test.check) | Property-based testing of FHIR resource generators, terminology validators, clinical logic |
| [XTDB](https://xtdb.com/) | Bitemporal clinical records; longitudinal patient history; provenance; amendment log; valid-time queries |
| [Datomic](https://www.datomic.com/) | Immutable clinical records with as-of queries (transaction time only); useful but commercial |
| [Datahike](https://github.com/replikativ/datahike) | Open-source Datomic-like DB with pluggable storage; less production-proven than XTDB |
| [Apache Kafka](https://kafka.apache.org/) | Clinical event log (HL7 v2 messages, FHIR resource change events); event sourcing; audit trail backbone |
| [core.async](https://github.com/clojure/core.async) | Async HL7 v2 MLLP message handling; FHIR subscription event routing |
| [Integrant](https://github.com/weavejester/integrant) | System lifecycle for FHIR servers, HL7 listeners, terminology clients (used by Blaze) |
| [Component](https://github.com/stuartsierra/component) | Alternative to Integrant for stateful component lifecycle |
| [Mount](https://github.com/tolitius/mount) | State management; simpler than Integrant but less explicit |
| [Pedestal](https://github.com/pedestal/pedestal) | HTTP server for FHIR REST APIs; async handling for long-running FHIR operations |
| [Ring](https://github.com/ring-clojure/ring) | HTTP server abstraction; foundational for FHIR REST APIs |
| [Reitit](https://github.com/metosin/reitit) | Data-driven router with schema coercion; clean for FHIR REST route definitions |
| [Lacinia](https://github.com/walmartlabs/lacinia) | GraphQL server; relevant for FHIR GraphQL endpoint (FHIR R4 includes a GraphQL capability) |
| Jackson (Java) | JSON parsing for FHIR JSON (used internally by HAPI FHIR); also available via Cheshire in Clojure |
| Transit | Clojure-optimized JSON/MessagePack; useful for internal FHIR resource caching, not for FHIR wire format |
| Avro / Protobuf | Schema-driven binary encoding for clinical event streams (Kafka); not suitable as FHIR wire format |
| [DuckDB JDBC](https://duckdb.org/) | In-process OLAP queries over FHIR NDJSON or Parquet; SQL-on-FHIR analytics |
| [Apache Arrow](https://arrow.apache.org/) | Columnar in-memory format for FHIR analytics; bridges to DuckDB, Spark, and Python ML |
| [Apache Parquet](https://parquet.apache.org/) | Columnar storage for FHIR resource archives and analytical pipelines |
| [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) | Clojure DataFrame library on Arrow; FHIR analytics after SQL-on-FHIR projection |
| [Tablecloth](https://github.com/scicloj/tablecloth) | Clojure DataFrame API wrapping tech.ml.dataset; clinical data quality analysis |
| Apache Spark Java API | Large-scale FHIR bulk export processing; OMOP ETL pipelines; cohort extraction |

---

## Recommended Technology Shortlist

### Adopt or Prototype Soon
- **HAPI FHIR** (parsing, validation, FHIRPath, client) — unavoidable for JVM FHIR work.
- **HAPI HL7v2** — the standard JVM HL7 v2 library.
- **Blaze** — study architecture; run as external FHIR server in development and testing.
- **Synthea** — synthetic patient generation for all testing.
- **Integrant** — Blaze's proven choice for Clojure system lifecycle.
- **Malli** — schema validation at clinical service boundaries.
- **XTDB** — evaluate for bitemporal clinical record storage.

### Evaluate with a Bounded Spike
- **org.hl7.fhir.core / validator_cli** — profile validation spike: validate a Synthea bundle against US Core.
- **cqf-ruler / CQL engine** — CQL evaluation spike: evaluate a simple quality measure against Synthea data.
- **Apache Camel FHIR component** — integration spike: route Synthea FHIR bundles through a Camel route.
- **XTDB** — temporal record spike: model a patient history with corrections and bitemporal queries.
- **fhirpath.clj** — evaluate coverage against FHIRPath test suite; assess whether Java interop is preferable.

### Retain as an External Process
- **validator_cli.jar** — invoke as subprocess for validation in CI pipelines.
- **Inferno** — FHIR conformance testing; run as an external test suite.
- **Blaze server** — use as a FHIR server accessed via HTTP in development.
- **Mirth/NextGen Connect** — HL7 v2 normalization and routing where a dedicated integration engine is needed.
- **OpenHIM** — audit logging and mediation layer if IHE ATNA compliance is required.

### Use Only as Architectural Prior Art
- **LinuxForHealth FHIR Server** — study `fhir-model` immutable object design.
- **OpenMRS** — study concept dictionary and encounter model.
- **Medplum** — study FHIR application architecture.
- **Aidbox / fhirbase** — study FHIR-on-PostgreSQL storage patterns.
- **VistA** — study longitudinal record model.

### Defer
- **Kotlin FHIR (Google)** — alpha as of September 2025; JVM/KMP support; monitor for production readiness.
- **openEHR / EHRbase** — unless archetype-based clinical modeling is a firm requirement.
- **OMOP CDM** — unless observational research or cohort analytics is in scope.

### Reject
- **Eclipse OHF** — abandoned.
- **OpenCDS** — unclear maintenance status; no visible active release.
- **clj-hl7-fhir (gered)** — abandoned 2014.
- **fhir.clj (fhirbase)** — abandoned 2018.
- **LinuxForHealth FHIR Server** — dormant since December 2022; do not adopt as a running server.

---

## Recommended Experiments

### Experiment 1: FHIR Parsing and Validation via Java Interop
- **Objective:** Establish a minimal Clojure REPL workflow for parsing and validating FHIR R4 resources using HAPI FHIR.
- **Candidate tool:** HAPI FHIR 8.6.0, `org.hl7.fhir.core` validator CLI.
- **Expected artifact:** A Clojure namespace with functions for parse, validate, and FHIRPath evaluation; a test namespace with Synthea-generated resources.
- **Acceptance criteria:** Parse a Synthea Patient bundle; validate against US Core Patient profile; evaluate FHIRPath expression; all in a single REPL session.
- **Estimated setup complexity:** Low (2–4 hours).
- **Likely failure modes:** Package cache network calls during CI; version skew between HAPI and validator CLI IG expectations.
- **Stop conditions:** If HAPI 8.x transitive dependency conflicts prevent clean classpath assembly.

### Experiment 2: Blaze Architecture Study
- **Objective:** Run Blaze locally, load Synthea data, and execute a CQL measure.
- **Candidate tool:** Blaze v1.10.1 (Docker).
- **Expected artifact:** Docker Compose configuration; Clojure HTTP client loading Synthea bundles into Blaze; `$evaluate-measure` call returning a patient count.
- **Acceptance criteria:** 1000 Synthea patients loaded; measure evaluates in under 5 seconds.
- **Estimated setup complexity:** Low (2–4 hours).
- **Likely failure modes:** RocksDB storage configuration; CQL measure compilation errors.
- **Stop conditions:** If Blaze's REST API is insufficient for the use case without code-level access.

### Experiment 3: HL7 v2 Parsing in Clojure
- **Objective:** Compare clojure-hl7-messaging-2-parser and HAPI HL7v2 interop for parsing ADT_A01 messages.
- **Candidate tools:** cmiles74 parser; HAPI HL7v2 via Java interop.
- **Expected artifact:** A Clojure namespace with both implementations; a test set of 50 real-world-structure HL7 v2 ADT messages (hand-authored or publicly sourced ADT messages — Synthea has no HL7 v2 export; see the Corrections note above); performance and ergonomics comparison.
- **Acceptance criteria:** Both parse all 50 messages; comparison of field extraction ergonomics documented.
- **Estimated setup complexity:** Low (2–4 hours).
- **Likely failure modes:** cmiles74 parser limitations on non-standard segment orderings.
- **Stop conditions:** If both parsers fail on the same message type.

### Experiment 4: Bitemporal Clinical Records with XTDB
- **Objective:** Model a patient history with corrections and bitemporal queries using XTDB 2.x.
- **Candidate tool:** XTDB 2.x (in-process, no server required).
- **Expected artifact:** Clojure namespace; 10 patient records with simulated corrections; queries for "what did the system know at time T?" and "what was clinically true at time T?".
- **Acceptance criteria:** Bitemporal queries return correct historical state; correction history is preserved.
- **Estimated setup complexity:** Medium (4–8 hours).
- **Likely failure modes:** XTDB 2.x API changes vs. v1; SQL vs. Datalog query interface choice.
- **Stop conditions:** If XTDB 2.x performance is unacceptable for patient-scale data (test with 1M records).

### Experiment 5: Offline Profile Validation in CI
- **Objective:** Establish a reproducible, offline FHIR validation pipeline using `validator_cli.jar`.
- **Candidate tool:** `org.hl7.fhir.core` validator CLI; Synthea output; US Core 6.1.0 IG.
- **Expected artifact:** Shell script + Clojure subprocess wrapper; CI pipeline that validates 100 Synthea resources offline with a pinned IG version.
- **Acceptance criteria:** Validation passes without network access; same results on repeated runs; failures are reported with OperationOutcome.
- **Estimated setup complexity:** Medium (4–8 hours).
- **Likely failure modes:** Package cache initialization requires network; IG version pinning has bugs.
- **Stop conditions:** If offline mode cannot be achieved without modifying `validator_cli.jar` source.

### Experiment 6: Malli Schema for Clinical Domain Types
- **Objective:** Define Malli schemas for a minimal clinical domain (Patient, Encounter, Observation) and validate against Synthea-generated maps.
- **Candidate tool:** Malli; Synthea FHIR JSON parsed into Clojure maps.
- **Expected artifact:** A Clojure namespace with Malli schemas for core FHIR resources; a test namespace generating conformant and non-conformant examples using `malli.generator`.
- **Acceptance criteria:** Valid Synthea maps pass; intentionally invalid maps produce human-readable errors; generators produce valid maps 100% of the time.
- **Estimated setup complexity:** Low (2–4 hours).
- **Likely failure modes:** FHIR resource complexity exceeds Malli schema ergonomics; open-ended extension elements are hard to schema.
- **Stop conditions:** If Malli schema maintenance cost exceeds the validation value for highly polymorphic FHIR types.

---

## Open Questions

1. **XTDB 2.x operational maturity:** XTDB 2.x introduced major API changes. What is the production operational experience with XTDB 2.x at clinical scale (millions of records, concurrent writes, long-running temporal queries)? What is the failure mode for RocksDB backend on large XTDB installations?

2. **Blaze embeddability:** Can Blaze's storage and CQL components be factored into a library-style dependency, or is the complete server the only reuse unit? Is the Samply team open to extracting library artifacts?

3. **FHIR R5/R6 adoption timeline:** HAPI FHIR supports R5. When will US regulatory requirements (ONC, CMS) require R5? What is the migration path for existing R4 implementations?

4. **fhirpath.clj coverage:** What percentage of the FHIRPath specification does `fhirpath.clj` implement? Is it sufficient for clinical data extraction use cases, or does it require fallback to the Java FHIRPathEngine?

5. **Terminology licensing at scale:** What is the most practical path to embedding SNOMED CT in a deployed JVM application while remaining compliant with SNOMED International licensing? Is HAPI FHIR's bundled SNOMED CT distribution licensed for redistribution?

6. **XTDB vs. Datomic for clinical records:** What are the practical tradeoffs between XTDB 2.x (open source, MIT, built-in bitemporality) and Datomic (commercial, single transaction time axis) for a primary clinical record store at 10M+ patient scale?

7. **CQL engine stability:** The `cqframework/clinical_quality_language` repository shows active Kotlin migration. What is the stability of the current ELM evaluation engine for production use? Are there known correctness issues in specific CQL operators?

8. **SMART on FHIR in Ring:** What is the most tested Clojure Ring middleware stack for SMART on FHIR authorization? Is there an established Keycloak + Ring integration pattern for FHIR compartment access control?

9. **Clojure REPL development with HAPI:** What are the established patterns for managing HAPI FHIR `FhirContext` and JPA `EntityManager` lifecycle in a REPL-driven Clojure development workflow to avoid the "restart after Java initialization" problem?

---

## Conclusions

### What the JVM and Clojure Ecosystem Already Provides

The JVM ecosystem has strong, production-ready libraries for every core FHIR use case: **HAPI FHIR** covers parsing, serialization, validation, FHIRPath, JPA persistence, and SMART on FHIR; **HAPI HL7v2** covers HL7 v2 parsing and MLLP; **org.hl7.fhir.core** provides the canonical profile validator. The **Synthea** generator provides realistic synthetic test data. The **CQL evaluation engine** supports clinical quality measures and CDS. **dcm4che** covers DICOM.

**Blaze** proves that a production FHIR server can be implemented natively in Clojure using standard Clojure patterns (Integrant, Clojure spec, pure functions, anomalies). Its architecture is the most important native-Clojure prior art in the healthcare ecosystem.

### Where Java Interop is Preferable to Native Clojure Implementation

- **FHIR parsing and validation:** HAPI FHIR via Java interop is more capable, better maintained, and more complete than any native Clojure FHIR library. The ergonomic cost of wrapping HAPI's builder API is manageable.
- **HL7 v2 parsing with conformance checking:** HAPI HL7v2 via Java interop is the responsible choice for production HL7 v2 work requiring versioned message models and MLLP.
- **FHIRPath:** The Java `FHIRPathEngine` from `org.hl7.fhir.core` is the reference implementation. `fhirpath.clj` is incomplete. Java interop or subprocess invocation is the more reliable path.
- **CQL evaluation:** The CQL engine is Java/Kotlin. No Clojure-native CQL engine exists.
- **Profile validation in CI:** The `validator_cli.jar` as a subprocess is the lowest-risk path for reproducible offline validation.

### Where Clojure Offers a Meaningful Architectural Advantage

- **Bitemporal clinical records:** Clojure's immutable data model and XTDB's native Clojure API make bitemporal record systems natural to implement in Clojure. The data-as-values paradigm aligns with append-only temporal records.
- **Event sourcing and audit trails:** Clojure's functional style and the rich Kafka/core.async ecosystem support clinical event log architectures cleanly.
- **Functional clinical domain modeling:** Clojure's spec/Malli, multimethods, and protocol-based dispatch allow clinical domain types to be modeled with principled polymorphism and runtime validation without the overhead of a Java class hierarchy.
- **REPL-driven development:** Blaze demonstrates that a production FHIR server can be built with REPL-driven development using Integrant lifecycle management. This is a genuine productivity advantage for iterative healthcare data system development.
- **Data-oriented transformation pipelines:** Clojure's transducer-based transformation pipelines are well-suited to FHIR resource normalization, OMOP ETL, and SQL-on-FHIR projection.
- **Configuration-as-data:** Clinical decision support rules expressed as Clojure data (not compiled code) are easier to audit and to subject to clinical governance review.

### Where Substantial Original Work Would Still Be Required

- A Clojure-native FHIRPath engine with full spec coverage.
- A Clojure SMART on FHIR / OAuth2 / OIDC authorization framework for FHIR compartment-level access control.
- A Clojure-native FHIR terminology client with embedded SNOMED CT, LOINC, and RxNorm support.
- A Clojure-native HL7 v2 conformance profile validator.
- A production-grade Clojure CDS Hooks server implementation.
- Bitemporal clinical record storage in XTDB with full FHIR R4 search support (requires building the search parameter index layer that Blaze already has for its own storage engine).
- Any CDA/C-CDA generation tooling in Clojure.

---

## Sources

| Source Title | Organization / Project | URL | Access Date | Source Type | Claims Supported |
|---|---|---|---|---|---|
| HAPI FHIR Documentation | HAPI FHIR / Smile Digital Health | [https://hapifhir.io/hapi-fhir/docs/](https://hapifhir.io/hapi-fhir/docs/) | 2026-07-12 | Official documentation | HAPI FHIR modules, versions, API |
| HAPI FHIR GitHub | hapifhir/hapi-fhir | [https://github.com/hapifhir/hapi-fhir](https://github.com/hapifhir/hapi-fhir) | 2026-07-12 | Official repository | Release history, maintenance status |
| HAPI FHIR Test Server | HAPI FHIR | [https://hapi.fhir.org/](https://hapi.fhir.org/) | 2026-07-12 | Official service | Snapshot version 8.11.4 |
| HAPI HL7v2 Documentation | hapifhir/hapi-hl7v2 | [https://hapifhir.github.io/hapi-hl7v2/](https://hapifhir.github.io/hapi-hl7v2/) | 2026-07-12 | Official documentation | HL7 v2 library capabilities |
| HAPI HL7v2 GitHub | hapifhir/hapi-hl7v2 | [https://github.com/hapifhir/hapi-hl7v2](https://github.com/hapifhir/hapi-hl7v2) | 2026-07-12 | Official repository | Maintenance status, release history |
| org.hl7.fhir.core GitHub | hapifhir/org.hl7.fhir.core | [https://github.com/hapifhir/org.hl7.fhir.core](https://github.com/hapifhir/org.hl7.fhir.core) | 2026-07-12 | Official repository | Validator, FHIRPath, IG tools |
| Blaze GitHub | samply/blaze | [https://github.com/samply/blaze](https://github.com/samply/blaze) | 2026-07-12 | Official repository | Blaze architecture, Clojure, release |
| Blaze DEVELOPMENT.md | samply/blaze | [https://github.com/samply/blaze/blob/main/DEVELOPMENT.md](https://github.com/samply/blaze/blob/main/DEVELOPMENT.md) | 2026-07-12 | Official source | Java version, Integrant, Clojure patterns |
| Blaze FHIR API docs | samply/blaze | [https://github.com/samply/blaze/blob/main/docs/api.md](https://github.com/samply/blaze/blob/main/docs/api.md) | 2026-07-12 | Official documentation | FHIR API capabilities |
| Medplum GitHub | medplum/medplum | [https://github.com/medplum/medplum](https://github.com/medplum/medplum) | 2026-07-12 | Official repository | Release history, maintenance |
| Medplum Open Source | Medplum | [https://www.medplum.com/open-source](https://www.medplum.com/open-source) | 2026-07-12 | Official documentation | Apache 2.0 license, open-source components |
| Medplum March 2026 Update | Medplum | [https://www.medplum.com/blog/march-2026-update](https://www.medplum.com/blog/march-2026-update) | 2026-07-12 | Official blog | v5.1.x release, HITRUST certification |
| Synthea GitHub | synthetichealth/synthea | [https://github.com/synthetichealth/synthea](https://github.com/synthetichealth/synthea) | 2026-07-12 | Official repository | Synthea capabilities, maintenance |
| Synthea eCQI | HealthIT.gov | [https://ecqi.healthit.gov/tool/synthea](https://ecqi.healthit.gov/tool/synthea) | 2026-07-12 | Official US government resource | Synthea description |
| CQL Evaluation Engine eCQI | HealthIT.gov | [https://ecqi.healthit.gov/tool/cql-evaluation-engine-java](https://ecqi.healthit.gov/tool/cql-evaluation-engine-java) | 2026-07-12 | Official US government resource | CQL engine Java description |
| CQL GitHub | cqframework/clinical_quality_language | [https://github.com/cqframework/clinical_quality_language](https://github.com/cqframework/clinical_quality_language) | 2026-07-12 | Official repository | CQL ecosystem, maintenance |
| CQL Reference Implementations | HL7 | [https://cql.hl7.org/10-c-referenceimplementations.html](https://cql.hl7.org/10-c-referenceimplementations.html) | 2026-07-12 | Official HL7 specification | CQL Apache 2.0, Java |
| cqf-ruler GitHub | cqframework/cqf-ruler | [https://github.com/cqframework/cqf-ruler](https://github.com/cqframework/cqf-ruler) | 2026-07-12 | Official repository | CDS Hooks, FHIR plugin |
| clinical_quality_language topics | GitHub | [https://ithub.global.ssl.fastly.net/topics/clinical-quality-language](https://ithub.global.ssl.fastly.net/topics/clinical-quality-language) | 2026-07-12 | Secondary (GitHub topics) | CQL ecosystem projects |
| dcm4che GitHub | dcm4che/dcm4che | [https://github.com/dcm4che/dcm4che](https://github.com/dcm4che/dcm4che) | 2026-07-12 | Official repository | DICOM toolkit, maintenance |
| dcm4chee-arc-light5 5.34.3 | SourceForge | [https://sourceforge.net/projects/dcm4che/files/dcm4chee-arc-light5/5.34.3/](https://sourceforge.net/projects/dcm4che/files/dcm4chee-arc-light5/5.34.3/) | 2026-07-12 | Official release | dcm4che 5.34.3 April 2026 |
| LinuxForHealth FHIR GitHub | LinuxForHealth/FHIR | [https://github.com/LinuxForHealth/FHIR](https://github.com/LinuxForHealth/FHIR) | 2026-07-12 | Official repository | Dormant status, Dec 2022 last release |
| Apache Camel FHIR | Apache Software Foundation | [https://camel.apache.org/components/4.18.x/fhir-component.html](https://camel.apache.org/components/4.18.x/fhir-component.html) | 2026-07-12 | Official documentation | Camel FHIR component |
| OMOP CDM | OHDSI | [https://ohdsi.github.io/CommonDataModel/](https://ohdsi.github.io/CommonDataModel/) | 2026-07-12 | Official documentation | OMOP CDM v5.4 |
| OMOP tools list | AndyRae | [https://github.com/AndyRae/omop-list](https://github.com/AndyRae/omop-list) | 2026-07-12 | Community list | OMOP ecosystem tools |
| EHRbase GitHub | ehrbase/ehrbase | [https://github.com/ehrbase/ehrbase](https://github.com/ehrbase/ehrbase) | 2026-07-12 | Official repository | openEHR server |
| fhirpath.clj GitHub | HealthSamurai/fhirpath.clj | [https://github.com/HealthSamurai/fhirpath.clj](https://github.com/HealthSamurai/fhirpath.clj) | 2026-07-12 | Official repository | Clojure FHIRPath |
| sql-on-fhir.clj GitHub | HealthSamurai/sql-on-fhir.clj | [https://github.com/HealthSamurai/sql-on-fhir.clj](https://github.com/HealthSamurai/sql-on-fhir.clj) | 2026-07-12 | Official repository | SQL-on-FHIR Clojure |
| fhir.clj GitHub | fhirbase/fhir.clj | [https://github.com/fhirbase/fhir.clj](https://github.com/fhirbase/fhir.clj) | 2026-07-12 | Official repository | Abandoned Clojure FHIR client |
| clojure-hl7-messaging-2-parser | cmiles74 | [https://github.com/cmiles74/clojure-hl7-messaging-2-parser](https://github.com/cmiles74/clojure-hl7-messaging-2-parser) | 2026-07-12 | Official repository | Clojure HL7 v2 parser |
| Health Samurai Open Source | Health Samurai | [https://www.health-samurai.io/opensource](https://www.health-samurai.io/opensource) | 2026-07-12 | Official website | Health Samurai open source projects |
| Aidbox FHIR Server | Health Samurai | [https://www.health-samurai.io/fhir-server](https://www.health-samurai.io/fhir-server) | 2026-07-12 | Official website | Aidbox commercial licensing, features |
| fhirbase GitHub | fhirbase/fhirbase | [https://github.com/fhirbase/fhirbase](https://github.com/fhirbase/fhirbase) | 2026-07-12 | Official repository | fhirbase PostgreSQL FHIR CLI |
| HAPI FHIR JPA Starter | hapifhir/hapi-fhir-jpaserver-starter | [https://github.com/hapifhir/hapi-fhir-jpaserver-starter](https://github.com/hapifhir/hapi-fhir-jpaserver-starter) | 2026-07-12 | Official repository | JPA server starter |
| HAPI FHIR 8.4.0 Release | Smile Digital Health | [https://www.smiledigitalhealth.com/event/hapi-fhir-amplification-release](https://www.smiledigitalhealth.com/event/hapi-fhir-amplification-release) | 2026-07-12 | Official release notes | HAPI FHIR 8.4.0 features, CDA-to-FHIR |
| HAPI FHIR 8.6.0 Release | Smile Digital Health | [https://www.smiledigitalhealth.com/smile-cdr-2025-11-r01-euphoria](https://www.smiledigitalhealth.com/smile-cdr-2025-11-r01-euphoria) | 2026-07-12 | Official release notes | HAPI FHIR 8.6.0 |
| CVE-2026-33180 | Miggo Security | [https://www.miggo.io/vulnerability-database/cve/CVE-2026-33180](https://www.miggo.io/vulnerability-database/cve/CVE-2026-33180) | 2026-07-12 | Security advisory | HAPI FHIR CVE |
| Blaze conformance scorecard | mock.health | [https://mock.health/conformance/servers/blaze](https://mock.health/conformance/servers/blaze) | 2026-07-12 | Secondary (conformance testing) | Blaze 91% FHIR R4 conformance |
| OpenMRS architecture | OpenMRS | [https://openmrs.atlassian.net/wiki/spaces/docs/pages/25476856/Technical+Overview](https://openmrs.atlassian.net/wiki/spaces/docs/pages/25476856/Technical+Overview) | 2026-07-12 | Official documentation | OpenMRS architecture |
| OpenEMR v8 | OpenEMR Community | [https://www.open-emr.org/blog/openemr-version-8-released/](https://www.open-emr.org/blog/openemr-version-8-released/) | 2026-07-12 | Official blog | OpenEMR v8 release |
| WorldVistA | WorldVistA | [https://worldvista.org/](https://worldvista.org/) | 2026-07-12 | Official website | VistA open source status |
| OpenHIM | Jembi Health Systems | [https://openhim.org/](https://openhim.org/) | 2026-07-12 | Official website | OpenHIM capabilities |
| NextGen Connect releases | nextgenhealthcare/connect | [https://github.com/nextgenhealthcare/connect/releases](https://github.com/nextgenhealthcare/connect/releases) | 2026-07-12 | Official repository | Mirth Connect releases |
| CDS Hooks specification | HL7 / SMART | [https://cds-hooks.org/](https://cds-hooks.org/) | 2026-07-12 | Official specification | CDS Hooks standard |
| HL7 C-CDA 2.1 Schematron | HL7 | [https://github.com/HL7/CDA-ccda-2.1](https://github.com/HL7/CDA-ccda-2.1) | 2026-07-12 | Official repository | C-CDA Schematron validation |
| Inferno | inferno-framework | [https://github.com/orgs/inferno-framework/repositories](https://github.com/orgs/inferno-framework/repositories) | 2026-07-12 | Official repository | Inferno FHIR testing |
| Inferno eCQI | HealthIT.gov | [https://ecqi.healthit.gov/tool/inferno](https://ecqi.healthit.gov/tool/inferno) | 2026-07-12 | Official US government resource | Inferno description |
| Gazelle IHE | IHE | [https://gazelle.ihe.net/](https://gazelle.ihe.net/) | 2026-07-12 | Official IHE resource | Gazelle test platform |
| LOINC License | Regenstrief Institute | [https://loinc.org/kb/license/](https://loinc.org/kb/license/) | 2026-07-12 | Official license | LOINC licensing terms |
| XTDB bitemporal blog | XTDB | [https://xtdb.com/blog/building-a-bitemp-index-2-resolution](https://xtdb.com/blog/building-a-bitemp-index-2-resolution) | 2026-07-12 | Official blog | XTDB bitemporal index |
| OpenMRS CDS Drools | OpenMRS | [https://openmrs.atlassian.net/wiki/spaces/projects/pages/603750673/OpenMRS+CDS+Engine+powered+by+Drools](https://openmrs.atlassian.net/wiki/spaces/projects/pages/603750673/OpenMRS+CDS+Engine+powered+by+Drools) | 2026-07-12 | Official documentation | Drools in OpenMRS |
| Kotlin FHIR (Google) | Google Open Health Stack | [https://opensource.googleblog.com/2025/09/introducing-kotlin-fhir-a-new-library-to-bring-fhir-to-multiplatform.html](https://opensource.googleblog.com/2025/09/introducing-kotlin-fhir-a-new-library-to-bring-fhir-to-multiplatform.html) | 2026-07-12 | Official Google blog | Kotlin FHIR alpha |
| Android FHIR SDK | Google Open Health Stack | [https://developers.google.com/open-health-stack/android-fhir](https://developers.google.com/open-health-stack/android-fhir) | 2026-07-12 | Official documentation | Android FHIR SDK Kotlin |
| Health Samurai Clojure meetup | Reddit r/Clojure | [https://www.reddit.com/r/Clojure/comments/1p7fxy0/clojure_online_meetup_by_health_samurai/](https://www.reddit.com/r/Clojure/comments/1p7fxy0/clojure_online_meetup_by_health_samurai/) | 2026-07-12 | Community secondary | Health Samurai Clojure activity |
| Implementing FHIR in Dynamic Languages | Health Samurai | [https://www.health-samurai.io/articles/implementing-fhir-in-dynamic-languages](https://www.health-samurai.io/articles/implementing-fhir-in-dynamic-languages) | 2026-07-12 | Official blog | Clojure/dynamic language FHIR |
| HAPI FHIR Intro | hapifhir.io | [https://hapifhir.io/hapi-fhir/docs/v/7.2.3/getting_started/](https://hapifhir.io/hapi-fhir/docs/v/7.2.3/getting_started/) | 2026-07-12 | Official documentation | HAPI FHIR FhirContext, parsers, Java API |
| awesome-FHIR list | fhir-fuel | [https://github.com/fhir-fuel/awesome-FHIR](https://github.com/fhir-fuel/awesome-FHIR) | 2026-07-12 | Community list | fhirpath.clj, Clojure FHIR references |
| FHIR Mapping Language | HL7 Confluence | [https://confluence.hl7.org/spaces/FHIR/pages/76158820/Using+the+FHIR+Mapping+Language](https://confluence.hl7.org/spaces/FHIR/pages/76158820/Using+the+FHIR+Mapping+Language) | 2026-07-12 | Official HL7 resource | StructureMapUtilities, CDA-FHIR mapping |
| FHIR Shorthand SUSHI | HL7/FHIR | [https://github.com/FHIR/sushi](https://github.com/FHIR/sushi) | 2026-07-12 | Official repository | FSH/SUSHI tooling |
| Ontoserver | CSIRO | [https://www.ontoserver.csiro.au/site/our-solutions/ontoserver/](https://www.ontoserver.csiro.au/site/our-solutions/ontoserver/) | 2026-07-12 | Official website | Terminology server |
| SMART on FHIR documentation | SMART Health IT | [https://smarthealthit.org/](https://smarthealthit.org/) | 2026-07-12 | Official specification | SMART on FHIR standard |
| AnyBio Medplum press release | BusinessWire | [https://finance.yahoo.com/healthcare/articles/anybio-medplum-launch-fhir-native-175300681.html](https://finance.yahoo.com/healthcare/articles/anybio-medplum-launch-fhir-native-175300681.html) | 2026-07-12 | News / secondary | Medplum production use cases |

---

*Report generated: July 12, 2026. All URLs verified as syntactically correct Markdown links. This report does not contain [cite:N] tags, proprietary citation markers, or unresolved footnotes. All citations are standard clickable Markdown links.*
