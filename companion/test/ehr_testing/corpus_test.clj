(ns ehr-testing.corpus-test
  "Chapter 22's lattice split, made executable: the same designed mutation
  (`ehr-testing.corpus/normalize-resulted-code-to-ordered`) breaks the
  quality-measure recipient's `:code-in-value-set` row while leaving every
  medication-reconciliation row green, for the literal sample message."
  (:require [clojure.test :refer [deftest is testing]]
            [ehr-testing.specimen :as specimen]
            [ehr-testing.properties :as properties]
            [ehr-testing.corpus :as corpus]))

(defn- row-by-name
  "Find a purpose-set row by :name in a public row table (properties/
  purpose-set-med-rec or purpose-set-quality-measure); `properties/
  row-by-name` is private, so this is the test namespace's own lookup over
  the same public data."
  [table row-name]
  (first (filter #(= row-name (:name %)) table)))

(defn- row-agrees?
  "Run one purpose-set row's triangle for a supplied composite transform
  `f'` (here, `oru->observation` composed with the mutation under test)
  against an ORIGINAL, unmutated `ParsedOru` as the source of truth --
  mirroring `properties/property-for-row`, which hardcodes
  `oru->observation` and is private, without changing that namespace's API."
  [row original-oru f'-observation]
  (properties/agree? (:tolerance row)
                      ((:q row) f'-observation)
                      ((:q-source row) original-oru)))

(deftest normalize-resulted-code-to-ordered-mutation-test
  (let [original (specimen/parse-oru specimen/sample-oru-message)
        mutated (corpus/normalize-resulted-code-to-ordered original)
        f'-observation (specimen/oru->observation mutated)]
    (testing "the mutation breaks its tagged target: quality-measure :code-in-value-set"
      (let [row (row-by-name properties/purpose-set-quality-measure :code-in-value-set)]
        (is (false? (row-agrees? row original f'-observation))
            "resulted code 2345-7 is in the value set; ordered code 1554-5 is not")))
    (testing "the mutation leaves the other recipient green: all six purpose-set-med-rec rows"
      (doseq [row properties/purpose-set-med-rec]
        (is (true? (row-agrees? row original f'-observation))
            (str "expected row " (:name row) " to still agree"))))))
