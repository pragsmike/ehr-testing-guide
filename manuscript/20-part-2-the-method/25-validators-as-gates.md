# Validators as Gates

> **Thesis:** Structural validation — parsers, profile validators,
> conformance checkers — is necessary and answers a real question, but it
> is a different question from semantic correctness, and confusing the two
> is one of the most common failures in EHR integration test plans. Tools
> like HAPI's validators, NIST's v2-validation suite, and FHIR's
> validator_cli belong at a specific, fixed point in the pipeline: always
> upstream of the semantic properties from Chapter 24, gating malformed
> input out before the properties are asked to reason about it, never
> substituting for them.

## What structural validation actually checks

Well-formedness and profile conformance, and nothing about meaning.

## Why passing validation is not evidence of correctness

A message can be perfectly conformant and still misrepresent the source.

## Where validators sit in the pipeline

Always upstream of semantic properties — never interleaved, never instead
of.

## A tour of the tools

HAPI FHIR's validator, NIST's v2-validation, `validator_cli`, and what each
one is and is not responsible for (full entries in Chapter 43).

## Wiring a validator into a test plan

Where the gate goes, concretely, in the specimen's test suite.
