# TODO

This file tracks the current task in progress.

## Active Task

- Status: in progress
- Title: Map all real game features onto the surface scan page and remove placeholders
- Requested by: user
- Requested at: 2026-03-29
- Goal: Make the surface scan page reflect the actual game loop and data model from the docs and code, including scanner-gated telemetry, transit risk, probe/anomaly intel, bunker status, and real action states, while removing all placeholder content.
- Notes: Reworked `GameScreen.kt` to use live `GameState` and `SurfaceLocation` values, replaced hardcoded telemetry and fake sections, aligned the UI with the documented `Search` / `Probe` / `Open` gameplay loop, and restored the newer custom surface-scan visual style instead of the shared older panel UI.
- Verification status: Gradle build passed and the updated debug APK was installed and launched on device `d30a1726`; commit hash pending
- Planned checks:
  - Build with Gradle
  - Run on device with `adb`
  - Commit changes
  - Push to `origin`

## How We Use This

When you send a new task, I will:

1. Write it into `TODO.md`.
2. Implement the change.
3. Build the app.
4. Run or install it through `adb`.
5. Commit and push the branch.
6. Move the completed item into `DONE.md`.
