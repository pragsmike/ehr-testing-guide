# Notes for the Non-Clojure Reader

> **Thesis:** This is a bridge chapter, addressed directly to the reader
> arriving from Python and years of HL7 integration work. Almost everything
> essential in this book — schemas expressed as data, generative and
> property-based testing, the property and metamorphic-relation catalog,
> the purpose-set method itself — is not Clojure-specific at all. What *is*
> incidental is the choice of Malli over Pydantic and test.check over
> Hypothesis; those are this book's implementation choices, not part of the
> method, and the mapping between them is close enough that this chapter
> can just hand it to you directly.

## What's essential versus what's incidental

A direct list: the method survives a rewrite into any stack; the library
names do not.

## Malli, for the Pydantic reader

Schemas as data versus schemas as classes, and where the analogy holds and
breaks.

## test.check, for the Hypothesis reader

Generators and properties mapped one-to-one where possible.

## Reading the rest of this book from here

Which chapters to slow down on, and which code can be skimmed as "this is
just the Python version with parentheses."
