(ns ehr-testing.schemas
  "Malli schemas for the shapes used by the book's specimen (see
  `ehr-testing.specimen`): a minimal FHIR R4 Observation, and a minimal
  intermediate representation of a parsed HL7 v2 ORU message.

  These are deliberately narrow -- only the fields the specimen's worked
  examples touch -- not full FHIR/v2 schemas. Chapters add fields as their
  worked examples need them.")

(def Coding
  "A single coding entry: a code from some code system (e.g. LOINC),
  optionally with a human-readable display string."
  [:map
   [:system :string]
   [:code :string]
   [:display {:optional true} :string]])

(def Observation
  "A minimal FHIR R4 Observation: just enough to carry the specimen's
  ORU-derived numeric lab result and the six purpose-set query targets
  from Chapter 21 (value, unit, interpretation, status, effective time,
  subject)."
  [:map
   [:resourceType [:= "Observation"]]
   [:status [:enum "registered" "preliminary" "final" "amended" "corrected"
             "cancelled" "entered-in-error" "unknown"]]
   [:code [:map
           [:coding [:vector Coding]]]]
   [:subject [:map
              [:reference :string]]]
   [:effectiveDateTime :string]
   [:valueQuantity [:map
                     [:value number?]
                     [:unit :string]
                     [:system :string]
                     [:code :string]]]
   [:interpretation [:map
                      [:coding [:vector Coding]]]]])

(def ParsedOru
  "A minimal intermediate representation of a parsed ORU^R01 message,
  carrying just the fields the specimen's transformation needs -- one
  patient (PID-3), one order (OBR-7), one observation (OBX), matching the
  happy-path shape `ehr-testing.specimen/parse-oru` assumes."
  [:map
   [:message-type :string]
   [:patient-id :string]
   [:effective-time :string]
   ;; OBX-2: fixed to NM for this chapter's specimen. OBX-2 turning OBX-5
   ;; into a runtime-typed dependent sum (NM/ST/CWE/...) is Chapter 33's
   ;; business, not this chapter's.
   [:value-type [:= "NM"]]
   [:observation-identifier Coding]
   ;; OBR-4: the ordered test, as distinct from OBX-3's resulted test
   ;; (:observation-identifier) -- the order/result divergence Chapter 23's
   ;; mutation layer exercises.
   [:ordered-test-identifier Coding]
   [:value number?]
   [:units :string]
   [:reference-range :string]
   [:abnormal-flag [:enum "N" "H" "L"]]
   [:status [:enum "F" "P"]]])
