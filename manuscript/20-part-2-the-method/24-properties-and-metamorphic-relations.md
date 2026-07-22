# Properties and Metamorphic Relations

> **Thesis:** Property-based testing lets us execute the observational
> definition of correctness directly — for all x, q(f(x)) equals
> q-source(x) — instead of enumerating examples. But some queries have no
> independent oracle: nothing computes the expected answer except the
> transform itself. Metamorphic relations solve this by relating outputs of
> *related* inputs instead of checking outputs against an oracle —
> permutation invariance, or translate-then-filter equals filter-then-
> translate. These relations are naturality conditions, and treating them
> as executable tests is this book's central technical move.

## The oracle problem

Why some transforms have no independently computable expected output.

## Property-based testing as direct execution of Chapter 21's definition

Turning `q(f(x)) = q_source(x)` into a test.check property over generated
inputs.

## Metamorphic relations, operationally first

Permutation invariance and translate-then-filter/filter-then-translate,
before the word "naturality" appears.

## Naming it: naturality conditions as tests

Why a metamorphic relation is exactly a naturality square, and why that
equivalence is useful rather than decorative.

## Building a property catalog for the specimen

ORU-to-Observation properties and metamorphic relations, worked concretely.
