(ns ehr-testing.specimen
  "The book's recurring specimen.

  Every part of the book returns to the same worked example: an HL7 v2
  ORU^R01 result message (one numeric lab result, LOINC-coded, with UCUM
  units) being transformed into a FHIR R4 Observation, and back.

  `sample-oru-message` is the literal message text referenced throughout
  the manuscript (Chapters 21-22, 23-24, 31, 33, 34 -- see
  AUTHORS-GUIDE.md section 5 for the full list of chapters that must be
  checked if this specimen changes).

  `parse-oru`, `oru->observation`, and `observation->oru` are the get/put
  pair discussed as a lens in Chapter 31. `parse-oru` and `oru->observation`
  are implemented here, happy-path only, as Chapter 21 requires (ADR-0007:
  no pseudocode). `observation->oru` remains a stub -- it is Chapter 31's
  material."
  (:require [clojure.string :as str]
            [clojure.edn :as edn]))

(def sample-oru-message
  "A minimal, realistic ORU^R01 message: one MSH, one PID, one OBR, and one
  OBX carrying a numeric (NM) glucose result. OBX-3 carries a LOINC code
  (2345-7, Glucose [Mass/volume] in Serum or Plasma); OBX-6 carries a
  UCUM unit (mg/dL). Entirely synthetic: no real patient data or
  production message content."
  (str "MSH|^~\\&|LAB|HOSPITAL|EHR|CLINIC|20260115103000||ORU^R01|MSG00001|P|2.4\r"
       "PID|1||123456^^^HOSPITAL^MR||DOE^JANE||19800101|F\r"
       "OBR|1|ORD001|RES001|1554-5^GLUCOSE^LN|||20260115090000\r"
       "OBX|1|NM|2345-7^Glucose^LN||95|mg/dL|70-100|N|||F\r"))

;; --- v2 segment/field/component splitting --------------------------------

(defn- quote-re
  "A regex matching `s` literally -- field/component separators (|, ^, &)
  are all regex metacharacters."
  [s]
  (re-pattern (java.util.regex.Pattern/quote s)))

(defn- split-on
  "Split `s` on the literal separator `sep`, keeping trailing empty fields
  (a v2 line ending in empty fields, e.g. `...|||`, is meaningful -- those
  are real, present-but-blank fields, not fields to be dropped)."
  [s sep]
  (str/split s (quote-re sep) -1))

(defn- encoding-chars
  "Read the field and component separators from MSH-1/MSH-2. MSH is
  special in v2: the character right after the segment ID *is* the field
  separator (MSH-1), so it's read positionally rather than by splitting;
  MSH-2's first character is the component separator. The remaining
  encoding characters (repetition, escape, subcomponent) aren't needed for
  this specimen's single, non-repeating OBX and aren't extracted."
  [msh-line]
  {:field-sep (str (nth msh-line 3))
   :component-sep (str (nth msh-line 4))})

(defn- find-segment
  [lines segment-id]
  (first (filter #(str/starts-with? % segment-id) lines)))

;; --- safety-critical field guard ------------------------------------------

(defn- required-field
  "Return `value` unless it's blank, in which case throw: a blank
  safety-critical field (value, units, status, patient id) must never be
  silently defaulted -- Chapter 21's specimen obligation."
  [value field-key]
  (if (str/blank? value)
    (throw (ex-info (str "ORU parse failure: missing safety-critical field "
                          field-key)
                     {:ehr-testing/error :missing-safety-critical-field
                      :field field-key}))
    value))

(def ^:private coding-system-uris
  "v2 coding-system abbreviations this specimen's OBX-3 uses, mapped to
  their canonical URI. Not a general Table 0396 mapping -- only LN (LOINC)
  is needed here."
  {"LN" "http://loinc.org"})

(defn- coding-system-uri
  [abbreviation]
  (get coding-system-uris abbreviation abbreviation))

(defn parse-oru
  "Parse an HL7 v2 ORU message string into the intermediate `ParsedOru`
  shape defined in `ehr-testing.schemas`.

  Happy-path only (ADR-0007's specimen, not a general v2 parser): assumes
  the sample's shape -- one MSH, one PID, one OBR, one OBX of type NM.
  Never silently defaults a safety-critical field (value, units, status,
  patient id); throws `ex-info` instead."
  [message-text]
  (let [lines (remove str/blank? (str/split message-text #"\r"))
        msh-line (find-segment lines "MSH")
        {:keys [field-sep component-sep]} (encoding-chars msh-line)
        fields (fn [line] (split-on line field-sep))
        components (fn [field] (split-on field component-sep))
        msh (fields msh-line)
        pid (fields (find-segment lines "PID"))
        obr (fields (find-segment lines "OBR"))
        obx (fields (find-segment lines "OBX"))
        ;; MSH-1 (the separator itself) isn't a split token, so MSH field N
        ;; (N >= 2) is at index (dec N); every other segment's field N is
        ;; at index N (index 0 is the segment ID).
        msh-field (fn [n] (nth msh (dec n) ""))
        pid-field (fn [n] (nth pid n ""))
        obr-field (fn [n] (nth obr n ""))
        obx-field (fn [n] (nth obx n ""))
        pid-3 (components (pid-field 3))
        obx-3 (components (obx-field 3))
        obr-4 (components (obr-field 4))
        patient-id (required-field (first pid-3) :patient-id)
        units (required-field (obx-field 6) :units)
        status (required-field (obx-field 11) :status)
        value-str (required-field (obx-field 5) :value)
        display (nth obx-3 1 "")
        ordered-display (nth obr-4 1 "")]
    {:message-type (msh-field 9)
     :patient-id patient-id
     :effective-time (obr-field 7)
     :value-type (obx-field 2)
     :observation-identifier
     (cond-> {:system (coding-system-uri (nth obx-3 2 ""))
              :code (nth obx-3 0 "")}
       (not (str/blank? display)) (assoc :display display))
     :ordered-test-identifier
     (cond-> {:system (coding-system-uri (nth obr-4 2 ""))
              :code (nth obr-4 0 "")}
       (not (str/blank? ordered-display)) (assoc :display ordered-display))
     :value (edn/read-string value-str)
     :units units
     :reference-range (obx-field 7)
     :abnormal-flag (obx-field 8)
     :status status}))

;; --- ORU -> Observation (Chapter 21's transform) --------------------------

(defn normalize-ucum
  "Normalize a v2 units string to its UCUM form. Identity for now -- every
  unit this specimen carries (mg/dL) is already valid UCUM. Real
  normalization (case folding, synonym tables such as mcg -> ug, resolving
  ambiguous units) would live here once the corpus (Chapter 23) exercises
  non-UCUM inputs. `ehr-testing.properties` applies this same function to
  both sides of the units purpose-set row, per its \"normalized\" tolerance."
  [units]
  units)

(defn classify-abnormal
  "Classify an OBX-8 abnormal-flag value as in-range or out-of-range, for
  the interpretation purpose-set row (Chapter 21). N is normal/in-range; H
  and L (high/low) are out-of-range."
  [flag]
  (if (= flag "N") :in-range :out-of-range))

(def status->fhir
  "OBX-11 result status to FHIR Observation.status, for the values this
  chapter's specimen carries. Other v2 result statuses (C corrected, X
  cancelled, ...) are out of scope for this happy path."
  {"F" "final"
   "P" "preliminary"})

(defn oru->observation
  "The `get` half of the specimen's lens (Chapter 31): given a parsed ORU
  (see `parse-oru`), produce a FHIR R4 Observation matching
  `ehr-testing.schemas/Observation`, preserving what Chapter 21's six
  purpose-set queries read."
  [parsed-oru]
  (let [{:keys [patient-id effective-time observation-identifier value units
                abnormal-flag status]} parsed-oru]
    {:resourceType "Observation"
     :status (status->fhir status)
     :code {:coding [observation-identifier]}
     :subject {:reference (str "Patient/" patient-id)}
     :effectiveDateTime effective-time
     :valueQuantity {:value value
                     :unit (normalize-ucum units)
                     :system "http://unitsofmeasure.org"
                     :code (normalize-ucum units)}
     :interpretation {:coding [{:system "http://terminology.hl7.org/CodeSystem/v2-0078"
                                :code abnormal-flag}]}}))

(defn observation->oru
  "The `put` half of the specimen's lens (Chapter 31): given a FHIR R4
  Observation and the original parsed ORU it was derived from, produce an
  updated `ParsedOru`.

  TODO: Chapter 31's material, not this chapter's obligation. Returns nil
  until then."
  [_observation _original-parsed-oru]
  nil)
