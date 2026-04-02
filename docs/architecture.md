# Architecture

## High-Level Shape

The app is a single-activity Compose application. `MainActivity` wires repositories, the game engine, and the `GameViewModel`, then renders one of several full-screen composables based on `UiState`.

## Packages

### `data/model`

Owns the game domain data:

- `GameState`
- `SurfaceLocation`
- `TravelProfile`
- `GameEvent`
- `ColonyOutcome`
- `RunRecord`

`RunRecord` persists the archive-facing snapshot for completed runs, including score, settlement name, location name, location type, duration, survivors, outcome label, grade label, and summary text used by leaderboard and history cards.

### `data/repository`

Owns content and persistence:

- `EventRepository`: base event set plus the expanded catalog
- `ExpandedEventCatalog`: larger generated event pools and apex-threat content
- `AiEndingForecastRepository`: NVIDIA NIM-backed structured ending forecast generation with timeout-safe fallback
- `HighScoreRepository`: DataStore-backed high score and run history storage

### `domain/engine`

- `GameEngine`: location generation, passive decay, event selection, choice resolution, score calculation, and final narrative generation

### `ui/viewmodel`

- `GameViewModel`: accepts `GameAction`, mutates `GameState`, exposes `UiState`, and coordinates persistence on run completion

### `ui/screen`

Full-screen Compose surfaces:

- `MainMenuScreen`
- `MissionIntroScreen`
- `GameScreen`
- `EventScreen`
- `EventOutcomeScreen`
- `OutcomeScreen`
- `LeaderboardScreen`
- `HistoryScreen`

### `ui/component`

Reusable bunker-console building blocks such as:

- `CommandPanel`
- `ActionButton`
- `StatusBadge`
- `VaultStatusPanel`
- `SystemStatusBar`
- `ChoiceButton`
- Shared backdrop and entrance motion helpers

## State Flow

The core runtime path is:

1. `MainActivity` builds `GameViewModel`.
2. `GameApp` collects `gameState`, `uiState`, `highScore`, `leaderboard`, and `runHistory`.
3. A new run initializes `GameState`, generates the first `SurfaceLocation`, and pauses on `UiState.PreRunBriefing`.
4. Screen-level callbacks dispatch `GameAction`.
5. `GameViewModel` calls into `GameEngine` for deterministic game mutations and generated content.
6. On run completion, the deterministic ending is optionally enhanced by the AI ending forecast pipeline.
7. The final resolved result is written to DataStore and then reflected back into leaderboard/history flows.

## Persistence

`HighScoreRepository` stores three user-facing data sets in DataStore Preferences:

- Best score seen so far
- Chronological run history, capped to 25 runs
- Leaderboard derived from the top 10 saved runs by score

There is no cloud sync, account system, or multi-slot save state. Progress is local to the device install.

## Navigation Model

This project does not use a route graph in practice despite having the navigation dependency present. Screen changes are driven directly by the `UiState` sealed class and rendered through `AnimatedContent`.

The main run-start path is now:

1. `MainMenu` / leaderboard / outcome replay dispatches `GameAction.StartNewGame`
2. `GameViewModel` resets `GameState`, generates the first site, and moves to `UiState.PreRunBriefing`
3. `MissionIntroScreen` renders the animated terminal briefing
4. `GameAction.ContinueFromBriefing` moves into `UiState.SurfaceScanning`
