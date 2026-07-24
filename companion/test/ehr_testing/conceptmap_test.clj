(ns ehr-testing.conceptmap-test
  "Chapter 34's worked translation check, including the dishonest
  alternative the chapter warns against (C26): asserting plain equality
  through a row the map itself annotated as not-equality-safe."
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [ehr-testing.conceptmap :as cm]))

(def local->loinc
  "The chapter's worked map: a site's local lab codes mapped to LOINC.

  The site uses one code, GLU, for every glucose result regardless of
  specimen -- serum, plasma, or fingerstick whole blood -- while LOINC
  distinguishes 2345-7 (Glucose [Mass/volume] in Serum or Plasma, the
  specimen's own OBX-3 code) from 2339-0 (Glucose [Mass/volume] in
  Blood). So GLU maps to *two* LOINC codes, each annotated :inexact:
  the honest record of a local partition coarser than LOINC's. The
  site's newer, specimen-specific code GLU-SP genuinely means what
  2345-7 means, and its row says so."
  {:source-system "urn:local:acme-lab"
   :target-system "http://loinc.org"
   :mappings [{:source "GLU"    :target "2345-7" :equivalence :inexact}
              {:source "GLU"    :target "2339-0" :equivalence :inexact}
              {:source "GLU-SP" :target "2345-7" :equivalence :equivalent}]})

(defn dishonest-equal?
  "The check Chapter 34 warns against: 'translated correctly' defined as
  'the produced code appears among the map's targets for this source
  code' -- ignoring the equivalence annotations entirely. Kept here, in
  the test namespace, so its failure mode is executable rather than
  hypothetical."
  [concept-map source-code produced-target]
  (boolean (some #(= produced-target (:target %))
                 (cm/translate concept-map source-code))))

(deftest worked-map-is-structurally-valid
  (is (cm/valid-map? local->loinc)))

(deftest translate-returns-the-relation-not-a-representative
  (testing "GLU maps to two LOINC codes -- n-to-m is the generic case"
    (is (= 2 (count (cm/translate local->loinc "GLU"))))))

(deftest honest-check-asserts-only-what-the-map-claims
  (testing "equivalent row: equality asserted and satisfied"
    (is (= :asserted-equal
           (:verdict (cm/check-translation local->loinc "GLU-SP" "2345-7")))))
  (testing "equivalent row: equality asserted and violated"
    (is (= :wrong
           (:verdict (cm/check-translation local->loinc "GLU-SP" "2339-0")))))
  (testing "inexact rows: equality refused, annotations surfaced"
    (let [v (cm/check-translation local->loinc "GLU" "2345-7")]
      (is (= :flagged (:verdict v)))
      (is (= #{:inexact} (:annotations v)))
      (is (= #{"2345-7" "2339-0"} (:candidates v)))))
  (testing "unmapped source code"
    (is (= :unmapped
           (:verdict (cm/check-translation local->loinc "HGB" "718-7"))))))

(deftest the-dishonest-check-passes-where-it-should-not-assert
  (testing "C26 executable: plain equality through an :inexact row passes"
    (is (true? (dishonest-equal? local->loinc "GLU" "2345-7")))
    (is (= :flagged
           (:verdict (cm/check-translation local->loinc "GLU" "2345-7"))))))

;; --- Property: equality is asserted iff the map annotates it safe --------

(def gen-code (gen/fmap #(str "C" %) gen/nat))

(def gen-mapping-row
  (gen/hash-map :source gen-code
                :target gen-code
                :equivalence (gen/elements (vec cm/equivalence-codes))))

(def gen-concept-map
  (gen/fmap (fn [rows]
              {:source-system "urn:example:src"
               :target-system "urn:example:tgt"
               :mappings (vec rows)})
            (gen/not-empty (gen/vector gen-mapping-row 1 8))))

(def equality-asserted-iff-annotated-safe
  "For every row of every map: checking that row's own (source, target)
  pair asserts equality exactly when some equality-safe row exists for
  that source code. The honest check's behavior is a function of the
  map's annotations -- never of the codes' plausibility."
  (prop/for-all [concept-map gen-concept-map]
    (every? (fn [{:keys [source target]}]
              (let [safe? (some #(and (= source (:source %))
                                      (contains? cm/equality-safe
                                                 (:equivalence %)))
                                (:mappings concept-map))
                    verdict (cm/check-translation concept-map source target)]
                (= (boolean safe?) (cm/asserts-equality? verdict))))
            (:mappings concept-map))))

(deftest equality-asserted-iff-annotated-safe-holds
  (let [result (tc/quick-check 200 equality-asserted-iff-annotated-safe)]
    (is (true? (:pass? result)) (pr-str result))))
