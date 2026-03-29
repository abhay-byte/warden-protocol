# TODO

This file tracks the current task in progress.

## Active Task

- Status: in progress
- Title: Add a dedicated ending wait screen and persist the full generated ending data locally
- Requested by: user
- Requested at: 2026-03-29T23:45:00+05:30
- Goal: Show a proper loading/wait page while the long-form ending is being prepared, remove explicit model/review/scoring language from the player-facing result flow, and save the full generated ending data locally with each run.
- Notes:
  - Replace the inline loading panel with a dedicated interstitial ending-processing screen.
  - Remove visible provider/review/score-adjustment wording from the result UI while still applying the final resolved score.
  - Extend local run persistence so the long narrative, verdict, timeline, and related ending fields are stored with the run record.
- Verification status: `./gradlew :app:assembleDebug` passed and the updated debug APK was clean-installed and launched on device `d30a1726`; commit and push pending
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
