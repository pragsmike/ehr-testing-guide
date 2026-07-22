# Test Plan Templates

> **Thesis:** The rest of this book argues *why* correctness for an EHR
> transformation is correctness-relative-to-a-purpose-set. This chapter is
> where that argument becomes something you fill in. It collects four
> worksheets — a purpose-set worksheet, a corpus design checklist, a
> property catalog, and a residue review sheet — each usable on its own,
> each demonstrated on the book's running ORU→Observation specimen. If you
> have a transformation to test and a deadline, start here and follow the
> cross-references back into the chapters only where a worksheet field
> surprises you.

<!-- Terms first used here that need back-matter entries: purpose set,
     correctness lattice, residue, observation (as query q / q-source),
     golden case, metamorphic relation, criticality tier. Several are
     already seeded in 91-glossary.md — verify rather than duplicate. -->

These worksheets are deliberately format-heavy: they are meant to be
copied, filled, and checked off, not read as prose. That makes this the one
chapter where tables and checklists *are* the content.

## 1. The purpose-set worksheet

Fill in one worksheet per **(transformation, recipient)** pair — not one per
transformation. The same ORU→Observation map is a different test target for
a clinician reconciling medications than for a quality-reporting pipeline,
and the point of Chapter 22's *correctness lattice* — the fact that one
transform can be correct for one recipient and incorrect for another at the
same time — only becomes visible when you fill in the worksheet twice.

For each recipient, a **purpose set** is the list of questions that
recipient will actually ask of the transformed data in order to act. Each
question is an **observation**: a query `q` you can run against the target,
paired with the corresponding query `q-source` against the source. The
transform is correct *for that question* when `q(f(x))` and `q-source(x)`
agree — that is, when asking the question after transforming gives the same
answer as asking it before. Correct *for the recipient* means correct for
every question in their set.

| Field | What goes here |
|---|---|
| Recipient & action | Who consumes the output, and the decision or action they take with it. |
| Question (`q`) | One thing the recipient must be able to read from the output. |
| Source query (`q-source`) | The same question asked of the input. |
| Agreement tolerance | Exact / normalized (e.g. unit-canonicalized) / same classification (e.g. in-range vs out) / set membership / within period. |
| Criticality tier | Safety-critical / decision-affecting / cosmetic. Drives corpus depth and gate placement. |
| Notes | Where this question's answer can go missing (forward-reference a residue-sheet row). |

### Worked: ORU→Observation, two recipients

The specimen is one OBX carrying a numeric serum-glucose result
(`2345-7^Glucose^LN`, value `95`, units `mg/dL`, reference range `70-100`,
abnormal flag `N`, status `F`). Filled for two recipients, the divergence is
the lesson.

**Recipient A — medication-reconciliation clinician** (reading the result to
sanity-check an insulin dose):

| `q` | `q-source` | Tolerance | Criticality |
|---|---|---|---|
| Numeric value | OBX-5 | Exact | Safety-critical |
| Units | OBX-6 → UCUM | Normalized (mg/dL ≡ mg/dL, **not** mmol/L) | Safety-critical |
| In/out of range | OBX-7 / OBX-8 flag | Same classification | Decision-affecting |
| Result status | OBX-11 (F vs P) | Exact | Safety-critical |
| Collection time | OBR-7 | Exact | Decision-affecting |
| Patient identity | PID-3 → subject ref | Exact | Safety-critical |

**Recipient B — population quality measure** (counting the patient toward a
diabetes-control denominator):

| `q` | `q-source` | Tolerance | Criticality |
|---|---|---|---|
| Analyte identity vs measure value set | OBX-3 LOINC ∈ value set? | Set-membership | Safety-critical (to the *measure's* validity) |
| Numeric value vs threshold | OBX-5 | Same classification (≤ threshold vs >) | Decision-affecting |
| Patient identity (dedup/attribution) | PID-3 → subject ref | Exact | Safety-critical |
| Date in measurement period | OBR-7 (OBX-14 where populated; the companion reads OBR-7) | Within-period | Decision-affecting |
| Result status | OBX-11 | Exact | Decision-affecting |

The two sets overlap on value, identity, status — and diverge exactly where
the lattice lives. **A** depends on the reference range and abnormal flag;
**B** ignores them entirely and defines its own thresholds. **B** depends on
whether OBX-3's LOINC falls inside the measure's value set — and the sample
message orders `1554-5` (*fasting* glucose) but reports `2345-7` (*generic*
serum glucose), so a measure keyed to fasting glucose may silently drop this
record; **A** never notices the distinction, reading only "glucose, 95, in
range." A transform that discards the reference range is correct for B and
wrong for A; one that normalizes the reported LOINC to the ordered one is
correct for A and *changes B's denominator*. Neither transform is "correct"
without naming the recipient.

## 2. The corpus design checklist

A test corpus has three layers, each catching a different failure class.
Record the provenance of every case: a golden case's expected output encodes
whoever wrote it, and a generated case's realism encodes its generator's
blind spots.

**Layer 1 — curated golden cases** (handcrafted input + expected output):

- [ ] Happy path: one clean case per intended-typical shape.
- [ ] Every criticality-tier field from §1 has a case that exercises it.
- [ ] Boundary values (reference-range edges, zero, negative, max precision).
- [ ] Known-hard real cases from the V2-to-FHIR IG examples and site logs.
- [ ] **Provenance recorded**: who wrote each expected output, against which
      spec version, on what date. Expected outputs inherit the spec's blind
      spots — treat them as reviewed artifacts, not ground truth.

**Layer 2 — generated cases** (Synthea or similar, before mutation):

- [ ] Generation seed and tool version recorded, so the corpus is
      reproducible (an unseeded run yields different "golden" outputs across
      runs — see the failure-mode index).
- [ ] Coverage gaps named: any case class the generator's modules can't
      produce is a hole Layer 3 must fill by hand.

**Layer 3 — controlled mutation** (deliberate defects injected into 1 or 2):

- [ ] Invalid / local codes outside any published map (the OBX-3 `99zzz`
      case).
- [ ] Missing required fields; truncated or malformed dates.
- [ ] Unit mismatches (mg/dL where mmol/L expected).
- [ ] Order/result code divergence (the `1554-5` / `2345-7` case above).
- [ ] Each mutation tagged with the purpose-set question it should break,
      so a mutation that *doesn't* break its target reveals a missing test.

## 3. The property catalog

Organized by the *kind* of arrow, because different transformation shapes
admit different properties. A **metamorphic relation** — a property that
relates the outputs of two related inputs without needing to know either
output in advance — is the escape hatch wherever you cannot compute an
expected output independently (the oracle problem).

**Structural transposition** (field remap, no semantic change):
- Round-trip / lens laws where a `put` exists: `get(put(v,s)) = v` and
  `put(get s, s) = s` (Chapter 31). For ORU→Observation, this is
  `oru→observation` / `observation→oru`.
- Order-invariance metamorphic relation: permuting independent OBX segments
  must not change the extracted result set.

**Terminology translation** (code-system remap):
- Purpose-set preservation: `q(translate(x)) = q-source(x)` for every `q`
  that reads the translated code.
- Round-trip is *not* expected — these are relations, not functions
  (Chapter 34). Assert instead that translation never *invents* a code
  outside the target value set, and flags rather than guesses on a miss.

**Unit conversion**:
- Physical-equivalence metamorphic relation: converting then reading the
  quantity equals reading then converting.
- Idempotence: converting to canonical units twice equals once.

**Aggregation / fold** (events → state, e.g. many ORUs → one Observation
history):
- Replay determinism: same event sequence yields same state.
- Permutation sensitivity is a *positive* test here — reordering events that
  are genuinely ordered (corrections!) *should* change the fold.

**Document extraction** (attested snapshot → resources, e.g. C-CDA → FHIR):
- Narrative/entries agreement is **not** machine-checkable and must route to
  human review (Chapter 31) — record it as a manual gate, not an automated
  property.

## 4. The residue review sheet

The residue is the information the source carries that the transform drops —
and that every purpose set in scope has declared it doesn't need. Making the
residue explicit and reviewable is what separates an engineered transform
from a hopeful one (Chapter 22). Enumerate it; don't discover it in
production.

| Dropped element | Which recipients don't need it | Which recipient *might* | Sign-off |
|---|---|---|---|
| Reference range / abnormal flag | B (quality measure) | A (clinician) — **kept** | |
| Ordering provider (OBR-16) | A, B | Billing / audit — out of scope? | |
| Order vs result LOINC divergence | A | B (denominator) — **flag, don't silently normalize** | |
| Specimen source detail | A, B (for this analyte) | Microbiology workflows — n/a here | |
| Performing-lab identity | A | B (attribution) — confirm | |

A row is safe to drop only when *every* in-scope recipient column says so.
Any row with a "might" is either kept, or the out-of-scope recipient is
recorded as explicitly out of scope, with a name and a date against the
decision. Unsigned residue is a latent incident.
