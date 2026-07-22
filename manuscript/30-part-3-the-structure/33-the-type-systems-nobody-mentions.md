# The Type Systems Nobody Mentions

> **Thesis:** HL7 v2 has a real, if informal, two-level type system: a
> fixed set of structural datatypes the standard itself owns (ST, NM, CWE,
> and the rest) whose *codomains* are plugged in externally by terminology
> tables such as Table 0396. OBX-2 is a runtime type tag that determines
> how OBX-5 must be read, making OBX-5 a dependent sum in everything but
> name. FHIR's `value[x]` pattern is the same structure made syntactically
> explicit. Naming this makes visible where real-world messages violate
> their own declared bindings — a violation class every test plan needs to
> check for and mostly doesn't.

## Structural datatypes, owned by the standard

ST, NM, CWE, and friends as v2's fixed vocabulary of shapes.

## Codomains plugged in from outside

Table 0396 and friends as the terminology layer supplying what the
datatypes point at.

## OBX-2 as a runtime type tag

Reading OBX-2 before you can know how to parse OBX-5 — a type check that
happens at runtime, in the data.

## Naming it: OBX-5 as a dependent sum

Why "the type of this field depends on the value of that field" is
precisely a dependent sum, stated only after the mechanism is already
familiar.

## FHIR's value[x] as the same idea, explicit

Comparing `valueQuantity` / `valueString` / ... directly against OBX-2/OBX-5,
and where real messages violate their declared bindings.
