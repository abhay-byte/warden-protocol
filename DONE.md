# DONE

This file tracks tasks that have been fully completed.

## Completed Tasks

### 2026-03-25 - Surface Descriptions and Transit Penalty

- Status: completed
- Title: Add short and long outside-world descriptions plus transit penalty before opening the vault
- Goal: Make each scanned location explain what the area outside the vault is actually like, and show the journey cost to reach it before the player commits.
- What changed: Added short and expandable long field descriptions for every generated surface location; introduced route, travel time, risk, attrition estimate, and score penalty telemetry on the gameplay screen; applied the travel attrition and score penalty when opening the vault; and carried the transit details into the final outcome breakdown.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

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

### 2026-03-25 - Quit Dialog Restyle

- Status: completed
- Title: Restyle the quit confirmation so it matches the game UI
- Goal: Replace the generic quit confirmation with a game-styled modal that feels like part of the bunker interface while keeping the back flow behavior.
- What changed: Replaced the stock-looking quit prompt with a custom full-screen bunker-themed modal that uses the same cards, colors, iconography, and action buttons as the rest of the interface.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the updated debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Randomized Vault-Opening Casualties

- Status: completed
- Title: Add more variation to how many survivors die when the vault opens
- Goal: Avoid repetitive final death counts like 200 or 300 by making vault-opening casualties vary more while still matching the location hazards.
- What changed: Replaced fixed casualty percentages in the vault-opening calculation with bounded random casualty bands for radiation, dehydration, hostility, and exposure effects.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the updated debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - UI Simplification Pass

- Status: completed
- Title: Simplify the UI for much better at-a-glance clarity
- Goal: Make the interface easier to understand quickly by reducing clutter and applying a clearer visual hierarchy.
- What changed: Simplified the shared chrome, flattened the visual styling, and redesigned the home and gameplay screens so the key information and next action are much easier to scan.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the updated debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.
