(ns ehr-testing.smoke-test
  "A smoke test: confirms the three core namespaces load, the Observation
  schema validates a hand-written example, and the generator-round-trip
  property holds over a small number of trials. Meant to fail loudly and
  immediately if the project is broken, not to exercise the specimen's
  transformation logic (only `observation->oru` remains stubbed -- see
  ehr-testing.specimen)."
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.test.check :as tc]
            [malli.core :as m]
            [ehr-testing.specimen :as specimen]
            [ehr-testing.schemas :as schemas]
            [ehr-testing.properties :as properties]))

(deftest namespaces-load-test
  (testing "the three core namespaces load and expose what's expected"
    (is (string? specimen/sample-oru-message))
    (is (some? schemas/Observation))
    (is (some? properties/schema-generator-round-trips))))

(deftest observation-schema-validates-test
  (testing "a hand-written Observation map validates against the schema"
    (is (m/validate schemas/Observation
                     {:resourceType "Observation"
                      :status "final"
                      :code {:coding [{:system "http://loinc.org"
                                        :code "2345-7"
                                        :display "Glucose"}]}
                      :subject {:reference "Patient/123456"}
                      :effectiveDateTime "20260115090000"
                      :valueQuantity {:value 95
                                       :unit "mg/dL"
                                       :system "http://unitsofmeasure.org"
                                       :code "mg/dL"}
                      :interpretation {:coding [{:system "http://terminology.hl7.org/CodeSystem/v2-0078"
                                                  :code "N"}]}}))))

(deftest generator-round-trip-property-test
  (testing "generated Observations validate against their own schema"
    (let [result (tc/quick-check 20 properties/schema-generator-round-trips)]
      (is (:pass? result) (pr-str result)))))
