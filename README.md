<p align="center">
  <img src="assets/icon.png" alt="The Warden Protocol" width="160">
</p>

<h1 align="center">The Warden Protocol</h1>

<p align="center">
  <b>A single-player Android strategy game built with Kotlin and Jetpack Compose.</b><br>
  You are the Warden. A thousand survivors depend on you.
</p>

<p align="center">
  <a href="https://github.com/abhay-byte/warden-protocol/releases/latest">
    <img src="https://img.shields.io/github/release/abhay-byte/warden-protocol.svg?logo=github" alt="GitHub Release">
  </a>
  <a href="https://github.com/abhay-byte/warden-protocol/blob/master/LICENSE">
    <img src="https://img.shields.io/badge/license-GPL--3.0--or--later-blue.svg" alt="License">
  </a>
  <img src="https://img.shields.io/badge/platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/min--sdk-26-orange.svg" alt="Min SDK">
</p>

---

<p align="center">
  <img src="fastlane/metadata/android/en-US/images/featureGraphic.png" alt="Feature Graphic" width="100%">
</p>

## What Is This?

**The Warden Protocol** is a post-apocalyptic vault management and surface exploration strategy game for Android. You act as the bunker intelligence — the Warden — tasked with keeping 1,000 survivors alive while scanning a ruined surface for viable settlement targets, resolving brutal incidents, and deciding exactly when to open the vault.

Every run is a roguelike: no two playthroughs are the same. The game combines procedural generation with narrative depth, delivering a tactical command-console experience with a distinctive "Brutalist Relic" aesthetic.

---

## Screenshots

<table>
  <tr>
    <td><img src="fastlane/metadata/android/en-US/images/1.png" alt="Main Menu" width="100%"></td>
    <td><img src="fastlane/metadata/android/en-US/images/5.png" alt="Gameplay" width="100%"></td>
    <td><img src="fastlane/metadata/android/en-US/images/6.png" alt="Event" width="100%"></td>
    <td><img src="fastlane/metadata/android/en-US/images/7.png" alt="Event Choices" width="100%"></td>
  </tr>
  <tr>
    <td align="center"><b>Main Menu</b></td>
    <td align="center"><b>Surface Scan</b></td>
    <td align="center"><b>Event Encounter</b></td>
    <td align="center"><b>Protocol Response</b></td>
  </tr>
  <tr>
    <td><img src="fastlane/metadata/android/en-US/images/8.png" alt="Outcome" width="100%"></td>
    <td><img src="fastlane/metadata/android/en-US/images/2.png" alt="Run History" width="100%"></td>
    <td><img src="fastlane/metadata/android/en-US/images/3.png" alt="Final Outcome" width="100%"></td>
    <td><img src="fastlane/metadata/android/en-US/images/4.png" alt="Settings" width="100%"></td>
  </tr>
  <tr>
    <td align="center"><b>Command Response</b></td>
    <td align="center"><b>Archive Logs</b></td>
    <td align="center"><b>Long-Range Chronicle</b></td>
    <td align="center"><b>System Config</b></td>
  </tr>
</table>

---

## Core Gameplay

Each run starts with **1,000 survivors**, **3 probes**, and a fully operational vault.

### The Loop

1. **Receive the animated Warden mission briefing** after a run starts.
2. **Review a generated surface site** — 16 archetypes, 300 named locations, each with unique descriptions and environmental data.
3. **Inspect vault status**, scanner output, and travel risk.
4. **Choose your action**: Search again, deploy a probe, or open the vault.
5. **If you keep searching**, the vault decays and a random event fires.
6. **Resolve the event**, absorb the outcome, and scan a new site.
7. **Open the vault** when the site quality and bunker condition justify the risk.

### What's In The Box

| Feature | Detail |
|---------|--------|
| **Location Archetypes** | 16 distinct types (Research Facility, Military Bunker, Underground City, etc.) |
| **Named Sites** | 300 procedurally named surface locations |
| **Travel System** | Routes with time, risk, attrition, and score penalties |
| **Scanner Intel** | Radiation, water, food, shelter, resources, and threat assessment |
| **Probe Reports** | Deep-dive anomaly classification and structural telemetry |
| **Event Catalog** | 200+ events across vault, surface, cosmic, and apex-threat categories |
| **Colony Outcomes** | Procedural ending generation with 10/50/100-year timeline and AI-enhanced forecasts |
| **Persistence** | Local high score, run history (25 entries), and top-10 leaderboard |

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (`data` / `domain` / `ui`) |
| **State Management** | `ViewModel` + `MutableStateFlow` |
| **Persistence** | Jetpack DataStore Preferences |
| **Navigation** | State-driven `AnimatedContent` (no route graph) |
| **Image Loading** | Coil |
| **AI Integration** | NVIDIA NIM API for structured ending forecasts (with deterministic fallback) |
| **Build System** | Gradle (Kotlin DSL) |

### Android Configuration

- **Min SDK**: 26 (Android 8.0)
- **Target/Compile SDK**: 36
- **Build Tools**: 36.0.0
- **Java Version**: 17

---

## Project Structure

```text
app/src/main/java/com/wardenprotocol/game/
├── data/
│   ├── model/           # GameState, SurfaceLocation, GameEvent, ColonyOutcome, RunRecord
│   └── repository/      # EventRepository, HighScoreRepository, AiEndingForecastRepository
├── domain/engine/
│   └── GameEngine.kt    # Location gen, decay, event selection, scoring, narrative
├── ui/
│   ├── component/       # Reusable bunker-console composables
│   ├── screen/          # 8 full-screen surfaces (menu, briefing, gameplay, events, outcomes, etc.)
│   ├── theme/           # Brutalist Relic visual tokens
│   └── viewmodel/
│       └── GameViewModel.kt
├── MainActivity.kt
└── WPApplication.kt
```

---

## Run Locally

### Prerequisites

- Android Studio with Android SDK installed
- JDK 17
- Android device or emulator on API 26+

### Build

```bash
# Debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Release APK (requires signing config)
./gradlew assembleRelease
```

### Open in Android Studio

1. Open the project root in Android Studio.
2. Sync Gradle.
3. Run on an emulator or physical device running Android 8.0+.

---

## Download

- **Latest Release**: [GitHub Releases](https://github.com/abhay-byte/warden-protocol/releases/latest)
- **F-Droid**: Coming soon — metadata prepared for inclusion.

---

## Documentation

| Document | Description |
|----------|-------------|
| [`docs/README.md`](docs/README.md) | Product overview and setup |
| [`docs/gameplay.md`](docs/gameplay.md) | Game systems, loop, progression, and scoring model |
| [`docs/architecture.md`](docs/architecture.md) | Package layout, state flow, and persistence model |
| [`docs/ux.md`](docs/ux.md) | Implemented pages, interaction patterns, and UX roadmap |
| [`docs/design.md`](docs/design.md) | Visual direction — the "Brutalist Relic" aesthetic |
| [`fastlane/README.md`](fastlane/README.md) | Fastlane setup, lanes, and deployment guide |

---

## Design Philosophy

The Warden Protocol is built around a **"Brutalist Relic"** visual identity:

- Hard-edged geometry with effectively zero-radius surfaces
- Amber-first tactical emphasis with phosphor-green status highlights
- Heavy tonal panel stacking instead of border-heavy cards
- CRT scanline atmosphere over a dark oxidized-steel background
- Asymmetrical command-console layout on larger screens
- Compact, vertical, dashboard-style composition on mobile

The interface should feel like a live bunker command terminal — not a generic game menu.

---

## License

This project is licensed under the **GPL-3.0-or-later** License. See [LICENSE](LICENSE) for details.

---

<p align="center">
  <i>Your decisions will determine the future of humanity.<br>
  Will you be its savior or its final witness?</i>
</p>
