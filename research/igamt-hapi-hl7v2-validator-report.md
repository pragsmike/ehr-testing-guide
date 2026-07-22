# IGAMT, HAPI HL7v2, and Building an HL7v2 Validator from Clojure

## Overview

IGAMT is primarily an authoring environment for HL7 v2 implementation guides and conformance profiles, while HAPI HL7v2 provides Java-based parsing and conformance tooling that can be invoked from Clojure through Java interop. IGAMT is used to create HL7 v2.x implementation guides that contain one or more conformance profiles, and NIST positions it as part of a broader toolchain for standards development and testing. See the NIST IGAMT site, the NIST tool overview, and the HL7 v2 implementation guide methodology: [IGAMT](https://hl7v2-igamt-2.nist.gov/), [NIST HL7 v2 Conformance Testing Tools](https://www.nist.gov/itl/health-it-testing-infrastructure/testing-tools/hl7-v2-conformance-testing-tools), [HL7 v2 Implementation Guides](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_HL7_v2_Implementation_Guides.html).

For a Clojure implementation, the natural split is to use IGAMT to define the conformance artifacts and HAPI HL7v2 as the runtime engine that parses messages, loads profiles, and validates messages against those profiles. HAPI documents runtime validation against XML conformance profiles and also describes generation of constrained Java classes from a profile. See: [HAPI Conformance Tools](https://hapifhir.github.io/hapi-hl7v2/conformance.html), [ProfileParser API](https://hapifhir.github.io/hapi-hl7v2/base/apidocs/ca/uhn/hl7v2/conf/parser/ProfileParser.html).

## What IGAMT Does

IGAMT, the Implementation Guide Authoring and Management Tool, is used to create HL7 v2 implementation guides that contain one or more conformance profiles. Those guides can include narrative specification content alongside structured conformance artifacts. See: [IGAMT](https://hl7v2-igamt-2.nist.gov/), [NIST HL7 v2 General Validation Tool](https://hl7v2-gvt.nist.gov/gvt), [usnistgov/hl7-igamt](https://github.com/usnistgov/hl7-igamt).

Within the HL7 v2 conformance methodology, the implementation guide is the organizing container for a family of related interactions, while the message profile is the governing artifact that specifies the requirements for a particular interface or message interaction. See: [HL7 v2 Implementation Guides](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_HL7_v2_Implementation_Guides.html), [Profile Construction](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Profile_Construction.html), [Message Profiles](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Message_Profiles.html).

## What HAPI HL7v2 Adds

HAPI HL7v2 provides the runtime side: message parsing, model handling, and conformance validation against XML profiles. Its conformance tooling accepts a profile and validates a message against that profile at runtime, and its parser APIs expose profile parsing as first-class functionality. See: [HAPI Conformance Tools](https://hapifhir.github.io/hapi-hl7v2/conformance.html), [ProfileParser API](https://hapifhir.github.io/hapi-hl7v2/base/apidocs/ca/uhn/hl7v2/conf/parser/ProfileParser.html), [HAPI TestPanel Validation](https://hapifhir.github.io/hapi-hl7v2/hapi-testpanel/validation.html).

For a Clojure system, this means a straightforward interop boundary: Clojure can own application flow, profile selection, persistence, routing, and custom business rules, while HAPI does the low-level Java work of parsing ER7 messages and applying conformance checks. If stronger typing is useful, HAPI also supports generation of constrained classes from a conformance profile. See: [HAPI Conformance Tools](https://hapifhir.github.io/hapi-hl7v2/conformance.html).

## How a Validator Could Be Built

A practical architecture is to treat validation as a pipeline:

1. Accept an inbound HL7 v2 message in ER7 form.
2. Parse it with HAPI.
3. Determine message type, trigger event, structure, and HL7 version.
4. Resolve the matching profile artifact exported from IGAMT.
5. Run conformance validation.
6. Apply additional terminology and business-rule validation.
7. Return structured validation results suitable for APIs, logs, or UI display.

That division fits both the HAPI runtime model and the NIST ecosystem, where IGAMT-authored artifacts feed downstream validation and testing tools. NIST describes GVT as a hosting platform for user-generated conformance testing tools built from IGAMT and TCAMT artifacts, and CDC also maintains a wrapper around the NIST validator. See: [GVT](https://www.nist.gov/itl/nist-health-it-program-legacy-website/gvt), [General-purpose Testing Tool for HL7 v2](https://www.nist.gov/document/product-brief-nist-gvt-tool), [CDC lib-hl7v2-nist-validator](https://github.com/CDCgov/lib-hl7v2-nist-validator).

### Clojure-Oriented Runtime Components

A useful Clojure implementation would typically include:

- A Java interop wrapper around HAPI parsing and profile validation.
- A profile repository keyed by version, message structure, trigger event, and local implementation guide version.
- A validation orchestration layer that chooses the correct profile and composes multiple validators.
- A terminology layer for value sets and code tables not fully captured by structural profile validation.
- A rule engine or ordinary Clojure predicate layer for local semantic checks.
- A reporting layer that converts raw validation output into stable application-level error objects.

In practice, HAPI can cover syntax and profile conformance, but production validation usually needs local rules around vocabulary bindings, cross-field dependencies, and workflow semantics. See: [HAPI Conformance Tools](https://hapifhir.github.io/hapi-hl7v2/conformance.html), [Constraints](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Constraints.html), [CDC lib-hl7v2-nist-validator](https://github.com/CDCgov/lib-hl7v2-nist-validator).

## What Else Is Needed

IGAMT and HAPI together are not the whole system. A working validator generally also needs profile lifecycle management, versioning discipline, table and value set handling, test fixtures, negative test cases, and operational packaging such as a REST API or batch validation service. NIST’s own toolchain separates authoring, test-case management, and hosted validation rather than collapsing everything into one component. See: [IGAMT](https://hl7v2-igamt-2.nist.gov/), [GVT](https://www.nist.gov/itl/nist-health-it-program-legacy-website/gvt), [NIST HL7 v2 Conformance Testing Tools](https://www.nist.gov/itl/health-it-testing-infrastructure/testing-tools/hl7-v2-conformance-testing-tools).

Depending on scope, the missing pieces often include:

- Profile storage and retrieval.
- Artifact export/import handling from IGAMT.
- Vocabulary and value-set services.
- Regression test suites.
- Message fixtures representing real partner behavior.
- Exception mapping and human-readable diagnostics.
- Deployment infrastructure and observability.

If the goal is only local runtime validation inside an integration service, this can stay relatively small. If the goal is a public validator comparable to a NIST-hosted tool, the surrounding infrastructure becomes a significant part of the project.

## What Tools Are Available to Build Profiles

The main profile authoring tool in this ecosystem is IGAMT. NIST explicitly presents it as the authoring tool for HL7 v2 implementation guides and conformance profiles. See: [IGAMT](https://hl7v2-igamt-2.nist.gov/), [NIST HL7 v2 Conformance Testing Tools](https://www.nist.gov/itl/health-it-testing-infrastructure/testing-tools/hl7-v2-conformance-testing-tools).

Other available options include older HL7 v2 tooling and direct XML-level work:

- HAPI documentation references Message Workbench as a way to create XML conformance profiles. See: [HAPI TestPanel Validation](https://hapifhir.github.io/hapi-hl7v2/hapi-testpanel/validation.html), [HAPI Conformance Tools](https://hapifhir.github.io/hapi-hl7v2/conformance.html).
- Direct authoring against the HL7 message profile schema is technically possible, but it is much more brittle and much less pleasant than using a dedicated authoring tool. See: [HL7MessageProfileSchema.xsd](https://connectathon.ihe-catalyst.net/XSD/HL7/V2/HL7MessageProfileSchema.xsd).

For most teams, IGAMT is the most realistic authoring environment because it aligns with the conformance methodology and with downstream NIST validation tooling.

## What Constitutes a Profile

A message profile is based on a standard HL7 v2 message structure and refines it for a specific context of use. The HL7 conformance materials describe it as the artifact that specifies more precise requirements than the base standard for a concrete interface. See: [Message Profiles](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Message_Profiles.html), [Profile Construction](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Profile_Construction.html), [HL7 Version 2.8.2 Chapter 2B](https://www.hl7.eu/HL7v2x/v282/std282/ch02b.html).

A profile typically constrains or defines:

- The base message structure and event.
- Segment and group presence.
- Cardinality and repetition.
- Usage such as required, optional, or conditional.
- Data types and length restrictions.
- Allowed tables, code systems, and value sets.
- Predicates, co-constraints, and other conditional rules.
- Potentially slicing and more specialized constraint artifacts in richer toolchains.

In other words, the profile is the formalized statement of what “valid for this interface” means, as distinct from merely “legal HL7.” See: [Constraints](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Constraints.html), [CDC lib-hl7v2-nist-validator](https://github.com/CDCgov/lib-hl7v2-nist-validator).

## How Much Manual Work It Is

The manual effort is usually concentrated in profile definition and curation rather than in the mechanics of message parsing. Parsing and basic validation can be delegated to HAPI, but turning a loosely understood trading-partner interface into an accurate profile is a modeling exercise that requires domain decisions. See: [HAPI Conformance Tools](https://hapifhir.github.io/hapi-hl7v2/conformance.html), [Profile Construction](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Profile_Construction.html), [Constraints](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Constraints.html).

The workload tends to scale roughly like this:

| Scope | Typical effort | Why |
|---|---|---|
| Basic syntax validation | Low | Mostly parser setup and generic validation rules. |
| Profile-based validation using an existing implementation guide | Moderate | The structure already exists, but local adaptation and testing still take time. |
| New local profile from scratch | High | The team must decide and encode all interface constraints explicitly. |
| Fully operational validation service | High to very high | In addition to profiles, the project needs testing, diagnostics, deployment, and lifecycle management. |

The most time-consuming work usually appears where the specification contains implicit operational knowledge. A rule like “PID-3 is required” is easy to encode, while a rule like “PID-3 is required unless the patient is temporarily unidentified, in which case PV1-19 and local identifier policies become conditionally required” is where the authoring effort becomes substantial. See: [Constraints](https://v2.hl7.org/conformance/HL7v2_Conformance_Methodology_R1_O1_Ballot_Revised_D9_-_September_2019_Constraints.html), [Hl7 v2 messaging conformance jan 2011](https://www.slideshare.net/slideshow/hl7-v2-messaging-conformance-jan-2011/7136635).

## Recommended Clojure Strategy

For a Clojure-first implementation, the most pragmatic approach is:

- Use IGAMT as the authoring surface for implementation guides and profiles.
- Export and version those artifacts outside the running service.
- Use HAPI HL7v2 through Java interop for parsing and profile-based validation.
- Add Clojure-native rule composition for local semantic constraints and integration-specific policy.
- Keep the domain model of validation results in Clojure data structures rather than leaking Java classes throughout the application.

That approach lets the JVM ecosystem handle HL7’s existing Java infrastructure while preserving the advantages of Clojure for orchestration, data transformation, testing, and rule composition. The result is usually easier to evolve than attempting to reimplement HL7 parsing and conformance semantics from scratch.
