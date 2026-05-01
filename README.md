<div align="center">

<img src="assets/icon.png" alt="The Warden Protocol" width="180" style="border-radius: 24px; box-shadow: 0 8px 32px rgba(0,0,0,0.3);">

<h1>The Warden Protocol</h1>

<p>
  <b>Post-Apocalyptic Vault Strategy RPG for Android</b><br>
  <i>You are the Warden. A thousand survivors depend on you.</i>
</p>

<p>
  <a href="https://github.com/abhay-byte/warden-protocol/releases/latest">
    <img src="https://img.shields.io/github/v/release/abhay-byte/warden-protocol?color=orange&label=Release&style=for-the-badge&logo=github" alt="Release">
  </a>
  <a href="https://github.com/abhay-byte/warden-protocol/releases/latest">
    <img src="https://img.shields.io/badge/Download-APK-green?style=for-the-badge&logo=android" alt="Download APK">
  </a>
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android" alt="Platform">
</p>

<p>
  <img src="https://img.shields.io/badge/language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/minSDK-26-2E7D32?style=flat-square" alt="Min SDK 26">
  <img src="https://img.shields.io/badge/license-GPL--3.0-blue?style=flat-square" alt="License">
  <img src="https://img.shields.io/github/stars/abhay-byte/warden-protocol?style=flat-square&color=yellow" alt="Stars">
</p>

<br>

<img src="fastlane/metadata/android/en-US/images/featureGraphic.png" alt="Feature Graphic" width="85%">

</div>

---

## Table of Contents

- [About](#about)
- [Screenshots](#screenshots)
- [Features](#features)
- [Gameplay](#gameplay)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

---

## About

**The Warden Protocol** is a single-player post-apocalyptic strategy game for Android where you assume the role of the Warden — an AI bunker intelligence tasked with guiding 1,000 survivors through a devastated world. Scan the radioactive surface for viable settlement locations, manage critical vault systems, resolve deadly incidents, and decide the fate of the last remnants of humanity.

Built with a distinctive **"Brutalist Relic"** aesthetic, the game immerses you in a high-contrast tactical command-console interface that feels like operating a real underground bunker terminal.

> Every run is a roguelike. No two playthroughs are ever the same.

---

## Screenshots

<div align="center">
  <table>
    <tr>
      <td width="25%"><img src="fastlane/metadata/android/en-US/images/1.png" width="100%"></td>
      <td width="25%"><img src="fastlane/metadata/android/en-US/images/5.png" width="100%"></td>
      <td width="25%"><img src="fastlane/metadata/android/en-US/images/6.png" width="100%"></td>
      <td width="25%"><img src="fastlane/metadata/android/en-US/images/7.png" width="100%"></td>
    </tr>
    <tr>
      <td align="center"><sub><b>Warden Core Hub</b></sub></td>
      <td align="center"><sub><b>Surface Scan</b></sub></td>
      <td align="center"><sub><b>Critical Event</b></sub></td>
      <td align="center"><sub><b>Protocol Response</b></sub></td>
    </tr>
    <tr>
      <td width="25%"><img src="fastlane/metadata/android/en-US/images/8.png" width="100%"></td>
      <td width="25%"><img src="fastlane/metadata/android/en-US/images/2.png" width="100%"></td>
      <td width="25%"><img src="fastlane/metadata/android/en-US/images/3.png" width="100%"></td>
      <td width="25%"><img src="fastlane/metadata/android/en-US/images/4.png" width="100%"></td>
    </tr>
    <tr>
      <td align="center"><sub><b>Command Response</b></sub></td>
      <td align="center"><sub><b>Archive Logs</b></sub></td>
      <td align="center"><sub><b>Long-Range Chronicle</b></sub></td>
      <td align="center"><sub><b>System Config</b></sub></td>
    </tr>
  </table>
</div>

---

## Features

<div align="center">

|  | Feature | Description |
|---|---|---|
| :radioactive: | **300 Surface Locations** | 16 archetypes with procedurally generated names and environmental data |
| :satellite: | **Scanner Intel** | Radiation, water, food, shelter, resources, and threat telemetry |
| :rocket: | **Probe System** | Deploy probes to reveal anomaly classifications and structural reports |
| :warning: | **200+ Events** | Vault, surface, cosmic, and apex-threat encounters with hidden consequences |
| :dna: | **AI-Powered Endings** | NVIDIA NIM-enhanced colony chronicles with 10/50/100-year timelines |
| :trophy: | **Leaderboard & History** | Persistent top-10 scores and 25-run archive with detailed telemetry |
| :art: | **Brutalist Relic UI** | CRT scanlines, phosphor terminals, amber tactical accents |
| :memo: | **Narrative Depth** | Procedural epilogues and settlement chronicles for every run |

</div>

---

## Gameplay

Each run begins with **1,000 survivors**, **3 probes**, and a fully operational vault.

```
┌─────────────────────────────────────────────────────────────┐
│  THE WARDEN PROTOCOL — MISSION LOOP                         │
├─────────────────────────────────────────────────────────────┤
│  1. Receive animated terminal mission briefing              │
│  2. Review generated surface site (name, archetype, data)   │
│  3. Inspect vault systems, scanner output, travel risk      │
│  4. CHOOSE: Search Again  |  Deploy Probe  |  Open Vault    │
│  5. If searching → vault decays + random event triggers     │
│  6. Resolve event choices (visible & hidden consequences)   │
│  7. Open vault → calculate casualties, score, and epilogue  │
└─────────────────────────────────────────────────────────────┘
```

### Core Systems

- **12 Vault Systems**: Power grid, food stores, medical bay, security, atmosphere scrubbers, scanners, and more
- **2 Archives**: Cultural and scientific preservation tracked across runs
- **Travel Risk Modeling**: Every site has a route with time, attrition, and score penalties
- **Event Chains**: Multiple events can trigger per search with cascading consequences

---

## Tech Stack

<div align="center">

<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
<img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
<img src="https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3">
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle">

<br><br>

| Layer | Technology |
|-------|------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (`data` / `domain` / `ui`) |
| **State Management** | `ViewModel` + `MutableStateFlow` |
| **Persistence** | Jetpack DataStore Preferences |
| **Navigation** | State-driven `AnimatedContent` |
| **Image Loading** | Coil |
| **AI Integration** | NVIDIA NIM API (with deterministic fallback) |
| **Build System** | Gradle Kotlin DSL |

</div>

### Android Configuration

- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target/Compile SDK**: 36
- **Build Tools**: 36.0.0
- **Java Version**: 17

---

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐    │
│  │  Screen  │ │ Component│ │  Theme   │ │  ViewModel   │    │
│  └──────────┘ └──────────┘ └──────────┘ └──────────────┘    │
└────────────────────────┬─────────────────────────────────────┘
                         │ MutableStateFlow
┌────────────────────────▼─────────────────────────────────────┐
│                     Domain Layer                             │
│                    GameEngine.kt                             │
│   Location Gen · Decay · Events · Scoring · Narrative        │
└────────────────────────┬─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│                      Data Layer                              │
│  ┌─────────────────────────────┐  ┌──────────────────────┐   │
│  │  Model                      │  │  Repository          │   │
│  │  GameState · SurfaceLocation│  │  EventRepository     │   │
│  │  GameEvent · ColonyOutcome  │  │  HighScoreRepository │   │
│  │  RunRecord · TravelProfile  │  │  AiForecastRepo      │   │
│  └─────────────────────────────┘  └──────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

---

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 17
- Android device or emulator running API 26+

### Build & Run

```bash
# Clone the repository
git clone https://github.com/abhay-byte/warden-protocol.git
cd warden-protocol

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build release APK (requires signing config)
./gradlew assembleRelease
```

### Open in Android Studio

1. Open the project root in Android Studio
2. Sync Gradle files
3. Run on an emulator or physical device

---

## Project Structure

```
warden-protocol/
├── app/src/main/java/com/wardenprotocol/game/
│   ├── data/
│   │   ├── model/              # Domain data classes
│   │   └── repository/         # Content & persistence
│   ├── domain/engine/
│   │   └── GameEngine.kt       # Core game logic
│   ├── ui/
│   │   ├── component/          # Reusable composables
│   │   ├── screen/             # 8 full-screen surfaces
│   │   ├── theme/              # Brutalist Relic tokens
│   │   └── viewmodel/
│   │       └── GameViewModel.kt
│   ├── MainActivity.kt
│   └── WPApplication.kt
├── docs/                       # Architecture & design docs
├── fastlane/                   # Store metadata & screenshots
├── metadata/                   # F-Droid build metadata
└── assets/                     # Icon, music, SFX, art sources
```

---

## Documentation

| Document | Description |
|----------|-------------|
| [`docs/gameplay.md`](docs/gameplay.md) | Game systems, loop, progression, and scoring model |
| [`docs/architecture.md`](docs/architecture.md) | Package layout, state flow, and persistence model |
| [`docs/ux.md`](docs/ux.md) | Implemented pages, interaction patterns, and UX roadmap |
| [`docs/design.md`](docs/design.md) | Visual direction — the "Brutalist Relic" aesthetic |
| [`fastlane/README.md`](fastlane/README.md) | Fastlane lanes, metadata, and deployment guide |

---

## Download

<div align="center">

<a href="https://github.com/abhay-byte/warden-protocol/releases/latest">
  <img src="https://img.shields.io/github/downloads/abhay-byte/warden-protocol/total?color=green&label=GitHub%20Downloads&style=for-the-badge&logo=github" alt="GitHub Downloads">
</a>

<br><br>

| Source | Link |
|--------|------|
| **GitHub Release** | [Latest Release](https://github.com/abhay-byte/warden-protocol/releases/latest) |
| **F-Droid** | Coming Soon |

</div>

---

## Contributing

Contributions are welcome! Whether it's bug fixes, new features, translations, or documentation improvements:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

Please read our documentation in the `docs/` directory to understand the codebase before contributing.

---

## Acknowledgments

- **Alexander Ehlers** — Free Music Pack for atmospheric background audio
- **jalastram** — SFX, Button Clicks & Beeps sound library
- **Jetpack Compose Team** — For the incredible UI toolkit
- **F-Droid Community** — For maintaining open-source app distribution

---

<div align="center">

### Your decisions will determine the future of humanity.

*Will you be its savior or its final witness?*

<br>

<a href="https://github.com/abhay-byte/warden-protocol/stargazers">
  <img src="https://img.shields.io/github/stars/abhay-byte/warden-protocol?style=social" alt="Star on GitHub">
</a>

</div>
