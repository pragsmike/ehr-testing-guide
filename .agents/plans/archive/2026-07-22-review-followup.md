# Plan: Review Follow-up — 2026-07-22

<!-- Produced by the 2026-07-22 follow-up editorial review
     (.agents/reviews/2026-07-22-review.md). Two small items; both
     are mechanical with verbatim text supplied, sized for one short
     Code integration session or hand edits. Item 1 touches a
     review-status chapter (21) — the edits are confined to one
     stale sentence and one identifier inside its listing; no other
     prose changes are authorized. Statuses: todo / in-progress /
     blocked / done / dropped. Close and archive per the
     .agents/plans/archive/ convention when done. -->

**Status: CLOSED (2026-07-22). Both items done — item 2 by author
hand edit, item 1 this session.**
**Source review:** `.agents/reviews/2026-07-22-review.md`
(findings F1–F3).

---

## 1. Chapter 21: stale stub claim + phantom identifier — done

The property the chapter describes landed with chapter 23's
companion work; the prose still says it is stubbed, and the listing
binds a generator name that does not exist in the companion
(`gen-oru`; the real def is `gen-parsed-oru` in
`companion/src/ehr_testing/properties.clj`). Two replacements in
`manuscript/20-part-2-the-method/21-what-correct-means.md`:

1. Replace the sentence
   "In the companion, a purpose-set row is a property. The shape —
   currently stubbed in `ehr-testing.properties` and promoted to a
   real test as this chapter lands — is:"
   with
   "In the companion, a purpose-set row is a property, live and
   tested in `ehr-testing.properties`, where `property-for-row`
   builds one such property from each row. The shape is:"
2. In the listing, replace
   "(prop/for-all [oru gen-oru]"
   with
   "(prop/for-all [oru gen-parsed-oru]"

The chapter's HTML sync comment retains its original wording per
the leave-after-payment convention — do not edit it. Fuller
alignment of the listing with the companion's exact form
(namespace-qualified calls) is deliberately NOT done: the listing
shows the shape, and with the identifier fixed every name in it now
exists in the companion.

**Acceptance:** `clojure -X:test` green (no companion change, so
counts unchanged); flattened-newline grep of `build/book.md` finds
"live and tested in" and "gen-parsed-oru" and does NOT find
"currently stubbed in `ehr-testing.properties`" outside HTML
comments; `git diff` on the chapter shows exactly two hunks.

## 2. editorial-review SKILL.md: one-space nit — done

In `.agents/skills/editorial-review/SKILL.md`, procedure step 2,
replace
"(skip`archive/`"
with
"(skip `archive/`"
(One character. Suitable for a hand edit folded into the next
commit; listed here so the finding has a tracked disposition.)

**Acceptance:** grep for "(skip `archive/`" matches; grep for
"(skip`archive/`" does not.

## Completion

Close when both items are done, then move this file to
`.agents/plans/archive/`. No follow-up review is required for a
plan of this size; the changes will be verified by the next
scheduled review in the ordinary course.
