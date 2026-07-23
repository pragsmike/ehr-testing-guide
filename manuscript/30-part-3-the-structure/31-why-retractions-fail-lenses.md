# Why Retractions Fail, and Lenses

> **Thesis:** The Pandoc dream from Chapter 11 fails in principle, not just
> in practice: vendor extensions, Z-segments, and the narrative-versus-
> entries split in C-CDA all carry information no target format has a slot
> for, so no round-trip through a canonical form can be lossless. A lens —
> a get/put pair obeying round-trip laws — is the honest weakening: it
> names exactly what is preserved and what is not, instead of pretending
> nothing is lost. This chapter distinguishes state-based from delta lenses
> and treats the round-trip laws as properties to run, not just equations
> to admire.

## Where losslessness actually breaks

Extensions, Z-segments, and narrative-vs-entries as three concrete leaks,
tied back to Chapter 11's retraction.

## Lenses, operationally first

get and put as two functions with a promise between them, before the word
"lens" appears.

## The round-trip laws

GetPut and PutGet, stated first as "if you put back what you got, nothing
changes" and "if you get what you just put, you see what you put."

## The third law: a second pass changes nothing

Run the round trip a second time on its own output and check that nothing
changes further — the diagnostic split between loss that *stabilizes*
after one pass (the record settles into what the target can hold) and
loss that *erodes* on every pass, like re-saving a JPEG. Weaker than
GetPut and PutGet, and for that reason worth running exactly where they
fail.

## State-based versus delta lenses

Why the kind of object (Chapter 12) determines which flavor of lens
applies.

## The specimen as a lens

`oru->observation` / `observation->oru` as get/put, and exactly where its
round-trip law fails — plus a pointer to the BX (bidirectional
transformations) literature for readers who want the full formal treatment.

> **Aside, for the categorically inclined** (scoped exception to
> ADR-0010's introduce-twice rule, flagged here for author ratification
> rather than assumed — this aside addresses only readers who already
> hold `adjunction`, `closure operator`, and `triangle identity`, so none
> of the three is introduced operationally-first, and none gets a
> glossary/acronym entry on the strength of appearing only here): the
> stabilized round trip is a **closure operator**; the book's retract →
> lens ladder reads as successively weakened **adjunctions**, with the
> third law surviving as the shadow of the **triangle identities**
> (f∘g∘f = f holds in any adjunction, even where neither round trip is
> the identity). Adjunction itself fails wherever no best approximant
> exists — exactly the cross-cutting terminology maps of Chapter 34,
> where spans take over. Pointer: Galois connections / abstract
> interpretation, parallel to the BX pointer above.
