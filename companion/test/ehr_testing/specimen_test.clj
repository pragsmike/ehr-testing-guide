(ns ehr-testing.specimen-test
  "Tests for the specimen's implemented half (Chapter 21, ADR-0007):
  `parse-oru` and `oru->observation` against the literal sample message,
  and the six purpose-set properties from `ehr-testing.properties` as
  test.check defspecs."
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [malli.core :as m]
            [ehr-testing.specimen :as specimen]
            [ehr-testing.schemas :as schemas]
            [ehr-testing.properties :as properties]))

(deftest parse-oru-test
  (testing "parsing the sample message yields a schema-valid ParsedOru"
    (let [parsed (specimen/parse-oru specimen/sample-oru-message)]
      (is (m/validate schemas/ParsedOru parsed) (pr-str parsed))
      (is (= "123456" (:patient-id parsed)))
      (is (= "20260115090000" (:effective-time parsed)))
      (is (= "NM" (:value-type parsed)))
      (is (= {:system "http://loinc.org" :code "2345-7" :display "Glucose"}
             (:observation-identifier parsed)))
      (is (= {:system "http://loinc.org" :code "1554-5" :display "GLUCOSE"}
             (:ordered-test-identifier parsed)))
      (is (= 95 (:value parsed)))
      (is (= "mg/dL" (:units parsed)))
      (is (= "70-100" (:reference-range parsed)))
      (is (= "N" (:abnormal-flag parsed)))
      (is (= "F" (:status parsed))))))

(deftest oru->observation-test
  (testing "transforming the sample's parse yields a schema-valid Observation"
    (let [parsed (specimen/parse-oru specimen/sample-oru-message)
          obs (specimen/oru->observation parsed)]
      (is (m/validate schemas/Observation obs) (pr-str obs))
      (testing "spot-checking the six purpose-set query targets (Chapter 21)"
        (is (= 95 (get-in obs [:valueQuantity :value])))
        (is (= "mg/dL" (get-in obs [:valueQuantity :unit])))
        (is (= "N" (get-in obs [:interpretation :coding 0 :code])))
        (is (= "final" (:status obs)))
        (is (= "20260115090000" (:effectiveDateTime obs)))
        (is (= "Patient/123456" (get-in obs [:subject :reference])))))))

(deftest parse-oru-safety-critical-fields-test
  (testing "a blanked value field throws ex-info rather than defaulting"
    (let [blanked (str/replace specimen/sample-oru-message
                                "||95|mg/dL" "|||mg/dL")]
      (is (thrown? clojure.lang.ExceptionInfo
                   (specimen/parse-oru blanked)))
      (try
        (specimen/parse-oru blanked)
        (is false "expected parse-oru to throw")
        (catch clojure.lang.ExceptionInfo e
          (is (= :value (:field (ex-data e))))))))
  (testing "a blanked status field throws ex-info rather than defaulting"
    (let [blanked (str/replace specimen/sample-oru-message
                                "|||F\r" "|||\r")]
      (is (thrown? clojure.lang.ExceptionInfo
                   (specimen/parse-oru blanked)))
      (try
        (specimen/parse-oru blanked)
        (is false "expected parse-oru to throw")
        (catch clojure.lang.ExceptionInfo e
          (is (= :status (:field (ex-data e)))))))))

;; The golden layer's independent oracle for `classify-abnormal` (Chapter
;; 23): these expectations are hand-authored against the spec, not derived
;; from the production rule, so they catch a shared-rule bug (e.g.
;; `classify-abnormal` misclassifying "H") that the purpose-set properties
;; are structurally blind to -- see the oracle-circularity comment on
;; `ehr-testing.properties/purpose-set-med-rec`.
(deftest classify-abnormal-golden-test
  (testing "hand-authored expectation table, independent of classify-abnormal itself"
    (is (= :in-range (specimen/classify-abnormal "N")))
    (is (= :out-of-range (specimen/classify-abnormal "H")))
    (is (= :out-of-range (specimen/classify-abnormal "L")))))

(deftest oru->observation-abnormal-flag-h-test
  (testing "an H-flagged message transforms to an out-of-range interpretation coding"
    (let [h-message (str/replace specimen/sample-oru-message "|N|" "|H|")
          parsed (specimen/parse-oru h-message)
          obs (specimen/oru->observation parsed)]
      ;; Hand-written expectation, not run through classify-abnormal.
      (is (= "H" (get-in obs [:interpretation :coding 0 :code]))))))

;; Six purpose-set properties (Chapter 21's table), 100 trials each.

(defspec value-property-test 100
  properties/value-property)

(defspec units-property-test 100
  properties/units-property)

(defspec interpretation-classification-property-test 100
  properties/interpretation-classification-property)

(defspec status-property-test 100
  properties/status-property)

(defspec effective-time-property-test 100
  properties/effective-time-property)

(defspec subject-identity-property-test 100
  properties/subject-identity-property)
