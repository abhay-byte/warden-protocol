# Todo

Items not yet started, sorted by priority.

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
  plan: null
---
