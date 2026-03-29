# TODO

This file tracks the current task in progress.

## Active Task

- Status: in progress
- Title: Make the AI ending forecast stop timing out so easily
- Requested by: user
- Requested at: 2026-03-29T23:32:00+05:30
- Goal: Fix the current AI ending timeout path so the result screen gets a fair chance to receive the structured NVIDIA NIM response before falling back to deterministic text.
- Notes:
  - The current forecast path uses a 6.5 second overall coroutine timeout and a 6 second HTTP read timeout, which is too short for a large structured 70B-model response.
  - Increase the timeout budget and add a lighter recovery request before giving up.
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
