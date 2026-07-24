(ns ehr-testing.conceptmap
  "A ConceptMap-shaped translation relation, and the honest check over it
  (Chapter 34; the ConceptMap reference entry is in Chapter 42).

  The data shape mirrors the parts of FHIR R4's ConceptMap that carry the
  relation itself: a source system, a target system, and rows pairing a
  source code with a target code *and an equivalence annotation*. R4 puts
  the annotation at `ConceptMap.group.element.target.equivalence`, drawn
  from the ConceptMapEquivalence value set; R5 renames the element to
  `relationship` and shrinks the code list. This namespace keeps the R4
  vocabulary because the book's specimen targets R4, and keeps only the
  fields the check needs -- it is a faithful subset, not a full FHIR
  resource implementation.

  The point the chapter makes runnable here: a ConceptMap is a *relation
  carrying honesty annotations*, not a function, and a translation test
  must read those annotations. `check-translation` below only asserts
  code equality where the map itself claims `:equivalent` or `:equal`;
  every other annotation yields `:flagged`, never a silent pass or a
  silent equality assertion. The dishonest alternative -- assert equality
  on every row regardless of annotation -- is written out in the test
  namespace so its failure mode is visible, not just described."
  (:require [clojure.set :as set]))

;; --- The R4 equivalence vocabulary ---------------------------------------

(def equivalence-codes
  "The ten codes of R4's ConceptMapEquivalence value set, as keywords.
  Note the reading direction R4 specifies: the annotation describes the
  *target* relative to the *source* -- `:wider` means the target concept
  is wider in meaning than the source concept. (R5 replaces this set with
  the five-code ConceptMapRelationship: related-to, not-related-to,
  equivalent, source-is-narrower-than-target,
  source-is-broader-than-target -- the direction moved into the code
  names themselves, which is arguably the same honesty lesson learned
  once more.)"
  #{:relatedto :equivalent :equal :wider :subsumes
    :narrower :specializes :inexact :unmatched :disjoint})

(def equality-safe
  "The only equivalence annotations under which asserting plain code
  equality is faithful to what the map claims. Everything else -- wider,
  narrower, inexact, and the rest -- is the map saying \"these are related
  but not interchangeable,\" and an equality assertion there is the test
  claiming more than its oracle does."
  #{:equivalent :equal})

;; --- The map itself, as data ----------------------------------------------

(defn valid-map?
  "Structural sanity for a concept map: every row names source and target
  codes and carries a recognized equivalence annotation. (Malli schemas are
  the book's usual tool for this; a predicate keeps this namespace free of
  any dependency beyond Clojure itself, and Chapter 34 shows the rows, not
  the schema.)"
  [{:keys [source-system target-system mappings]}]
  (and (string? source-system)
       (string? target-system)
       (seq mappings)
       (every? (fn [{:keys [source target equivalence]}]
                 (and (string? source)
                      (string? target)
                      (contains? equivalence-codes equivalence)))
               mappings)))

(defn translate
  "Look a source code up in the map and return every row for it -- the
  relation, honestly: possibly empty (the code is unmapped), possibly
  several rows (n-to-m is the generic case, Chapter 34). Returning a
  collection rather than a single code IS the point: a function-shaped
  `(translate m code) => target` signature would silently pick a
  representative, which is exactly the unforced choice a sectionless span
  refuses to make for you."
  [concept-map source-code]
  (filterv #(= source-code (:source %)) (:mappings concept-map)))

(defn check-translation
  "The honest translation check: given a concept map and one observed
  translation (a source code and the target code some transform actually
  produced), return a verdict map with a `:verdict` of

    :asserted-equal  -- the map claims :equivalent/:equal for this pair,
                        and the produced code matches a claimed target;
    :wrong           -- the map claims :equivalent/:equal for this source
                        code, and the produced code matches none of the
                        equality-safe targets;
    :flagged         -- the map relates this pair only under a
                        non-equality annotation (:wider, :narrower,
                        :inexact, ...): the check refuses to assert
                        equality and reports the annotations instead,
                        for a human (or a stricter, purpose-set-specific
                        rule) to judge;
    :unmapped        -- the map has no row for this source code at all.

  `:flagged` is deliberate kin to the gate verdict vocabulary Chapter 25
  uses (`:indeterminate` there): a check that cannot honestly decide says
  so, rather than rounding to pass or fail. The failure mode this
  prevents is C26's: a test asserting plain equality through an :inexact
  row passes when it should not have been asserting anything -- the map
  said the honest thing and the check wasn't listening."
  [concept-map source-code produced-target]
  (let [rows       (translate concept-map source-code)
        safe-rows  (filter #(contains? equality-safe (:equivalence %)) rows)
        safe-codes (into #{} (map :target) safe-rows)]
    (cond
      (empty? rows)
      {:verdict :unmapped :source source-code :produced produced-target}

      (seq safe-rows)
      (if (contains? safe-codes produced-target)
        {:verdict :asserted-equal :source source-code
         :produced produced-target}
        {:verdict :wrong :source source-code :produced produced-target
         :expected-one-of safe-codes})

      :else
      {:verdict :flagged :source source-code :produced produced-target
       :annotations (into #{} (map :equivalence) rows)
       :candidates (into #{} (map :target) rows)})))

(defn asserts-equality?
  "True when the check actually asserted equality for this verdict --
  the property the test namespace runs: `check-translation` asserts
  equality on a row if and only if the map annotated that row
  equality-safe."
  [{:keys [verdict]}]
  (contains? #{:asserted-equal :wrong} verdict))
