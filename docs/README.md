# Docs

This directory is the maintained documentation set for the current `warden-protocol` app.

## What The App Is

The Warden Protocol is a post-nuclear bunker management game for Android. The player acts as the vault command intelligence, balancing survivors, infrastructure health, scanner coverage, archive preservation, and the risk of surface settlement.

## Shipping Scope

- Android app built with Kotlin and Jetpack Compose
- Single-run roguelike structure with local progression through score and archives
- 7 implemented screens
- 16 location archetypes and 300 named site variants
- Travel-risk modeling before settlement
- AI-enhanced ending forecasts with deterministic fallback
- Persistent run history capped at 25 entries
- Persistent top-10 leaderboard derived from saved runs

## Build And Run

### Prerequisites

- Android Studio with Android SDK installed
- JDK 17
- Android device or emulator on API 26+

### Commands

```bash
cd /home/abhay/repos/thewardenprotocol
./gradlew assembleDebug
./gradlew installDebug
```

## Documentation Index

- [`gameplay.md`](/home/abhay/repos/thewardenprotocol/docs/gameplay.md): systems, loop, progression, and scoring model
- [`architecture.md`](/home/abhay/repos/thewardenprotocol/docs/architecture.md): package layout, state flow, and persistence model
- [`ux.md`](/home/abhay/repos/thewardenprotocol/docs/ux.md): implemented pages, interaction patterns, and UX roadmap
- [`design.md`](/home/abhay/repos/thewardenprotocol/docs/design.md): home-screen visual direction based on the Brutalist Relic reference set

## Notes On Accuracy

Several older markdown files in the repository root describe earlier versions of the project. Use the files in `docs/` and the top-level README as the current reference set.
