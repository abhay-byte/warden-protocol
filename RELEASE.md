# v1.2.0

## What's Changed

### Features
- feat(i18n): complete Russian translation of UI chrome, surface events, intel/travel/settlement, and refactor location names to use string resources (T1)
- feat(i18n): complete Russian translation of all events (T1)

### Bug Fixes
- fix(i18n): wire 661 hardcoded English strings to R.string resources (T1)

## Items Shipped
- **T1**: Implement i18n (Russian first) with runtime language dropdown in Settings

## Migration Notes
- Per-app language now selectable in Settings. Russian locale is fully translated. Other locales fall back to English.
- Setting language via the new in-app selector does not change system locale.
