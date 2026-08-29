# companion

The reference implementation for the *EHR Testing Guide*. Every code
listing in the book (`../manuscript/`) lives and runs here — this project
is not sample code written to illustrate the book after the fact, it is
the thing the book quotes from. It is also meant to be cloned and adapted:
a reasonable starting point for a real test plan against a real HL7 v2,
FHIR, or C-CDA transformation, not just a teaching artifact.

## Platform notes: WSL and Windows

This project is developed from both native Windows and WSL, and both are
supported the same way, using the same `deps.edn` — there's nothing
platform-specific in it. The only per-platform difference is how you get
the `clojure` CLI itself onto `PATH`:

- **WSL (the reference environment for Spacemacs/CIDER work)** — install
  the official Clojure CLI as you would on any Linux box: the
  [linux-install script](https://clojure.org/guides/install_clojure)
  (`curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh`,
  then `chmod +x` and run it with `sudo`), or `sudo apt install clojure`
  on Debian/Ubuntu if the packaged version is recent enough. `make` and
  `pandoc` are both a plain `sudo apt install make pandoc` away, so
  running `make check` from the repo root works out of the box once the
  CLI is installed.
- **Windows** — the official installer is an MSI; if you'd rather not
  install system-wide, [deps.clj](https://github.com/borkdude/deps.clj)
  is a single-binary implementation of the same `clojure`/`clj` CLI (no
  admin rights needed) and is what this repo's own CI-equivalent checks
  were run with.

Building and running tests is fine from either platform. **Committing is
not** — see the root [`AUTHORS-GUIDE.md`](../AUTHORS-GUIDE.md#7-git-operations-wsl-only):
all git operations on this repo happen from WSL only.

If you clone or access this same working tree from both sides (e.g. this
repo on the Windows filesystem, reached from WSL via `/mnt/c/...`), see
the root `.gitattributes` — it forces LF line endings on checkout
regardless of platform `core.autocrlf` settings, which is what keeps the
Makefile and these Clojure sources working identically on both sides.
Cloning a separate copy into the WSL filesystem (e.g. under `~/`) instead
of working through `/mnt/c` will generally feel faster in day-to-day
editing, but isn't required.

## Running the tests

```sh
clojure -X:test
```

This uses [cognitect-labs/test-runner](https://github.com/cognitect-labs/test-runner)
via the `:test` alias's `:exec-fn`, resolving only `deps.edn`'s base deps
plus the runner — it does not require the `:hapi` alias and should be
fast even on a cold dependency cache.

## Jacking in from CIDER (Spacemacs, in WSL)

The `:dev` alias adds `nrepl/nrepl` and `cider/cider-nrepl` without
polluting the base project's dependency set. From Spacemacs, `cider-jack-in`
(or `SPC m j j` in a Clojure buffer) with your `clojure-cli-jack-in-dependencies`
or equivalent configured to add `:dev` will start an nREPL server with
CIDER middleware already on the classpath. From the shell directly:

```sh
clojure -M:dev
```

`companion/.lsp/config.edn` and `companion/.clj-kondo/config.edn` are
checked in so `clojure-lsp` and `clj-kondo` work with no extra local setup
beyond having the binaries installed — in WSL, both are typically a
`sudo apt install clojure-lsp clj-kondo` or a download from their
respective GitHub releases away.

## The `:hapi` alias

HAPI FHIR (`ca.uhn.hapi.fhir/*`) and HAPI HL7v2 (`ca.uhn.hapi/*`) are kept
out of the base `:deps` and out of `:test`, in their own `:hapi` alias.
They pull in a large Java dependency graph that's slow to resolve on a
cold cache; keeping them isolated means the core project — and
`clojure -X:test` — stay fast for everyone who isn't currently working on
a listing that needs them. Add them explicitly when you do:

```sh
clojure -M:hapi -e "(compile 'ehr-testing.specimen)"   # example only
```

## Namespaces

- `ehr-testing.specimen` — the book's recurring worked example: an HL7 v2
  ORU^R01 result becoming a FHIR R4 Observation. See its docstring and
  `AUTHORS-GUIDE.md` section 5 for the full list of chapters that
  reference it.
- `ehr-testing.schemas` — the Malli schemas for the specimen's shapes.
- `ehr-testing.properties` — test.check properties: lens laws and
  observational-correctness properties, expressed as executable
  properties over the schemas and specimen functions.
- `ehr-testing.corpus` — the corpus's mutation layer (Chapter 23):
  controlled, labelled defect injection over the specimen's cases.
- `ehr-testing.layers` — Chapter 35's toy two-layer model: a process
  design as data, `lower` onto a substrate, `erase` back, and the
  soundness law `lower ⨟ erase = id` as a test.check property.
