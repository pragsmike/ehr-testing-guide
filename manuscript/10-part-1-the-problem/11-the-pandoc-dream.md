<!-- Companion sync (ADR-0007): none — this chapter deliberately shows no
     listings, so it creates no companion obligation. Keep it that way in
     revision; the first executable material is Chapter 21's. Register sync:
     flip C1 to "in ch 11" and C2 to "in ch 11" (C2's second destination,
     ch 31, still pending — same convention as C19/C24/C26). Glossary sync:
     add universal object, coproduct, colimit, hub architecture, Z-segment,
     extension (FHIR), and open template; verify retraction, lens, span,
     residue, purpose set, delta/sigma, and fold already exist (they do) —
     this chapter uses them at gesture strength and the entries already
     point at their owning chapters. Acronyms: nothing new (BX, CDA, C-CDA,
     FHIR, IG all present). Bib sync: add a @misc entry for Pandoc
     (key suggestion: pandoc; John MacFarlane, https://pandoc.org,
     accessed-date TODO) cited at first mention; the catalog-of-breakage
     sentence cites the existing hl7_v2_to_fhir_ig and cda_fhir_mapping_ig
     keys (both still carry TODO URLs — the eventual verification pass
     covers them). Status sync: flip chapter 11 to review in AUTHORS-GUIDE
     on landing. -->

# The Pandoc Dream

> **Thesis:** Every interop architect who has faced N incompatible formats
> has had the same idea: define one universal canonical format U, write N
> translators in and N translators out instead of N², and be done with it —
> the Pandoc dream. The dream is not wrong to want; it is wrong about what
> U would have to be, because it assumes each translation into U and back
> out is a *retraction* — lossless, information-preserving — when for
> healthcare data no such U exists. This book takes that failure seriously
> as structure, not as a misfortune to be engineered around, and builds its
> method on top of it rather than pretending it isn't there.

## The N-squared problem

A hospital of any size runs many systems that must exchange records: a
laboratory system, an electronic health record, a registration system, a
billing system, a state registry feed, a health-information exchange. Each
speaks its own representation, and the naive architecture connects each to
each — a translator from the lab system to the EHR, another from the EHR
to the registry, another back. With *n* representations in play, that is
on the order of *n²* directed translators to write, test, and maintain:
four formats already means twelve, and ten formats means ninety. Worse
than the count is the coupling. Every new system multiplies the work by
the number of systems already present, and every change to one
representation ripples into every translator that touches it.

Faced with this, every integration team eventually has the same idea,
independently, and usually with the feeling of having discovered
something: put one representation in the middle. Define a single canonical
format that every system's data maps *into* and every system's data can be
produced *from*, and the *n²* translators collapse to *2n* — one arrow in
and one arrow out per format. New system? Two translators, not *2n* more.
An architecture with one designated representation at the center and every
other format reduced to a spoke is a **hub architecture**, and the
designated center is the dream's protagonist: the universal canonical
format, *U*. The idea is so natural that healthcare has tried it at every
scale — site integration engines are built around exactly this shape, and
FHIR itself was, in part, an attempt to be *U* for the whole domain.

The dream's arithmetic is right, and nothing in this book argues with it.
What the rest of this chapter examines is the dream's *correctness* claim
— the assumption, usually unstated, about what each spoke's two arrows
must preserve for the hub to be not merely convenient but safe.

## What a retraction is, and why U would need to be one

Picture the check the hub architecture silently relies on. Take a record
in some format *A* — a lab result message, say. Translate it into *U*.
Now translate it back out into *A*, and compare what you got with what
you started with. If the two are identical — not similar, not clinically
close, but the same record — then the round trip through the hub lost
nothing, and you would be entitled to say the detour through *U* was
free. The dream needs this to hold for *every* record of *every* format,
because that is what it means for the hub to be the equal of the
point-to-point translators it replaced: any translation *A → B* now
factors through *U*, going *A → U → B*, and the route is only as
faithful as its first leg. If translating into *U* discards something,
every route out of *A* discards it too — the loss is not one translator's
bug but a toll charged at the hub to every passenger, and it compounds
with whatever the second leg drops on the way out.

This round-trip condition has a categorical name, and this book's
convention — here and everywhere — is to show the machinery at work
before naming it. An object *U* that everything maps into and out of
again, coherently, is a **universal object**; the requirement that
going into *U* and coming back out returns exactly what you started
with makes each format *A* a **retract** of *U*, and the in-and-back-out
round trip itself a **retraction** — the precise statement of "converts
losslessly." Stated in one line: the Pandoc dream is the claim that a
single *U* exists of which every healthcare format is a retract. That is
a strong claim, and its strength is the point. If it held, the hub would
be *correct* — provably as good as any point-to-point translation — and
this book would be a pamphlet. The next two sections ask where claims of
this shape actually do hold, and then why healthcare is not among those
places.

## Where the dream comes from, and where it has worked

The dream deserves its due, because it is not a fantasy — it is a
generalization from real successes, and seeing exactly what made those
successes work is the fastest way to see what healthcare lacks.

The clean case is character encodings. For decades, text interchange was
its own *n²* problem — Latin-1, Shift JIS, KOI8-R, EBCDIC, each carving
up the space of characters its own way — until Unicode supplied a genuine
*U*: a single space of code points into which each legacy encoding maps,
and from which it can be recovered. The round trip holds — a Latin-1 file
converted to Unicode and back is byte-identical — and it holds for a
reason worth stating carefully: the legacy encodings were all encodings
*of the same kind of thing*. Each was a way of writing down a sequence of
characters, the domain had (with effort and standards politics) an agreed
answer to what the characters *were*, and Unicode was built by
enumerating that answer and giving every legacy code point a home in it.
The universal object existed because the underlying abstraction was
shared before *U* was built; *U* named it rather than invented it.

The instructive case — the one this chapter is named for — is Pandoc,
which readers from the healthcare side may not know and readers who
write markdown may use daily. Pandoc [@pandoc] is a universal document
converter: one internal document model, dozens of readers that parse
formats (Markdown, HTML, LaTeX, Word) into that model, dozens of writers
that render the model back out. It is the hub architecture executed
about as well as it can be, and it works remarkably well — which is
exactly why its failure mode is so informative. Pandoc's round trips are
*not* lossless. A LaTeX document is more expressive than the internal
model; so is Word; so, in places, is Markdown. Pandoc survives this by
an escape hatch: a **raw block**, a chunk of format-specific content
carried opaquely through the model, untranslated, meaningful only if the
output format happens to be the one the chunk came from. The raw block
is a confession, made in the format's own syntax, that *U* does not
cover the sources — the hub works for the common core and smuggles the
rest. For document conversion this is a fine bargain; a dropped LaTeX
nicety costs a grumble. The question the next section answers is which
of these two cases healthcare resembles — and the answer is the second,
with the smuggling forbidden and the grumble replaced by a patient.

## Why healthcare has no such U

Two things go wrong with the healthcare *U*, and both are informative
rather than fatal — each failure tells you something structural about
the domain, which is why this book treats them as findings rather than
obstacles.

The first failure is one of *shape*: the imagined *U* is usually the
wrong kind of object. Ask an architect to sketch the universal format
and the sketch is almost always a union — a superset schema with a place
for everything any format carries, v2's fields and FHIR's elements and
CDA's sections side by side. But walk through what a union actually
provides. It gives every format a way *in* — each format's content has
its reserved corner — and that is all it gives. There is no canonical
way back out, because nothing in a union says how another format's
corner projects onto yours; and, more damningly, nothing in a union
*identifies* the content two formats share. The patient's glucose value
sits in the v2 corner and again in the FHIR corner as two unrelated
residents. A disjoint pile of representations with an inclusion arrow
from each — that construction is, categorically, a **coproduct**, and a
coproduct is precisely the structure that answers "how do I store all of
these" while refusing to answer "which parts of these are the same." What
the hub actually needs is the opposite move: not juxtaposing the formats
but *gluing* them — taking the pile and then identifying, pair by pair,
the places where two formats say the same thing, so that the shared
content exists once and each format's remainder hangs off it. Gluing a
family of objects together along their declared overlaps is a
**colimit**, and naming it buys something concrete: it says where all
the difficulty lives. The pile is trivial; the identifications are the
work. And in healthcare the identifications are genuinely hard, because
the standards disagree not merely in syntax but in how they carve
clinical reality — what counts as one observation, where a medication
ends and an order begins, which of two codes is "the same" concept. The
later chapters on terminology (Chapter 34 especially) are an extended
tour of exactly this difficulty; here it is enough that the dream's
usual sketch is a coproduct wearing a colimit's job description.

The second failure is blunter: the round-trip laws just fail, concretely,
on the formats as specified — and not at the margins, but by design.
Every major healthcare format is deliberately open-ended. A FHIR
resource may carry **extensions** — locally defined elements, sanctioned
by the standard, holding content no fixed schema anticipated. An HL7 v2
message may carry **Z-segments** — locally defined segments, named with
a leading Z, that the standard explicitly reserves for site-specific
content. A CDA document's sections are governed by **open templates** —
templates that constrain what a section must contain while permitting
content beyond the constraints. Each mechanism exists because the
standards' authors knew no fixed schema would cover practice, and each
is a raw block by another name — except that where Pandoc's raw block
carries its cargo through the hub, a healthcare *U* with a fixed target
schema simply drops it. Any concrete instance can carry content outside
any fixed *U*; therefore every fixed *U* is lossy for someone. This is
not a speculative worry. The official mapping guides between the
formats — the V2-to-FHIR guide, the C-CDA-to-FHIR guide
[@hl7_v2_to_fhir_ig; @cda_fhir_mapping_ig] — are, read with this
chapter's eyes, a catalog of exactly where the retraction breaks: each
hedged mapping row, each "context dependent," each note that a target
element has no source is a documented point at which in-and-back-out
returns something other than what went in.

And beneath both failures sits a deeper one, which the next chapter
develops in full and this paragraph only opens. The three format
families are not three encodings of one kind of value, the way Latin-1
and Shift JIS were two encodings of character sequences. An HL7 v2
message reports an *event* — something happened: an admission, an
order, a result. A FHIR resource carries a *state* — the current value
of something. A C-CDA document is an *attested snapshot* — a state
frozen at a moment and signed, with the signature part of the content.
A state is what you get by replaying events — the relationship of a
bank balance to the transactions — so two of the three are related the
way an integral is related to a derivative, and the third adds an
authorship act on top. Between objects of different kinds, some arrows
are not lossy translations; they are *not functions at all* — a point
Chapter 12 makes precise. Unicode had one abstraction to name.
Healthcare has at least three, and they are not interconvertible even
in principle.

## Taking the failure as structure

There are two ways to respond to a failed dream, and the difference
between them is this book's founding decision. The common response is to
engineer around the failure: keep the hub, accept the loss, patch the
worst gaps with site-specific mappings, and hope that what falls through
is never the field a clinician needed. Every working integration
engineer has lived inside this response; its signature is that the loss
is *implicit* — distributed through a thousand mapping decisions,
enumerated nowhere, discovered in production. The other response, this
book's, is to take the failure as structure: treat the exact ways the
retraction breaks as load-bearing facts about the domain, and build the
method on top of them.

Taken as structure, each failure dictates a piece of the method. Because
no arrow between formats is a lossless round trip, the honest arrows are
*weaker* things — a read-and-write-back pair whose laws are asymmetric
(a **lens**, developed in Chapter 31), or an explicit shared core with a
projection to each side (a **span**, also Chapter 31) — arrows that
tolerate, and more importantly *declare*, what the map back cannot
recover. Because the difficulty lives in the gluing, the shared core is
not an implementation detail but the central object of negotiation —
and Part II will find the same object arriving from the opposite
direction, as the overlap of what different data consumers require.
Because the formats are different kinds, the closest thing to an honest
universal object turns out not to be a format at all but an append-only
log of the events themselves, of which every format is a view or an
ingestion — Part III's subject. And because every real transformation
therefore loses something, "is it correct?" cannot mean "does it
preserve everything?" — it has to mean *correct for something*: for the
specific questions the data's recipient will ask of it. That
reframing, made precise and executable, is Part II, and what a
transformation drops becomes not a silent gap but a named, enumerated,
reviewed artifact — the residue — with a signature on it.

None of that machinery is built yet; this chapter's job was only to
clear the ground it is built on. The next chapter makes the three-kinds
claim precise — events, states, and attested snapshots, and what
arrows can and cannot exist between them. Chapter 13 orients readers
new to the standards landscape. Then Part II builds the method, Part
III the structure beneath it, and Part IV the dated reference shelf.
The dream's arithmetic was always right: the way out of *n²* really
does run through understanding what the formats share. It just doesn't
run through a format.
