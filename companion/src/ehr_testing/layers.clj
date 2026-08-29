(ns ehr-testing.layers
  "Chapter 35's toy two-layer model: a process design as a typed diagram
  at the conceptual layer, a lowering of it onto an implementation
  substrate, and the erasure that forgets the substrate again.

  A *conceptual diagram* is a vector of boxes, read left to right. Each
  box names the kinds of artifact it consumes (`:in`), the kinds it reads
  without consuming (`:catalytic`), and the kinds it produces (`:out`).
  Nothing here has a cost or a location: the wires carry kinds, not
  files.

    {:boxes [{:name :f :in [:a] :catalytic [:k] :out [:b]}
             {:name :g :in [:b] :catalytic []   :out [:c]}]}

  A *substrate* is a choice of how to run it: which store each kind
  lives in (`:stores`), which runtime each box gets (`:runtimes`), and
  after which boxes an infrastructure box -- persist, hash, retry -- is
  inserted (`:persist-after`). `lower` applies that choice, producing an
  *implementation diagram*: the same boxes, now decorated, with the
  infrastructure boxes spliced in and marked `:infra`. `erase` strips
  the decorations and drops the infrastructure boxes.

  The law the chapter rests on is `lower ⨟ erase = id`: lowering a
  design onto any substrate and erasing again returns the design you
  started from. `sound?` is that law as a single boolean witness, and
  the companion test checks it as a generative property over random
  diagrams and random substrates. It also checks the fiber: two
  different substrates give two different implementations that erase
  to one design. And it shows the law *failing* for a lowering that
  quietly drops a catalytic input -- the implementation that hard-codes
  what the design said it would read -- because a witness is only worth
  having if the reader can see what it rejects.

  Kept deliberately small. There is no interpreter for box interiors
  here, on purpose: the chapter's eighth section is explicit that the
  diagram says what a box consumes and produces, never that it is
  correct inside.")

;; --- The conceptual layer -----------------------------------------------

(defn box
  "A conceptual box. `catalytic` defaults to none."
  ([name in out] (box name in [] out))
  ([name in catalytic out]
   {:name name :in (vec in) :catalytic (vec catalytic) :out (vec out)}))

(defn kinds
  "Every kind of artifact a conceptual diagram mentions, on any wire."
  [diagram]
  (into #{} (mapcat (fn [{:keys [in catalytic out]}]
                      (concat in catalytic out)))
        (:boxes diagram)))

(defn box-names [diagram]
  (mapv :name (:boxes diagram)))

(defn conceptual?
  "Structural check that a value is a conceptual diagram: boxes with
  names, keyword kinds on every wire, and nothing an implementation
  would add. Used by the test to state what `erase` must land in."
  [diagram]
  (and (map? diagram)
       (vector? (:boxes diagram))
       (every? (fn [b]
                 (and (keyword? (:name b))
                      (every? keyword? (:in b))
                      (every? keyword? (:catalytic b))
                      (every? keyword? (:out b))
                      (not (contains? b :runtime))
                      (not (contains? b :infra))))
               (:boxes diagram))))

;; --- Lowering ------------------------------------------------------------

(defn- place
  "Decorate one wire's kind with the store the substrate assigns it."
  [stores kind]
  {:kind kind :store (get stores kind :unplaced)})

(defn- lower-box
  [{:keys [stores runtimes]} {:keys [name in catalytic out] :as b}]
  (assoc b
         :in        (mapv #(place stores %) in)
         :catalytic (mapv #(place stores %) catalytic)
         :out       (mapv #(place stores %) out)
         :runtime   (get runtimes name :default)))

(defn- persist-box
  "The infrastructure box lowering inserts after a box: it takes the
  box's outputs and hands them on unchanged, having persisted, hashed,
  or retried them. It exists only at the implementation layer."
  [{:keys [stores]} {box-name :name out :out}]
  (let [wires (mapv #(place stores %) out)]
    {:name (keyword (str "persist-after-" (name box-name)))
     :infra true
     :in wires :catalytic [] :out wires
     :runtime :infrastructure}))

(defn lower
  "Lower a conceptual diagram onto a substrate. Adds a store to every
  wire, a runtime to every box, and splices an infrastructure box after
  each box named in the substrate's `:persist-after`."
  [diagram substrate]
  (let [persist-after (set (:persist-after substrate))]
    {:boxes (into []
                  (mapcat (fn [b]
                            (cond-> [(lower-box substrate b)]
                              (persist-after (:name b))
                              (conj (persist-box substrate b)))))
                  (:boxes diagram))}))

;; --- Erasure --------------------------------------------------------------

(defn- erase-box [b]
  (-> b
      (update :in        #(mapv :kind %))
      (update :catalytic #(mapv :kind %))
      (update :out       #(mapv :kind %))
      (dissoc :runtime)))

(defn erase
  "Forget the substrate: drop infrastructure boxes and strip stores and
  runtimes, leaving only the kinds on the wires."
  [implementation]
  {:boxes (into []
                (comp (remove :infra) (map erase-box))
                (:boxes implementation))})

;; --- The witness ----------------------------------------------------------

(defn sound?
  "The law `lower ⨟ erase = id`, as a boolean: lowering `diagram` onto
  `substrate` and erasing again gives `diagram` back. One design, one
  substrate, one check -- pointwise, which is all a witness ever is."
  [diagram substrate]
  (= diagram (erase (lower diagram substrate))))

;; --- A lowering that leaves the fiber -------------------------------------

(defn lower-dropping-catalytic
  "A deliberately wrong lowering, kept executable so the law's failure
  is visible: the same as `lower`, except that every catalytic input is
  hard-coded into the box instead of being read from a wire. The
  implementation runs fine. It just no longer erases to the design that
  promised to read its configuration."
  [diagram substrate]
  (update (lower diagram substrate) :boxes
          (fn [boxes] (mapv #(assoc % :catalytic []) boxes))))
