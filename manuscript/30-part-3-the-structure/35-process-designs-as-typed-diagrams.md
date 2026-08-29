<!-- Companion sync (ADR-0007): this chapter lands with
     `companion/src/ehr_testing/layers.clj` and
     `companion/test/ehr_testing/layers_test.clj` -- the toy two-layer
     model, `lower` / `erase`, the soundness witness `sound?`, and the
     generative properties `lower-then-erase-is-identity`,
     `two-lowerings-erase-to-one-design`, and the deliberately wrong
     lowering that the law rejects. Figures: `manuscript/figures/` is
     created by this chapter (convention in its README). Register sync:
     C29 flips to "in ch 35". Glossary sync: add witness, resource
     equation, catalytic input, enrichment (vs transformation), lowering,
     erasure, fiber, coherence square, work signature, graded check.
     Bib: cites only fong2019seven, already present. Provenance of the
     formalism is recorded in notes/design-rationale.md, not here. -->

# Process Designs as Typed Diagrams

> **Thesis:** A process design is a typed diagram — kinds of artifact on
> the wires, operations as boxes, composition read left to right — and
> every edge of it is a checkable claim. An implementation is a
> *lowering* of the diagram onto files, stores, and running programs,
> and it is sound exactly when it *erases* back to the design you meant.
> One structure then serves three uses: auditing what a system currently
> is, classifying every proposed change by how much meaning it moves, and
> specifying delegated work precisely enough that the delivered change
> can be type-checked rather than trusted. The rigor is deliberately
> partial — laws are checked by witnesses, not proved — and the last
> section says exactly where that honesty ends.

## Between the whiteboard and the proof assistant

Every test pipeline this book has described was, at some point, a
drawing. Four boxes on a whiteboard — *generate* the corpus, *mutate*
it to inject the defects of Chapter 23, *validate* it through the gates
of Chapter 25, *package* what survives — with arrows between them, and
everyone in the room nodding. The drawing is useful and it is also
unchecked: nothing about it can fail. Six months later the mutate step
reads a configuration file nobody drew, the validate step is silently
skipped for one message type, and the drawing is a picture of an
intention, not of a system. The opposite reaction is to reach for a
formalism heavy enough to prove the pipeline correct, and that reaction
is right about the problem and wrong about the budget: nobody running a
hospital integration project will maintain a mechanized proof of their
corpus pipeline, and a rigor nobody pays for is a rigor nobody has.

There is a middle, and this chapter is about naming it honestly rather
than apologizing for it. Take the whiteboard drawing seriously as a set
of *claims*. "Mutate takes a corpus and produces a corpus" is a claim.
"Validate consumes what mutate produced" is a claim. Each arrow asserts
that a particular kind of thing flows from one operation to the next,
and each such assertion could be false. Now ask, of every arrow, one
question: *what would fail if this were a lie?* A test, a digest
compared against a recorded one, a schema check at the boundary, a
review with a rubric — something that runs, or is performed, and
returns a verdict. Call that thing the arrow's **witness**: the named
check whose failure is what it looks like for the edge to be wrong. Ask
it of the whiteboard pipeline and the usual answer is that two of the
four arrows have one, and the other two have only the nod.

That is the method in miniature. A design language that is
semi-rigorous — the phrase names a discipline, not an absence — is one
in which every edge of the diagram is a claim with a witness, checked
pointwise, with rigor spent where it pays and the unwitnessed edges left
*visibly* unwitnessed rather than quietly assumed. The concept is not
new to this book. Chapter 24 declared that treating naturality
conditions as executable tests is its central technical move; a witness
generalizes that move from the arrows of a data transformation to the
arrows of the process that produces and tests the data. What follows is
the smallest vocabulary that makes the generalization usable.

## The smallest language that earns its keep

Four constructs suffice, and the book already uses three of them
without naming them. The arrows of the whiteboard drawing each carry a
*kind* of artifact — not a particular corpus but "a corpus," not this
message but "a message stream" — and the drawing's rectangles are
operations with such arrows in and out. Call the arrows **wires** and
the rectangles **boxes**; together they make a **diagram**, and
diagrams compose by joining an output wire of one box to an input wire
of the next, so the whole reads left to right as a sentence. This is
string-diagram notation for a monoidal category, in the sense of Fong
and Spivak [@fong2019seven]; readers who want the formal treatment
should go there, and this chapter takes only what it can check.

The fourth construct is the one the whiteboard forgot. The mutate step
*reads* a configuration — which defect classes to inject, at what rate
— and does not consume it: the same configuration is read on every run
and comes out the other side unchanged. Chapter 25's validator reads a
profile the same way. Call an input that participates without being
consumed a **catalytic input**: drawn as a dashed wire into the side of
a box, it is the artifact the box needs on the desk but never uses up.
Naming it matters because catalytic inputs are exactly the wires
whiteboards omit, and an omitted one is an implementation that
hard-codes what the design said it would read — the companion shows
what that does to the law below.

A diagram is a picture, but the form that fits in a repository and a
diff is a line of text per box. Write each box as its inputs, an arrow,
its outputs, the box's name in brackets, and any annotations in braces:

    corpus × config  → corpus    [Mutate]    {catalytic: config}
    corpus × profile → verdicts  [Validate]  {catalytic: profile}

Call a line of this shape a **resource equation**. The equation and the
picture are the same object written two ways: the picture for reading,
the equation for keeping. Composition of boxes is written with the
symbol `⨟`, read "then": `Mutate ⨟ Validate` is the diagram in which
mutate's output wire is validate's input wire, in the order the
sentence says. One sentence is owed to the reader of Chapter 21, which
wrote its commuting triangle as `q ∘ f = q-source` in the
mathematician's applicative order, rightmost arrow first: that is the
same composite this chapter would write `f ⨟ q`, and the book keeps
`∘` where it already stands and uses `⨟` for diagrams, because `⨟`
reads in the direction the diagrams are drawn.

One distinction remains, and it turns out to be the one that carries
the most weight in the sections that follow. Some boxes produce *new*
content: mutate takes a corpus and returns a different corpus. Other
boxes leave their input's content untouched and attach something to it
— a verdict, a score, a digest, a signature. Validate, drawn honestly,
does the second: the message it read is the message it returns, now
stamped with findings. Call the first kind a **transformation** and the
second an **enrichment**. The distinction is operational. An
enrichment is safe to run twice, because the second run finds nothing
changed to stamp; two enrichments that write to different parts of the
stamp can run in either order; a transformation, in general, has
neither property. Part II's gate is an enrichment followed by a branch
on the stamp, and the properties of Chapter 24 are enrichments that
attach a pass or a fail — which is why they compose so freely, and why
a transformation slipped in among them is the change that breaks a
suite. The chapter returns to enrichment when the stamp stops being a
boolean.

## Two layers: what you mean and what runs

Now draw the pipeline twice. In the first drawing the wires carry
kinds — a corpus, a message stream, a verdict — and nothing has a
location, a cost, or a hash. This is the **conceptual layer**: the
design, what you mean. In the second the same boxes appear, but the
wires carry files, tables, queues, cache entries keyed by content hash,
and each box has a runtime — a process, a job, a worker. Between two
boxes there may be a box the first drawing never mentioned: a persist
step, a retry, a checksum. This is the **implementation layer**: what
runs. The move from the first drawing to the second is **lowering**,
and it only ever *adds* — stores, runtimes, infrastructure boxes. The
move back, which forgets everything lowering added and keeps only the
kinds on the wires, is **erasure**.

The design is sound when the two moves compose to nothing: lower the
design onto any substrate, erase the result, and you must have the
design you started from —

    lower ⨟ erase = id

— which is to say, you can always recover what you meant from what
runs. Figure A draws it. Above, the conceptual plane: two boxes, three
wires, one catalytic input. Below, three implementations of the same
design on different substrates, one of them with an infrastructure box
spliced in. Vertical arrows carry `lower` down and `erase` up, and the
three variants are bracketed together on the right as the **fiber** over
the design: the set of every implementation that erases to it.

![Figure A — Two planes: a design, and the fiber of implementations
that erase to it.](manuscript/figures/fig-35-a-two-planes.svg)

The fiber is the picture's most useful part, because it makes a routine
engineering act visible as a *type*. Moving from variant one to variant
two — files to a table and a queue, a persist box added — is a move
*inside* the fiber: the design does not change, and the witnesses of
the design's arrows should not change either. The companion makes the
figure executable. `ehr-testing.layers` carries a toy diagram as data,
a substrate as data, `lower` and `erase` as functions, and `sound?` as
the law written as a boolean; `ehr-testing.layers-test` checks
`lower-then-erase-is-identity` as a test.check property over random
diagrams and random substrates, and `two-lowerings-erase-to-one-design`
as the fiber — two substrates, two implementations, one design. It also
carries a lowering that hard-codes the catalytic input instead of
reading it, and shows the law rejecting it on exactly the diagrams that
read something: the witness fails where it should and nowhere else.

The reader of Chapter 31 will have been waiting with an objection, and
it is a fair one. That chapter showed that round-trip laws *fail* —
that `get` then `put` is never quite the identity across healthcare
formats — and now this chapter posits a round trip that holds exactly.
The resolution is a distinction the book has not needed until now.
Chapter 31's retractions failed because *neither side was yours*: HL7
v2, FHIR, and C-CDA were each built to someone else's purpose, so no
map between them could be lossless, and the ladder slid from
retraction down to lens. At a layer boundary, both sides are yours. The
implementation layer is *constructed* as the image of the conceptual
one: every store is attached to a kind you declared, every
infrastructure box is marked as such the moment it is inserted, and
erasure is total by construction rather than by luck. The law holds not
because implementations are more faithful than formats but because you
designed both ends and built the lower one to be forgettable.

Which brings the honest wrinkle, Chapter 31's lesson applied rather
than contradicted. The moment an arrow of your design exits into a
format you do not control — an HL7 v2 message on a wire to a vendor, a
C-CDA handed to a registry — erasure stops existing for that arrow:
there is no reading the design back from the bytes the vendor received.
The surrogate that remains checkable is to *freeze the lowered bytes*
and witness the freeze. The golden corpus of Chapter 23, the digest
compared on every run, is a fiber narrowed to a single point because no
erasure is available to widen it. Where you own both sides the fiber is
wide and the law is your freedom; where you own one, the fiber is a
point and the digest is your witness.

> **Aside, for the categorically inclined.** `lower ⨟ erase = id` says
> `lower` is a section of `erase` and `erase` a retraction of `lower`
> — retraction being Chapter 11's word for the round trip that failed
> and section Chapter 34's for the splitting the terminology span lacks
> — here holding because the total space is constructed over the base.
> The fiber over a design is the fiber of `erase` in the fibred-category
> sense, and a fiber move is a morphism within it. The companion's two
> properties are the section law, and the statement that every
> substrate's lowering lands in the fiber, distinct substrates at
> distinct points. Pointer: fibred categories and the Grothendieck
> construction, beside the BX literature Chapter 31 cites.

## The diagram as audit instrument

The first use of the structure is retrospective. Draw the diagram of
the system you *have* — not the whiteboard's, the one the repository
implements — and annotate every edge with its witness or its absence.
The exercise is mechanical and worth doing mechanically, box by box,
because the unannotated edges are the audit's finding. A pipeline with
nine arrows and four witnesses is not a pipeline with four checks; it
is a pipeline with five claims never tested, and the diagram is often
the first artifact that says so in one place.

Some edges are not arrows but *squares*. Chapter 32 built the event log
as the one object every format is a view of, and a system built on it
has more than one view of the same log: an emitter producing messages
from it, another producing current-state resources. Each emitter is an
arrow out of the log with its own witness. But the two views also make
a claim *about each other*: on the content they share — the same
patient, the same result — they must agree. Draw the log, the two
emitters, and the shared content as four corners, and the claim is that
both paths around the square land in the same place. Call it a
**coherence square**: two views of one object, and a witness that they
agree where they overlap. Chapter 24's reader has met this shape,
because a coherence square between two views is a metamorphic relation
— translate one way and query, translate the other and query, compare
— and that chapter's central move applies to it unchanged.

The audit's value shows most sharply when the square is missing. A
third emitter added to the log a year later, with its own tests and no
square against the other two, is a correct arrow with an unchecked
relationship — the shape Chapter 12 noted in C-CDA, an authoritative
narrative beside machine-readable entries with nothing enforcing their
agreement. On the annotated diagram the missing square is a blank where
the others have a name, and a blank is easier to argue about than an
absence nobody has drawn. In equation form the audit is a list: one
line per box, one per square, each ending in the name of a witness or
the word *none*. It is short, it is diffable, and it is the first thing
a reviewer should ask for.

## Every change is one of three moves

The second use is prospective, and it falls out of the two layers with
no new machinery: once a system has a conceptual diagram and an
implementation that erases to it, every proposed change classifies by
*how much meaning it moves*.

A **fiber move** swaps implementation with the design fixed: files to a
database, one library to another, a cache added between two boxes. The
design's witnesses are unchanged by definition, and their staying green
after the change is the *proof* that it was a fiber move — not an
assurance, a check. A fiber move that turns a witness red was not a
fiber move; it was a design change wearing an implementation change's
clothes, and the classification has just caught it.

A **new arrow** out of an existing object adds meaning without
changing what was there: a third view of the log, a new mutation
operator beside the existing ones. It may be added freely, with one
obligation — it must satisfy the coherence its siblings already obey.
The third emitter owes a square to the first two; a new mutation
operator owes the invariant that every existing operator's witnesses
still pass. The obligation is a type, and the audit list of the
previous section is where it is checked.

A **change to the conceptual object itself** — a kind on a wire means
something different now, a box's signature changes, the log's event
vocabulary grows — is the expensive kind, and the classification's job
is to see it *declared* as such: everything downstream re-derived,
every witness that touched it re-examined, the change recorded as a
change of design rather than smuggled in as a fix. A roadmap typed by
this classification states each item's blast radius before work
starts, in a vocabulary of three words.

The sharpest example runs the other way, and it is the reason to insist
on the classification even when it seems pedantic. Swapping one
validator engine for another *sounds* like a fiber move: the design
says "validate against the profile," and the engine is a detail of
that box. But a validator is an enrichment whose stamp is a verdict,
and if the two engines' verdicts differ on any item of the corpus then
the kind on the output wire has changed — "verdict by engine one" is
not "verdict by engine two" — and the swap was a change to the
conceptual object, with every consumer of verdicts now re-deriving what
it can trust. The classification forces the question the changelog
would have dodged: do the verdicts agree on the whole corpus, and if
not, on which items, and is that a defect in an engine or a change in
what the design means by *valid*? Chapter 25's gates deserve this
question every time their engine moves.

## Steering delegated work

The third use is the one the previous two were built for. Work on a
system of any size is delegated — to a colleague, a contractor, an
automated agent — and the perennial choice has been between
micromanaging the work and trusting the report of it. The typed diagram
offers a third option: treat the unit of work as an *arrow* and give
it a signature.

The signature declares four things. Its *domain* is the context the
work receives: the artifacts it may change, and — as catalytic inputs —
the design, the conventions, and the existing witnesses it reads but
does not touch. Its *codomain* is the declared shape of the delivered
value: which objects will have changed, a witness for every new arrow,
a record of what moved. Its *invariants* name the fiber the work must
stay inside: every standing witness still passes, and the design above
does not move. And its *scope* names what the work may not touch at
all — objects outside the declared cut, other arrows' witnesses, the
conceptual layer itself. Call the four together a **work signature**:
a resource equation with two annotations added. Figure B draws one.

![Figure B — A delegated unit of work as a typed arrow, and the check
that types its value.](manuscript/figures/fig-35-b-work-signature.svg)

The delivered change is the arrow's *value*, and the decisive step is
that it is **type-checked** against the signature by a checker who is
not the doer, working from the artifacts alone. The checker reads the
delivered objects, runs the witnesses, confirms the scope was untouched
— and does not read the doer's account of the work, which Figure B
draws as a dashed line that never reaches the verification box. This is
not distrust of any particular doer; it is the observation that a
narrative cannot be type-checked and an artifact can. On a type error
the doer *halts and reports* rather than improvising a repair: a value
without the declared shape is a finding about the signature or about
the work, and either way the signature's author needs to know before
anyone patches around it. Autonomy, on this reading, is freedom of
movement inside the declared fiber, and exactly that — a great deal of
freedom, since the fiber over a design is wide, bounded in a way both
parties can see.

A worked signature, in the book's own domain. The unit of work is *add
one mutation operator to the corpus's injection layer* (Chapter 23).
Domain: the injection namespace and its tests, consumed; the existing
operators, the defect-class vocabulary, and the specimen's purpose-set
properties, catalytic. Codomain: one new operator, one witness showing
it injects exactly the defect it names and that the property it targets
catches it, and a one-line record.
Invariants: every existing operator's witnesses unchanged and green;
the purpose set unchanged. Scope: no edit to any existing operator or
property. Verification: a fresh checkout, the full witness suite, and a
diff confined to the declared cut. Nothing in that signature is exotic;
it is the review most teams believe they perform, written down as a
type so that it is performed the same way every time and its failures
are findings rather than arguments.

## When the check itself is soft

Every witness so far has returned a boolean: a digest matches or does
not, a property passes or fails, a diff is inside the cut or it is not.
But some of the arrows this book cares about most are checked by
something that returns a *grade* — a review rubric, a score, a
clinician's judgment that a mapping is acceptable for the purpose at
hand — and it is tempting to keep these in a separate column from the
real checks. They are not a different kind of thing. A **graded check**
does what a crisp one does, which is test membership against a declared
shape; it differs only in its codomain, a scale of grades in place of
two booleans. A rubric is a type whose inhabitation comes by degree.

Two consequences follow, both already met in other clothes. First, a
graded pass enters the record *with its grade*. An enrichment that
stamps a message "reviewed" has stamped it with almost nothing; one
that stamps it "reviewed, 3 of 5 on the mapping-fidelity rubric, by
this reviewer, against this rubric version" has stamped it with a value
the next box can act on. Second, grades only degrade through
composition. A chain of checked arrows is never more trustworthy than
its weakest checked link, so a high-confidence finding cannot be
manufactured downstream of a medium-confidence input by any amount of
careful processing; if a higher grade is wanted, the input must be
improved, not the operation. Both are the move Chapter 34 made when it
insisted that a ConceptMap row carry its equivalence annotation and
that a translation check *read* it: an arrow that says *how well*, not
merely *whether*, and a downstream check that refuses to assert more
than the annotation licensed. The graded witness is the same honesty,
applied to the process rather than the data.

## What this does not give you

The rigor is partial, and it is worth stating plainly where it stops,
because a method that overstates itself is worse than a whiteboard.

There is no semantics for the inside of a box. The diagram says what an
operation consumes and produces; it never says the operation is correct
inside. A validate box that returns a verdict on every message has
satisfied its signature even if every verdict is wrong, and the only
thing that catches a wrong verdict is a witness of the arrow — a
property, a golden case, a coherence square against another view — not
the type of the box.

Witnesses are pointwise, not theorems. Green today, on these inputs,
under this seed, is what a witness certifies, and nothing about inputs
it has not seen. The companion's law has been checked on three hundred
random diagrams per run; that is evidence, of the kind Chapter 24
argued is right for this domain, and it is not a proof.

The delegation reading is a discipline, not a proof. A checker verifies
the *type* of a delivered change — shape, invariants, scope — and not
its wisdom. A change can type-check perfectly and be a bad idea; the
signature's author, not the checker, is responsible for having declared
the right fiber. What the discipline buys is that a bad idea arrives as
a well-typed value someone can argue about, instead of as a narrative
nobody can.

And where erasure does not exist, the honest move is to say so and
freeze the bytes, not to draw an `erase` arrow with nothing behind it.
The fiber narrows to a point at every edge of what you own; the diagram
should show the point, the digest that witnesses it, and nothing more.
Part III began by weakening the arrow, found the object every format is
a view of, exposed the types inside the data, and showed where arrows
stop being functions. This chapter has lifted the same machinery one
level, from the structure of the data to the structure of the process
that produces and tests it — and stopped, as the rest of Part III did,
at the exact boundary of what it can check.
