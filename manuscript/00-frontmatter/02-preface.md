# Preface

> **Thesis:** This book is for the engineer who has been handed a mapping
> spreadsheet, a validator, and a deadline, and who suspects — correctly —
> that passing the validator is not the same thing as being correct. It
> teaches a way of thinking about EHR data transformations, built on
> observational correctness and a small amount of category theory, alongside
> the Clojure tools that make the thinking executable. It is written for two
> readers at once: one arriving from Clojure and functional programming who
> needs the healthcare domain, and one arriving from years of HL7 v2 and
> FHIR integration work — often in Python — who needs the theoretical
> vocabulary for what they already know how to do.

## Who this book is for

Two entry points, one destination, named explicitly so each reader knows
where they are.

## What "test plan" means here

Not a checklist — an artifact with a purpose set, a corpus, and a property
catalog, all reviewable.

## How to read this book

Parts II and III can be read in either order by readers strong in one
tradition; Part IV is reference, not narrative.

## The specimen

One example — an HL7 v2 ORU result becoming a FHIR Observation — runs
through the whole book; where to find it in code.

## Acknowledgments and scope

What this book does not cover (v3/RIM implementation detail, full CDA
authoring, terminology server operation) and why.

The clinical values, ranges, and codes in this book's examples are
illustrative of testing technique only; nothing in this repository
is clinical guidance or intended for diagnostic or treatment use.
HL7®, FHIR®, and the FHIR flame design are registered trademarks
of Health Level Seven International; their use here is descriptive
and does not imply endorsement.

All patient data in the examples throughout this book is entirely
synthetic; no real patient data or production message content
appears anywhere.
