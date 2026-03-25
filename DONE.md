# DONE

This file tracks tasks that have been fully completed.

## Completed Tasks

### 2026-03-25 - UI Rehaul, Archives, and Motion Polish

- Status: completed
- Title: Complete UI rehaul for home and gameplay, plus leaderboard and run history
- Goal: Redesign the app with a more immersive game-like bunker interface, add archive screens for leaderboard and previous runs, and polish the presentation with transitions and micro-animations.
- What changed: Reworked the home, gameplay, event, and outcome screens; added shared game chrome and icon-led panels; persisted run history and leaderboard data; added screen transitions, score-based result animations, and small entrance animations across core UI elements; and fixed top status-bar overlap on device.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Expanded Survivor Endings

- Status: completed
- Title: Expand ending narratives with long-term fate of the vault survivors
- Goal: Make endgame outcomes explain what becomes of the vault population after opening the vault, including how long they survive, when they establish civilization, or when they die out.
- What changed: Extended the outcome generator with longer epilogues for every ending tier, so the results now describe later settlement milestones, the years needed to stabilize, generational outcomes, and eventual collapse where applicable.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the updated debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Back Button Flow Fix

- Status: completed
- Title: Intercept Android back button and show quit confirmation from home
- Goal: Make the back button return the player to the home screen first, and only show a quit confirmation from the home screen instead of exiting immediately.
- What changed: Added root-level back handling so non-home screens route back to the main menu, and the home screen now shows a quit confirmation dialog before closing the activity.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the updated debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.
