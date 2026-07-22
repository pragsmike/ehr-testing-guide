# EHR Testing Guide

This repository contains two things that evolve together:

1. **A book.** A practitioner's guide to writing test plans for EHR
   (electronic health record) data transformations — the HL7 v2, FHIR, and
   C-CDA translation work that every health-data integration eventually does
   and every team eventually gets wrong in the same handful of ways. The book
   uses Clojure tooling for its worked examples and category theory (lenses,
   spans, naturality) as its reasoning language, not as decoration. It is
   written for two audiences at once: readers who come from Clojure,
   functional programming, or category theory and are learning the healthcare
   domain; and healthcare-integration veterans — often working in Python — who
   are learning the theoretical structure underneath work they already know
   how to do. Chapter 26 is addressed directly to the second audience.

2. **A companion Clojure project.** The book's reference implementation.
   Every code listing in the manuscript runs here — nothing in the book is
   pseudocode. The project is also meant to be cloned and adapted: it is a
   reasonable starting point for a real test plan against real HL7 v2, FHIR,
   or C-CDA transformations, not just a teaching artifact. All patient data
   in this repository — the sample messages, every generated case, every
   worked example — is entirely synthetic. No real patient data, PHI, or
   production message content appears anywhere in this repository or its
   history.

## Layout

```
manuscript/   the book's source, in Pandoc-flavored Markdown
companion/    the Clojure reference implementation (deps.edn project)
```

See [`manuscript/`](manuscript/) for chapter-by-chapter structure and
[`companion/README.md`](companion/README.md) for how the code project is
organized.

## Building

From the repo root:

```sh
make book   # concatenate manuscript chapters into build/book.md
            # (and build/book.html if pandoc is installed)
make test   # run the companion project's test suite
make check  # both of the above
```

`make book` does not require Pandoc to succeed — without it you still get
`build/book.md`, just not the rendered HTML. `make test` requires the
Clojure CLI (`clojure`) to be installed and runs `clojure -X:test` inside
`companion/`.

This works the same way from WSL or from native Windows — see
[`companion/README.md`](companion/README.md#platform-notes-wsl-and-windows)
for how to get `clojure`, `make`, and `pandoc` on each. `.gitattributes`
pins line endings to LF so the working tree is identical either way.

**Building is supported on both platforms; git operations are not.** All
`git` commands — commits above all — are done from WSL only. See
[`AUTHORS-GUIDE.md`](AUTHORS-GUIDE.md#7-git-operations-wsl-only) for why
and for the one-time `make hooks` setup that enforces it.

See [`AUTHORS-GUIDE.md`](AUTHORS-GUIDE.md) for how the manuscript and the
companion project are meant to be written together, and the conventions
that keep them from drifting apart.

## License

Everything in this repository except the manuscript's prose —
the `companion/` project, build tooling, notes, and agent
scaffolding — is licensed under [Apache-2.0](LICENSE). The
manuscript's prose is
[CC BY-NC-ND 4.0](manuscript/LICENSE.md), with two express
exceptions: code listings shown in the book remain Apache-2.0,
and the templates and worksheets may be copied, filled, and
adapted for any purpose. See
[`manuscript/LICENSE.md`](manuscript/LICENSE.md) for the exact
terms.
