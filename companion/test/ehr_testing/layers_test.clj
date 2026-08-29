(ns ehr-testing.layers-test
  "Chapter 35's listing: the two-layer soundness law `lower ⨟ erase = id`
  as an executable property, the fiber over one design, and a lowering
  that leaves the fiber so the law can be seen to fail."
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [ehr-testing.layers :as layers]))

;; --- The chapter's worked design -----------------------------------------

(def design
  "Figure A's conceptual plane: a into f, which also reads k, giving b;
  b into g, giving c."
  {:boxes [(layers/box :f [:a] [:k] [:b])
           (layers/box :g [:b] [:c])]})

(def variant-1
  "Figure A, variant 1: files and processes, nothing inserted."
  {:stores {:a :file :b :file :c :file :k :config-file}
   :runtimes {:f :process :g :process}
   :persist-after #{}})

(def variant-2
  "Figure A, variant 2: a table in, a queue between, a persist box
  spliced in after f."
  {:stores {:a :table :b :queue :c :table :k :config-file}
   :runtimes {:f :job :g :worker}
   :persist-after #{:f}})

(deftest the-worked-design-is-sound-on-both-variants
  (is (layers/sound? design variant-1))
  (is (layers/sound? design variant-2)))

(deftest the-two-variants-differ-but-erase-to-one-design
  (testing "lowering genuinely adds something: the implementations differ"
    (is (not= (layers/lower design variant-1)
              (layers/lower design variant-2))))
  (testing "variant 2 carries a box the design never mentioned"
    (is (= [:f :persist-after-f :g]
           (layers/box-names (layers/lower design variant-2)))))
  (testing "and erasure forgets it, landing on the same design both times"
    (is (= design
           (layers/erase (layers/lower design variant-1))
           (layers/erase (layers/lower design variant-2))))))

;; --- Generators ---------------------------------------------------------

(def gen-kind
  (gen/elements [:a :b :c :d :e :k :m]))

(def gen-diagram
  "A chain of one to four boxes, each consuming what the previous one
  produced, with up to two catalytic side inputs each."
  (gen/let [n     (gen/choose 1 4)
            kinds (gen/vector gen-kind (inc n))
            cats  (gen/vector (gen/vector gen-kind 0 2) n)]
    {:boxes (mapv (fn [i]
                    (layers/box (keyword (str "op" i))
                                [(nth kinds i)]
                                (nth cats i)
                                [(nth kinds (inc i))]))
                  (range n))}))

(def gen-store   (gen/elements [:file :table :queue :stream :cache]))
(def gen-runtime (gen/elements [:process :job :worker :service]))

(defn gen-substrate-for
  "A substrate that places every kind the diagram mentions and gives
  every box a runtime; any subset of boxes gets a persist box after it."
  [diagram]
  (let [ks (vec (layers/kinds diagram))
        bs (layers/box-names diagram)]
    (gen/let [stores   (gen/vector gen-store (count ks))
              runtimes (gen/vector gen-runtime (count bs))
              persist  (gen/vector gen/boolean (count bs))]
      {:stores        (zipmap ks stores)
       :runtimes      (zipmap bs runtimes)
       :persist-after (set (map first (filter second (map vector bs persist))))})))

(def gen-diagram-and-substrate
  (gen/let [d gen-diagram
            s (gen-substrate-for d)]
    [d s]))

(def gen-diagram-and-two-substrates
  (gen/let [d  gen-diagram
            s1 (gen-substrate-for d)
            s2 (gen-substrate-for d)]
    [d s1 s2]))

;; --- The law, as a property ----------------------------------------------

(def lower-then-erase-is-identity
  "For every conceptual diagram and every substrate: lowering and then
  erasing is the identity. This is the chapter's soundness law, checked
  pointwise over generated inputs -- a witness, not a proof."
  (prop/for-all [[d s] gen-diagram-and-substrate]
    (and (layers/conceptual? d)
         (layers/sound? d s))))

(deftest law-holds-for-every-generated-substrate
  (let [result (tc/quick-check 300 lower-then-erase-is-identity)]
    (is (true? (:pass? result)) (pr-str result))))

;; --- The fiber ---------------------------------------------------------

(def two-lowerings-erase-to-one-design
  "The fiber over a design: any two substrates give implementations
  that erase to the same design. Where the substrates differ the
  implementations differ too; the design does not move."
  (prop/for-all [[d s1 s2] gen-diagram-and-two-substrates]
    (let [i1 (layers/lower d s1)
          i2 (layers/lower d s2)]
      (and (= (layers/erase i1) (layers/erase i2) d)
           (or (= s1 s2) (not= i1 i2))))))

(deftest fiber-collapses-to-one-design
  (let [result (tc/quick-check 300 two-lowerings-erase-to-one-design)]
    (is (true? (:pass? result)) (pr-str result))))

;; --- The law failing, visibly ---------------------------------------------

(deftest a-lowering-that-drops-a-catalytic-input-does-not-erase-back
  (testing "the wrong lowering produces a runnable-looking implementation"
    (is (= [:f :persist-after-f :g]
           (layers/box-names (layers/lower-dropping-catalytic design variant-2)))))
  (testing "but it left the fiber: erasure lands somewhere else"
    (let [erased (layers/erase (layers/lower-dropping-catalytic design variant-2))]
      (is (not= design erased))
      (is (= [] (:catalytic (first (:boxes erased)))))
      (is (= [:k] (:catalytic (first (:boxes design))))))))

(def the-wrong-lowering-fails-exactly-where-k-was-read
  "The wrong lowering is caught by the law precisely on the diagrams
  that read a catalytic input somewhere, and passes on the rest -- the
  witness rejects what it should and nothing else."
  (prop/for-all [[d s] gen-diagram-and-substrate]
    (let [reads-anything? (some (comp seq :catalytic) (:boxes d))
          erased (layers/erase (layers/lower-dropping-catalytic d s))]
      (= (boolean reads-anything?) (not= d erased)))))

(deftest the-wrong-lowering-is-rejected-iff-something-was-read
  (let [result (tc/quick-check 300 the-wrong-lowering-fails-exactly-where-k-was-read)]
    (is (true? (:pass? result)) (pr-str result))))
