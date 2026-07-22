(ns ehr-testing.properties
  "Lens laws and observational-correctness properties, as test.check
  properties.

  This namespace is where the book's method (Chapters 21, 22, 24, 31)
  becomes executable: correctness-relative-to-a-purpose-set as a property,
  the correctness lattice's join over two recipients' purpose sets, and
  the lens round-trip laws (GetPut/PutGet) as properties over the
  specimen's `oru->observation` / `observation->oru` pair.

  `purpose-set-med-rec` and its six properties implement Chapter 21's
  worked table (\"The specimen, worked\") for the medication-reconciliation
  clinician. `purpose-set-quality-measure` and its five properties add
  Chapter 22's second recipient, a quality-measure pipeline. `union-strictest`
  is Chapter 22's join: the two tables combined by :name, shared rows kept
  at the stricter tolerance. Every property checks that `q` composed with
  `oru->observation` agrees with `q-source`, at that row's agreement
  tolerance -- `agree?` below. These properties generate `ParsedOru` maps
  directly via `malli.generator` rather than generating wire-format v2
  strings, since the latter would require a v2 renderer this book doesn't
  need (`parse-oru` is tested separately, against the literal sample
  message). The lens-law properties remain commented out and stubbed,
  since `observation->oru` is still a stub (Chapter 31)."
  (:require [clojure.test.check.properties :as prop]
            [malli.core :as m]
            [malli.generator :as mg]
            [ehr-testing.schemas :as schemas]
            [ehr-testing.specimen :as specimen]))

(def schema-generator-round-trips
  "A generator/schema round-trip property: every value generated from the
  Observation schema validates against that same schema. This is a
  sanity check on the schema itself, not yet a property about the
  specimen's transformation -- it establishes that `malli.generator` and
  `ehr-testing.schemas/Observation` agree with each other before any
  property is built on top of generated Observations."
  (prop/for-all [obs (mg/generator schemas/Observation)]
    (m/validate schemas/Observation obs)))

(def gen-parsed-oru
  "Generator for `ehr-testing.schemas/ParsedOru`, shared by every
  purpose-set property below (both recipients' rows)."
  (mg/generator schemas/ParsedOru))

(defn agree?
  "Implements a purpose-set row's agreement tolerance (Chapter 21,
  \"Observational equivalence, operationally first\"; Chapter 22 adds two
  more rows of the same shape for the quality-measure recipient):

  - `:exact` compares the two answers directly.
  - `[:normalized f]`, `[:same-classification f]`, `[:set-membership f]`,
    and `[:within-period f]` all apply the named function `f` to both
    answers before comparing -- the same mechanical check under four
    names, because each chapter treats these as different *questions*
    (unit normalization, range classification, value-set membership,
    period membership) even though the check itself is identical."
  [tolerance q-answer q-source-answer]
  (if (vector? tolerance)
    (let [[kind f] tolerance]
      (case kind
        :normalized (= (f q-answer) (f q-source-answer))
        :same-classification (= (f q-answer) (f q-source-answer))
        :set-membership (= (f q-answer) (f q-source-answer))
        :within-period (= (f q-answer) (f q-source-answer))))
    (case tolerance
      :exact (= q-answer q-source-answer))))

;; `purpose-set-med-rec`'s :units, :interpretation-classification, and
;; :status rows call `specimen/normalize-ucum`, `specimen/classify-abnormal`,
;; and `specimen/status->fhir` on the q-source side -- the very same
;; functions `oru->observation` calls to build the q side's answer. This is
;; a deliberate, accepted oracle circularity, not an oversight: it means
;; a bug *in the shared rule itself* (say, `classify-abnormal` misclassifying
;; "H") is invisible to these properties, because both sides of the triangle
;; would be wrong the same way -- the triangle commutes by construction for
;; that rule's own (wrong) semantics. What these properties do catch is the
;; transform dropping, misrouting, or corrupting a field on its way through.
;; The independent check on the rules themselves is the golden layer: the
;; hand-written literal expectations in `ehr-testing.specimen-test` (e.g.
;; "N" -> :in-range, "F" -> "final"), authored independently of the
;; production code they check. Chapter 23's corpus design and Chapter 24's
;; property families both return to this distinction between generated and
;; curated cases.
(def purpose-set-med-rec
  "The six purpose-set rows from Chapter 21's table, each a
  `{:name :q :q-source :tolerance}` row: `q` reads the transform's output
  (an Observation), `q-source` reads the source (a ParsedOru), and
  `tolerance` is one of `agree?`'s cases. Some `q-source` functions format
  their answer to match the target's terms (e.g. prefixing the patient id
  as `Patient/<id>`, or mapping OBX-11's F/P to final/preliminary) -- that
  is the query itself, phrased so the two sides are comparable, not a
  weakening of the tolerance."
  [{:name :value
    :tolerance :exact
    :q (fn [obs] (get-in obs [:valueQuantity :value]))
    :q-source :value}
   {:name :units
    :tolerance [:normalized specimen/normalize-ucum]
    :q (fn [obs] (get-in obs [:valueQuantity :unit]))
    :q-source :units}
   {:name :interpretation-classification
    :tolerance [:same-classification specimen/classify-abnormal]
    :q (fn [obs] (get-in obs [:interpretation :coding 0 :code]))
    :q-source :abnormal-flag}
   {:name :status
    :tolerance :exact
    :q :status
    :q-source (comp specimen/status->fhir :status)}
   {:name :effective-time
    :tolerance :exact
    :q :effectiveDateTime
    :q-source :effective-time}
   {:name :subject-identity
    :tolerance :exact
    :q (fn [obs] (get-in obs [:subject :reference]))
    :q-source (fn [oru] (str "Patient/" (:patient-id oru)))}])

(defn- row-by-name
  [table row-name]
  (first (filter #(= row-name (:name %)) table)))

(defn- property-for-row
  "correct-for-q: the triangle commutes, at this row's tolerance."
  [{:keys [q q-source tolerance]}]
  (prop/for-all [oru gen-parsed-oru]
    (agree? tolerance
            (q        (specimen/oru->observation oru))
            (q-source oru))))

(def value-property
  (property-for-row (row-by-name purpose-set-med-rec :value)))

(def units-property
  (property-for-row (row-by-name purpose-set-med-rec :units)))

(def interpretation-classification-property
  (property-for-row (row-by-name purpose-set-med-rec :interpretation-classification)))

(def status-property
  (property-for-row (row-by-name purpose-set-med-rec :status)))

(def effective-time-property
  (property-for-row (row-by-name purpose-set-med-rec :effective-time)))

(def subject-identity-property
  (property-for-row (row-by-name purpose-set-med-rec :subject-identity)))

;; --- Chapter 22, recipient B: the quality-measure pipeline ----------------

(def quality-measure-value-set
  "The diabetes-control measure's value set: LOINC codes counted toward
  the denominator. Deliberately excludes 1554-5 (fasting glucose, the
  sample's *ordered* test, OBR-4) -- the order/result code divergence
  between OBR-4 and OBX-3 is Chapter 23's mutation-layer material, not
  this chapter's; see `ehr-testing.corpus` for the mutating function that
  exercises it."
  #{"2345-7"})

(def quality-measure-threshold
  "The measure's own numeric threshold, independent of the clinician's
  reference range (OBX-7) -- Chapter 22's point that the measure brings
  its own thresholds rather than reading the source's."
  130)

(def quality-measurement-period
  "The measure's reporting period: an inclusive [start end] pair of
  v2-style timestamp strings bracketing the sample's OBR-7
  (20260115090000)."
  ["20260101000000" "20260131235959"])

(defn- in-period?
  "Whether a v2-style timestamp string falls within `period`'s inclusive
  [start end] bounds. Lexicographic string comparison suffices here
  because these timestamps are fixed-width, all-numeric, and zero-padded
  (YYYYMMDDHHMMSS) -- lexicographic order and chronological order
  coincide for that shape. This is an assumption about well-formed v2
  timestamps, not something the ParsedOru schema enforces (:effective-time
  is a bare :string, Chapter 21); a differently-shaped timestamp would
  need a real parse, not string comparison."
  [[start end] timestamp]
  (and (>= (compare timestamp start) 0)
       (<= (compare timestamp end) 0)))

(def purpose-set-quality-measure
  "Chapter 22, recipient B: denominator inclusion for a glucose measure.
  :subject-identity and :status are the *same* rows as
  `purpose-set-med-rec` (reused, not redefined) -- both recipients ask
  exactly the same two questions of the data, which is what lets
  `union-strictest` treat them as one shared row rather than two."
  [{:name :code-in-value-set
    :tolerance [:set-membership (fn [code] (contains? quality-measure-value-set code))]
    :q (fn [obs] (get-in obs [:code :coding 0 :code]))
    :q-source (fn [oru] (:code (:observation-identifier oru)))}
   {:name :value-vs-threshold
    :tolerance [:same-classification (fn [value] (<= value quality-measure-threshold))]
    :q (fn [obs] (get-in obs [:valueQuantity :value]))
    :q-source :value}
   (row-by-name purpose-set-med-rec :subject-identity)
   {:name :in-measurement-period
    :tolerance [:within-period (fn [ts] (in-period? quality-measurement-period ts))]
    :q :effectiveDateTime
    :q-source :effective-time}
   (row-by-name purpose-set-med-rec :status)])

(def code-in-value-set-property
  (property-for-row (row-by-name purpose-set-quality-measure :code-in-value-set)))

(def value-vs-threshold-property
  (property-for-row (row-by-name purpose-set-quality-measure :value-vs-threshold)))

(def quality-measure-subject-identity-property
  (property-for-row (row-by-name purpose-set-quality-measure :subject-identity)))

(def in-measurement-period-property
  (property-for-row (row-by-name purpose-set-quality-measure :in-measurement-period)))

(def quality-measure-status-property
  (property-for-row (row-by-name purpose-set-quality-measure :status)))

;; --- Chapter 22: the join ---------------------------------------------------

(def ^:private tolerance-strictness
  "Chapter 22's 'stricter of each pair' ordering for `union-strictest`:
  lower rank is stricter. Ranked by how much slack the tolerance allows,
  not by which chapter introduced it -- :exact permits no difference;
  :normalized permits only a named, format-level rewrite; the
  classification-style tolerances (:same-classification, :set-membership,
  :within-period) permit any difference that lands in the same bucket,
  which is strictly more slack than a format-level rewrite."
  {:exact 0
   :normalized 1
   :same-classification 2
   :set-membership 2
   :within-period 2})

(defn- tolerance-rank
  [tolerance]
  (tolerance-strictness (if (vector? tolerance) (first tolerance) tolerance)))

(defn- stricter-tolerance
  "The stricter of two tolerances on the same named row. A tie (equal
  rank -- in this book, always two :exact tolerances on a row reused
  verbatim by both tables) keeps `a`; when both sides already agree on
  strictness, which literal tolerance value survives doesn't change what
  gets checked."
  [a b]
  (if (<= (tolerance-rank a) (tolerance-rank b)) a b))

(defn union-strictest
  "Chapter 22's join: the union of two purpose-set tables, keyed by
  :name. A row present in only one table passes through unchanged; a row
  present in both keeps table-a's q/q-source with whichever tolerance is
  stricter, via `stricter-tolerance` (for this book's two tables, the
  only shared rows, :subject-identity and :status, are literally the
  same row value in both tables already, so there's nothing to reconcile
  beyond confirming the tie)."
  [table-a table-b]
  (let [by-name-b (into {} (map (juxt :name identity)) table-b)
        names-a (into #{} (map :name) table-a)
        joined-a (mapv (fn [row-a]
                          (if-let [row-b (get by-name-b (:name row-a))]
                            (update row-a :tolerance stricter-tolerance (:tolerance row-b))
                            row-a))
                        table-a)
        only-b (remove #(contains? names-a (:name %)) table-b)]
    (into joined-a only-b)))

(def combined-suite
  "The join: serve both recipients; shared rows keep the stricter
  tolerance (Chapter 22, \"The lattice, operationally first\")."
  (union-strictest purpose-set-med-rec purpose-set-quality-measure))

(comment
  ;; --- Lens laws (Chapter 31), stubbed ---
  ;;
  ;; GetPut: putting back what you just got changes nothing.
  ;;   (= (observation->oru (oru->observation parsed) parsed) parsed)
  ;;
  ;; PutGet: getting what you just put shows you what you put.
  ;;   (= (oru->observation (observation->oru obs parsed)) obs)
  ;;
  ;; Both depend on ehr-testing.specimen/observation->oru being
  ;; implemented (it is still a stub -- oru->observation is done, above).
  ;; Once it is, these become:

  (def get-put-holds
    (prop/for-all [parsed gen-parsed-oru]
      (= (ehr-testing.specimen/observation->oru
          (ehr-testing.specimen/oru->observation parsed)
          parsed)
         parsed)))

  (def put-get-holds
    (prop/for-all [obs (mg/generator schemas/Observation)
                    parsed gen-parsed-oru]
      (= (ehr-testing.specimen/oru->observation
          (ehr-testing.specimen/observation->oru obs parsed))
         obs))))
