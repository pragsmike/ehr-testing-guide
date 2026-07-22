(ns ehr-testing.corpus
  "The corpus's mutation layer (Chapter 23): controlled defect injection.

  A test corpus has three layers -- golden cases (hand-authored oracles),
  generated cases (volume, via `ehr-testing.properties`'s test.check
  generators), and mutated cases, this namespace's business. A mutation
  takes a known-good `ParsedOru` and applies one precise, documented
  change -- a labelled defect, not a hand-built broken case from nothing --
  so that what the mutated case tests is never in doubt. Each mutation is
  tagged with the purpose-set question (a row name in
  `ehr-testing.properties/purpose-set-med-rec` or
  `purpose-set-quality-measure`) it is expected to break; a mutation that
  fails to break its tagged row reveals a missing or too-weak property in
  the suite, not a bug in the transform.")

(defn normalize-resulted-code-to-ordered
  "The chapter's designed mutation: a 'cleanup' transform that overwrites
  the resulted test code (OBX-3, `:observation-identifier`) with the
  ordered test code (OBR-4, `:ordered-test-identifier`), modeling the
  cleanup-transform stage from Chapter 22's lattice discussion.

  Tag: expected to break the quality-measure purpose set's
  `:code-in-value-set` row (the sample's resulted code 2345-7 is in the
  measure's value set, its ordered code 1554-5 is not) and to leave every
  `purpose-set-med-rec` row green (none of the six medication-reconciliation
  queries reads the code at that grain)."
  [parsed-oru]
  (assoc parsed-oru :observation-identifier (:ordered-test-identifier parsed-oru)))
