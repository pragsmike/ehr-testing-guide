(ns ehr-testing.properties-test
  "Tests for Chapter 22's second recipient (the quality-measure pipeline)
  and the join over both recipients' purpose sets: the five
  `purpose-set-quality-measure` properties as test.check defspecs, and
  `union-strictest`/`combined-suite` as plain deftests. Chapter 21's
  medication-reconciliation properties are tested in `specimen-test`."
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [ehr-testing.properties :as properties]
            [ehr-testing.specimen :as specimen]))

;; Five purpose-set-quality-measure properties (Chapter 22's table), 100
;; trials each -- same pattern as Chapter 21's six.

(defspec code-in-value-set-property-test 100
  properties/code-in-value-set-property)

(defspec value-vs-threshold-property-test 100
  properties/value-vs-threshold-property)

(defspec quality-measure-subject-identity-property-test 100
  properties/quality-measure-subject-identity-property)

(defspec in-measurement-period-property-test 100
  properties/in-measurement-period-property)

(defspec quality-measure-status-property-test 100
  properties/quality-measure-status-property)

(deftest combined-suite-test
  (testing "the join has one row per distinct name: 6 + 5 - 2 shared"
    (is (= 9 (count properties/combined-suite))))
  (testing "every row's :name is unique"
    (let [names (map :name properties/combined-suite)]
      (is (= (count names) (count (distinct names))) (pr-str names))))
  (testing "the two shared rows (:subject-identity, :status) keep the
            stricter tolerance -- here, :exact on both sides, so :exact
            survives"
    (let [by-name (fn [n] (first (filter #(= n (:name %)) properties/combined-suite)))]
      (is (= :exact (:tolerance (by-name :subject-identity))))
      (is (= :exact (:tolerance (by-name :status))))))
  (testing "rows unique to either table pass through unchanged"
    (let [by-name (fn [n] (first (filter #(= n (:name %)) properties/combined-suite)))]
      (is (= [:normalized specimen/normalize-ucum] (:tolerance (by-name :units))))
      (is (= :set-membership (first (:tolerance (by-name :code-in-value-set)))))
      (is (= :same-classification (first (:tolerance (by-name :value-vs-threshold))))))))
