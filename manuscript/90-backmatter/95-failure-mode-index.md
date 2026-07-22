# Failure Mode Index

A lookup table: symptom observed in the field → likely cause → chapter to
read. Add a row whenever a new failure mode is diagnosed; this index only
stays useful if it grows with real incidents.

| Symptom observed | Likely cause | Chapter |
|---|---|---|
| Round-trip test fails only on documents containing narrative text | Narrative-vs-entries span: the narrative and the structured entries can drift apart independently | 31, 34 |
| Translated code is missing or null in the target | Source used a local code outside the standard's published map (e.g. outside Table 0396) | 33, 34 |
| Same input produces a different "golden" output across test runs | Generator invoked without a recorded/fixed seed | 23 |
| A field no purpose set claims acquires a downstream consumer; a later cleanup that drops it breaks that consumer | Accidental contract: no residue sheet enumerated and signed off on what the transform carries | 22 |
| A quality measure's counts can't be reconciled with the source system months later | Silent code normalization changed a value no in-scope query was checking; the second recipient's purpose set was never elicited | 22 |
| A translation property test passes on a mapping the ConceptMap itself annotates as wider, narrower, or inexact | Equivalence annotation read as if it were equivalent — the check asserts plain equality where the map already declared the pair non-equivalent | 34, 42 |
| An injected defect fails to make any test fail | The mutation's tagged property is missing, too weak, or reading the wrong field — the corpus found a hole in the test suite, not the transform | 23 |
