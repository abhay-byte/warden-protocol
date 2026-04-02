# The Warden Protocol

The Warden Protocol is a single-player Android strategy game built with Kotlin and Jetpack Compose. You act as the bunker intelligence that keeps a thousand survivors alive, scans a ruined surface for settlement targets, resolves brutal incidents, and decides when to open the vault.

## Current State

- Playable end-to-end Android app
- Kotlin + Jetpack Compose + Material 3
- Single-activity app with `StateFlow`-driven screen state
- Local persistence for high score, run history, and top-10 leaderboard
- 8 implemented screens: main menu, pre-run briefing, gameplay, event, event outcome, final outcome, leaderboard, and run history

## Core Gameplay

Each run starts with 1,000 survivors, 3 probes, and a fully operational vault. The game loop is:

1. Receive the animated Warden mission briefing after a run starts.
2. Review a generated surface site.
3. Inspect vault status, scanner output, and travel risk.
4. Choose to search again, deploy a probe, or open the vault.
5. If you keep searching, the vault decays and a random event fires.
6. Resolve the event, absorb the outcome, and scan a new site.
7. Open the vault when the site quality and bunker condition justify the risk.

The current build includes:

- 16 location archetypes
- 300 named surface locations
- Travel routes with time, risk, attrition, and score penalties
- Scanner-gated intel for radiation, water, food, shelter, resources, and threats
- Probe reports with deeper site recommendations
- Animated terminal-style pre-run mission briefing
- Expanded encounter catalog with vault, surface, cosmic, and apex-threat events
- Procedural colony outcome generation with end-of-run telemetry

## Tech Snapshot

- Language: Kotlin
- UI: Jetpack Compose
- Architecture: simple MVVM split across `data`, `domain`, and `ui`
- State: `ViewModel` + `MutableStateFlow`
- Persistence: Jetpack DataStore Preferences
- Android config: min SDK 26, target/compile SDK 36, Java 17

## Project Structure

```text
app/src/main/java/com/wardenprotocol/game/
├── data/
│   ├── model/
│   └── repository/
├── domain/engine/
├── ui/
│   ├── component/
│   ├── screen/
│   ├── theme/
│   └── viewmodel/
├── MainActivity.kt
└── WPApplication.kt
```

## Run Locally

```bash
cd /home/abhay/repos/thewardenprotocol
./gradlew assembleDebug
```

Open the project in Android Studio to run on an emulator or device running Android 8.0+.

## Documentation

The current docs live in [`docs/README.md`](/home/abhay/repos/thewardenprotocol/docs/README.md).

- Product and setup: [`docs/README.md`](/home/abhay/repos/thewardenprotocol/docs/README.md)
- Gameplay systems: [`docs/gameplay.md`](/home/abhay/repos/thewardenprotocol/docs/gameplay.md)
- Architecture and code map: [`docs/architecture.md`](/home/abhay/repos/thewardenprotocol/docs/architecture.md)
- UX inventory and future plans: [`docs/ux.md`](/home/abhay/repos/thewardenprotocol/docs/ux.md)

Legacy planning notes still exist in the repo root, but the `docs/` directory and this README are now the maintained references.
