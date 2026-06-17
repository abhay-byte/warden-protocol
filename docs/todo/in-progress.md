# In Progress

Items currently being worked on.

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
    Goal: Full Russian translation of all data (events, locations, choices, intel, narratives, AI forecast). UI chrome already localized; data layer is hardcoded English in code.

    Root cause:
    - EventScreen.kt:484 — `Text(choice.description)` uses hardcoded English, never routes through `stringResource`
    - EventScreen.kt:131-132 — falls back to `event.title/description` (hardcoded) when `localized*` returns null
    - Location intel (terrain/radiation/water/etc.) in GameEngine.kt:727-797 is `when` returning English strings
    - Route names in GameEngine.kt:60-65+ are hardcoded lists (16 types × 22+ names)
    - Outcome narrativeText in ExpandedEventCatalog.kt:594+ is hardcoded
    - AiEndingForecastRepository.kt:197+ is hardcoded
    - ExpandedEventCatalog.kt:76-99 — systemLabel/archiveLabel hardcoded English

    Scope:
    - GameEngine.kt: ~150 strings (intel lines, route names, travel templates, risk drivers, settlement names)
    - ExpandedEventCatalog.kt: 709 unique strings (event titles, descs, choices, outcomes)
    - EventRepository.kt: 548 unique strings (legacy events)
    - AiEndingForecastRepository.kt: 65 strings

    Approach:
    1. Fix EventScreen.kt:484 — route through `localizedChoiceDescription`
    2. Extend ContentStrings.kt with helpers for intel, routes, outcomes, system labels
    3. Add string keys to values/strings.xml (English source)
    4. Add Russian translations to values-ru/strings.xml
    5. Refactor data classes to hold string keys, resolve at Composable boundary

    Edge cases:
    - Locale switch: existing `recreate()` handles UI; data classes are recomposed
    - Missing key: keep English fallback via `?:` operator
    - City/region proper nouns: keep as proper nouns (do not translate)

    Test plan:
    - Switch to Russian → all visible text is Russian
    - Switch to English → all visible text is English
    - Each surface location: intel lines + route name in Russian
    - Each event: title, desc, all 3 choices, outcome narrative in Russian
    - AI forecast screen: Russian text

    Done when: Russian locale produces 100% Russian UI on all screens, no English fallback visible.
---
