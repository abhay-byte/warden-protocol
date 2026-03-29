# TODO

This file tracks the current task in progress.

## Active Task

- Status: in progress
- Title: Add AI-generated ending forecasts with structured score adjustments
- Requested by: user
- Requested at: 2026-03-29T23:10:00+05:30
- Goal: Enhance the result screen with an AI-generated, dark long-form ending forecast that uses full run context, returns a strongly structured response, can adjust score, and falls back to the current deterministic ending on timeout or failure.
- Notes:
  - Use the NVIDIA NIM OpenAI-compatible chat endpoint with a strict JSON-only prompt and an example response embedded in the prompt.
  - Keep the API key out of tracked source by loading it from ignored local Gradle properties and exposing only a `BuildConfig` value at build time.
  - Preserve the current deterministic ending as the immediate fallback and show it whenever the AI request fails, times out, or returns unusable data.
- Verification status: `./gradlew :app:assembleDebug` passed, the debug APK was clean-installed and launched on `d30a1726`, and a live NVIDIA NIM JSON-response smoke test succeeded; commit and push pending
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
