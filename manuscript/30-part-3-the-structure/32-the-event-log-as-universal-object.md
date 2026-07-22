# The Event Log as Universal Object

> **Thesis:** Chapter 11's canonical format U fails as a lossless retract,
> but a different candidate for "the universal object" actually works: a
> bitemporal event log, where every format the book has discussed is either
> a view over the log (a projection or query) or a way of ingesting into
> it. Distinguishing valid time (when something was true clinically) from
> transaction time (when the system learned it) lets attestations and
> corrections be modeled as appended events rather than in-place edits, and
> reading the log this way is exactly the CQRS pattern, with XTDB as one
> concrete substrate.

## What "universal object" needs to mean for this to work

Not lossless-encoding-of-everything, but the one thing every format can be
derived from.

## Valid time versus transaction time, operationally first

"When it was true" versus "when we found out," with a concrete clinical
example, before the bitemporal vocabulary.

## Attestation and correction as appended events

Why a C-CDA signature (Chapter 12) and a later amendment both become new
events, never edits to old ones.

## Formats as views

HL7 v2 as an ingestion stream, FHIR as a current-state query, C-CDA as a
point-in-time snapshot query — all reads of the same log.

## CQRS and XTDB as a concrete substrate

How this maps onto command/query separation, and where XTDB fits as one
implementation (full entry in Chapter 43).
