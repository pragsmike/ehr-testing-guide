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

Why SNOMED, LOINC, ICD, and RxNorm disagree on purpose, not by accident.

## Maps as relations, not functions

n-to-m, partial, context-dependent — operationally, with a real
cross-terminology example, before calling it a relation.

## Spans with no section

What it means for a map to have no consistent inverse, and why that's the
generic case here, not the exception.

## UMLS as terminology-level universal object

How UMLS relates to the terminologies the way the event log (Ch. 32)
relates to the formats — and where that echo breaks down.

## ConceptMap and what it predicts for testing

FHIR's ConceptMap equivalence annotations as honesty about the relation,
and the test properties this structure implies.
