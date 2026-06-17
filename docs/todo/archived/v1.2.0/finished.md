# Finished

Items completed in current cycle.

---
- id: T1
  title: Implement i18n (Russian first) with runtime language dropdown in Settings
  type: feature
  priority: high
  difficulty: hard
  why: User-facing request — game has zero translations. English-only blocks non-English speakers.
  really_needed: Yes — out of 13.6k LOC, only `app_name` is in strings.xml. All other text is hardcoded English inline in Kotlin.
  impact: settings UI, all screens, all components, all data repositories (events/locations/outcomes), build config, manifest
  followups: T2 (additional locales), T3 (AI prompt localization tuning)
  images: null
  github_ref: null
  plan: |
    1. Add Russian locale config (locales_config.xml + AndroidManifest)
    2. Add LocaleStore + LocaleApplier
    3. Add per-app language dropdown in Settings
    4. Add 3300+ Russian translations to values-ru/strings.xml
    5. Refactor hardcoded English literals in 3 Kotlin files (EventRepository, ExpandedEventCatalog, GameEngine) to use context.getString() at runtime
    6. Convert ExpandedEventCatalog from `object` to `class` with context
    7. Add context parameter to EventRepository constructor
    8. Thread context through MainActivity

    Result: 661 string replacements wired to R.string resources; runtime locale switching works; Russian locale produces fully Russian UI.
---
