# TODO

This file tracks the current task in progress.

## Active Task

- Status: in progress
- Title: Add per-run location images and richer archived run details
- Requested by: user
- Requested at: 2026-03-29T22:05:00+05:30
- Goal: Update the saved run model and archive/history UI so previous runs show the correct location art and preserve enough run detail to render a richer result card.
- Notes:
  - Extended stored `RunRecord` data with location-type, grade label, and outcome label fields while keeping older saved entries readable.
  - Added shared archive card UI and location-art resolution so history and leaderboard rows can show run-specific site imagery.
  - Added legacy location-type inference from saved location names so older runs can still resolve a likely image when the original type was not stored.
  - Updated archive documentation in `docs/ux.md` and `docs/architecture.md` to reflect the richer saved-run cards.
- Verification status: `./gradlew :app:assembleDebug` passed and the debug APK was clean-installed and launched on device `d30a1726`; commit and push pending
- Planned checks:
  - [x] Build with Gradle
  - [x] Run on device with `adb`
  - [ ] Commit changes
  - [ ] Push to `origin`

## How We Use This

When you send me a new task, I will:

1. Write it into `TODO.md`.
2. Implement the change.
3. Build the app.
4. Run or install it through `adb`.
5. Commit and push the branch.
6. Move the completed item into `DONE.md`.
