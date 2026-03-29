# TODO

This file tracks the current task in progress.

## Active Task

- Status: in progress
- Title: Extract stitched event art and map it to the live event screen
- Requested by: user
- Requested at: 2026-03-29T21:21:00+05:30
- Goal: Normalize the generated event images from `assets/stitch_main_menu(1).zip`, map live events to 18 shared event image keys, and replace the event screen placeholder artwork with local game-driven art.
- Notes:
  - Extracted selected event hero sources into `assets/event_hero_sources/` and copied normalized `event_<key>.png` assets into `app/src/main/res/drawable-nodpi/`.
  - Added shared event image resolution logic so live `GameEvent` values route to 18 image buckets using apex-prefix rules, expanded catalog sets, and keyword matching.
  - Replaced the remote placeholder image on the event page with local drawable-backed artwork.
  - The supplied archive does not include a distinct `vault_catastrophe` image, so `event_vault_catastrophe.png` currently uses the selected `vault_power_infrastructure` variant as a documented fallback.
- Verification status: build passed with `./gradlew :app:assembleDebug` and the debug APK was installed and launched on device `d30a1726`; commit and push pending
- Planned checks:
  - [x] Build with Gradle
  - [x] Run on device with `adb`
  - [ ] Commit changes
  - [ ] Push to `origin`

## How We Use This

When you send a new task, I will:

1. Write it into `TODO.md`.
2. Implement the change.
3. Build the app.
4. Run or install it through `adb`.
5. Commit and push the branch.
6. Move the completed item into `DONE.md`.
