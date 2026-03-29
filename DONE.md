# DONE

This file tracks tasks that have been fully completed.

## Completed Tasks

### 2026-03-30 - Unified Hub Music Track

- Status: completed
- Title: Keep the same music across home, archive, and settings pages
- Goal: Prevent the background music from changing while moving between the main menu, archive screens, and settings by routing them through one stable shared hub track.
- What changed:
    - Replaced the separate menu/archive scene split with a single shared hub music scene in `WardenAudioController.kt`.
    - Updated `MainActivity.kt` so the main menu, leaderboard, run history, and settings pages all map to that same hub scene.
    - Kept gameplay, event, and outcome pages on their own scenes, so only the hub navigation flow stays musically stable.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `ff88c26`

### 2026-03-30 - Audio Toggle Fix And Longer Ending Wait

- Status: completed
- Title: Fix SFX-off behavior, randomize music playback, and extend the ending wait timeout
- Goal: Make the SFX setting fully silence button clicks when disabled, make music selection less predictable, raise the ending forecast fallback window to 60 seconds, and add a clearer moving reassurance message on the wait page.
- What changed:
    - Fixed the settings toggle path so turning SFX off immediately disables button sounds instead of forcing one more toggle click through.
    - Updated the scene music controller to choose non-repeating random tracks within each scene playlist instead of stepping through them in a fixed order.
    - Increased the ending forecast timeout in `GameViewModel.kt` from 28 seconds to 60 seconds before falling back to the deterministic result.
    - Added a moving wait-line message to `EndingProcessingScreen.kt` so the player gets a clearer animated “we’ll get back to you, just wait” cue during long result generation.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `b5fb939`

### 2026-03-30 - Richer Static Event Briefings

- Status: completed
- Title: Move event detail into the real static event sources and remove repetitive generated filler
- Goal: Make scan-triggered event descriptions feel richer and more specific by using the actual event source data instead of appending repetitive generic context at runtime.
- What changed:
    - Removed the repeated generic event-level boilerplate that had been appended to every generated event description at runtime.
    - Expanded the static event builder templates in `ExpandedEventCatalog.kt` so each generated vault, surface, cosmic, and apex incident now includes a second sentence tied to its real systems and archive stakes.
    - Kept the dynamic choice-detail expansion, but simplified its wording so it reads more like an operational summary and less like repeated template narration.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `242176d`

### 2026-03-30 - Music, Button SFX, And Event Risk Badge Removal

- Status: completed
- Title: Add live music and sound effects with settings control, and remove event choice risk badges
- Goal: Bring the provided music and button SFX assets into the app, connect them to the existing settings toggles, and remove the visible probability/risk tags from event choice cards.
- What changed:
    - Added a new `WardenAudioController` that drives scene-based music playback and distinct button sounds for primary, secondary, navigation, toggle, and danger actions.
    - Copied the supplied music and button SFX files into `app/src/main/res/raw/` and wired playback through `MainActivity.kt` so the app reacts to the existing settings toggles in real time.
    - Removed the separate risk/probability badge and survivor-risk line from event choice cards so the choice UI is cleaner and relies on the descriptive body text instead.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `4e10dbe`

### 2026-03-30 - Event Choice Card Wrapping Fix

- Status: completed
- Title: Fix cramped wrapping and truncation in the event choice cards
- Goal: Keep the existing event-page styling while making long option titles, risk badges, and detail text render cleanly on narrow mobile screens.
- What changed:
    - Reworked the protocol choice-card header so the risk badge no longer fights the option title for the same horizontal row.
    - Let the option body keep more usable width and improved body text spacing for the expanded event briefings.
    - Stacked the metric lines in a stable vertical group so they stay readable instead of forcing the card into awkward narrow wrapping.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `92b0da8`

### 2026-03-29 - Expanded Static Event Briefings

- Status: completed
- Title: Make scan-triggered event data and choices much easier to understand
- Goal: Expand the static event descriptions and option explanations so players can understand what each event and response means without changing logic, balance, or outcomes.
- What changed:
    - Added a central event-briefing formatter that enriches every generated event with a clearer operational summary based on its category, available protocol count, and hidden-risk count.
    - Expanded every choice description with the authored expectation plus a plain-language summary of the exact survivor, system, archive, and probe changes already contained in the outcome payload.
    - Kept the underlying event IDs, hidden-risk values, outcome maps, and post-choice results unchanged, so this is strictly a readability pass on the static event data.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `ef85f91`

### 2026-03-30 - Animated Ending Wait Screen And Archived Outcome Reopen

- Status: completed
- Title: Add visible ending-processing animation, fix result text wrapping, and reopen archived run reports
- Goal: Make the ending wait page feel actively in progress, stop long result telemetry from truncating, and let saved history and leaderboard entries open their full archived outcome details.
- What changed:
    - Upgraded `EndingProcessingScreen.kt` with visible scan-sweep and progress-rail motion while keeping the same bunker-console styling as the rest of the app.
    - Removed the line clamp from the long result telemetry rows so surface and vault breakdown text wraps fully on the outcome page.
    - Added archived-outcome reopening from both history and leaderboard cards, plus run-data reconstruction so saved runs can restore their detailed outcome report instead of only showing a summary card.
    - Extended locally stored run data with encoded outcome stats and additional forecast fields so future archive entries preserve the full result payload more faithfully.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `f455edf`

### 2026-03-29 - Themed Ending Wait Screen And Full Local Result Persistence

- Status: completed
- Title: Add a properly themed ending-processing page and save the full generated result locally
- Goal: Show a mission-consistent wait page while the long-form ending is being assembled, hide internal provider/review wording from the player-facing result flow, and store the full resolved ending data for completed runs.
- What changed:
    - Added a dedicated `EndingProcessingScreen.kt` that follows the same command-console visual language as the other gameplay screens instead of showing an inline loading block inside the final result page.
    - Updated the result flow so the final outcome screen no longer exposes provider/review/score-adjustment wording while still showing the resolved final score and long-range story output.
    - Extended `RunRecord` and `HighScoreRepository` so completed runs now persist the full narrative, verdict, timeline payload, cause lists, and score delta data locally instead of only the short summary.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `55876a4`

### 2026-03-29 - AI Forecast Timeout Recovery

- Status: completed
- Title: Give the AI ending forecast enough time to answer before falling back
- Goal: Fix the result-screen AI forecast path so the structured NVIDIA NIM response is not abandoned prematurely by overly short client and coroutine timeouts.
- What changed:
    - Increased the HTTP connection/read timeout budget for the forecast request instead of cutting it off after six seconds.
    - Increased the overall coroutine timeout around ending generation so the result flow gives the 70B model a realistic response window.
    - Added a lighter compact-prompt retry before the app gives up and shows the deterministic fallback ending.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `2a782bb`

### 2026-03-29 - AI Ending Forecast System

- Status: completed
- Title: Add AI-generated ending forecasts with structured score adjustment and deterministic fallback
- Goal: Enhance the result screen with a dark, brutally honest, structured long-range ending analysis that uses full run telemetry, can revise score, and safely falls back to the existing deterministic ending if the AI path fails or times out.
- What changed:
    - Added an NVIDIA NIM-backed `AiEndingForecastRepository` that sends the full outcome, location, travel, survivor, archive, and vault-system context through a strict JSON-only prompt with an embedded example response.
    - Extended the outcome pipeline so the result screen first shows the deterministic ending, then upgrades to the structured AI forecast if it returns in time; otherwise it preserves the current ending text and score as the fallback.
    - Updated `OutcomeScreen.kt` to render AI forecast status, score-review reasoning, 10/50/100/final timeline beats, and explicit failure/survival drivers while replacing fake breakdown values with real travel and vault telemetry.
    - Wired the NVIDIA key through ignored local Gradle properties instead of tracked source, and updated the maintained docs to reflect the AI-enhanced ending flow.
- Verification: `./gradlew :app:assembleDebug` succeeded, the debug APK was installed and launched on device `d30a1726` via `adb`, and a live NVIDIA NIM JSON-response smoke test succeeded against `meta/llama-3.3-70b-instruct`.
- Commit: `d203863`

### 2026-03-29 - Archive Run Cards With Location Art

- Status: completed
- Title: Show per-run location images and richer result details in archive history
- Goal: Update saved run metadata and archive UI so previous runs display location-specific imagery plus score, survivors, duration, outcome state, and narrative summary.
- What changed:
    - Extended archived run data so saved entries keep location type, archive grade label, and outcome label alongside the existing run summary fields.
    - Updated the archive and leaderboard cards to render saved location art and the richer run breakdown shown in the new history layout.
    - Added legacy location-type inference from stored location names so older run-history entries can still resolve a likely image even if the original type was not persisted.
    - Reflected the archive-card behavior in `docs/ux.md` and `docs/architecture.md`.
- Verification: `./gradlew :app:assembleDebug` succeeded and the debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `c652c70`

### 2026-03-29 - Warden HUD Unification & Height Bug Resolution

- Status: completed
- Title: Standardized Top Bars and fixed command tray height scaling.
- Goal: Achieve universal Top Bar parity across all mission screens, redesign the Vault Status console, and resolve the 'Towering Tray' layout regression.
- What changed:
    - Standardized Top Bar iconography (Terminal/Tune) and typography (titleMedium/Black) across all 7 mission-critical screens.
    - Redesigned the Vault Status card in `GameScreen.kt` with a 3-column technical layout and live simulated metrics (Health, Radiation mSv, Food Days).
    - Restored the fixed height constraint (112.dp + bottomPadding) to the command tray in `GameScreen.kt`, fixing the vertical stretching bug.
    - Synchronized archival headers for "History" and "Leaderboard" with the core Warden Protocol branding.
- Verification: `./gradlew assembleDebug` succeeded. App installed and verified via `adb`. Pushed to `master`.
- Commit: `91d2fad`

### 2026-03-29 - Outcome Screen (Mission Complete) Redesign

- Status: completed
- Title: Redesign Outcome Screen with premium Brutalist aesthetic
- Goal: Architect a dedicated, high-fidelity game result screen based on the provided "Brutalist Relic" mockup.
- What changed:
    - Implemented a cinematic hero section with a grayscale environmental backdrop and tactical status badges (Mission Complete, New High Score).
    - Created a high-contrast narrative panel ("Post-Settlement Chronicles") for gameplay results.
    - Added integrated diagnostics telemetry for Hull Integrity and Core Temp with critical hazard color-coding.
    - Standardized the core console aesthetic with vertical gradients and CRT scanline/pixel matrix overlays.
- Verification: `./gradlew assembleDebug` succeeded. Pushed to `master`.
- Commit: `4ef7c2a`

### 2026-03-29 - Extract And Map Event Hero Art

- Status: completed
- Title: Extract stitched event art into clean assets and map it to the live event screen
- Goal: Normalize the generated event images from `assets/stitch_main_menu(1).zip`, route live events into 18 shared event image buckets, and replace the event screen placeholder artwork with local game-driven hero art.
- What changed: Extracted the selected event hero images into `assets/event_hero_sources/`, copied normalized `event_<key>.png` files into `app/src/main/res/drawable-nodpi/`, added `EventImageMapping.kt` to resolve live `GameEvent` values by apex prefix, expanded catalog grouping, and keyword routing, exposed expanded event ID sets needed for that mapping, and updated `EventScreen.kt` so the event page now renders local drawable-backed art instead of the remote placeholder image. The supplied archive did not include a distinct `vault_catastrophe` image, so `event_vault_catastrophe.png` currently uses the selected `vault_power_infrastructure` variant as a documented fallback.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `7e95a7c`

### 2026-03-29 - Extract And Map Surface Location Hero Art

- Status: completed
- Title: Extract stitched location art into clean assets and map it to the live surface location categories
- Goal: Normalize the generated location images from `assets/stitch_main_menu.zip`, wire them to the 16 live `LocationType` values used by the surface scan UI, and replace the old generic image buckets with category-specific art.
- What changed: Extracted the selected hero images into `assets/location_hero_sources/`, copied normalized `loc_<category>.png` files into `app/src/main/res/drawable-nodpi/`, removed the old `loc_urban` and `loc_tech` fallback assets, and updated `GameScreen.kt` so each location category resolves to its own hero image. The supplied archive did not include a distinct `CULT_TERRITORY` image, so `loc_cult_territory.png` currently uses the selected military-base variant as a documented fallback.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `2fa9618`

### 2026-03-29 - Surface Scan Gameplay Integration In New UI

- Status: completed
- Title: Map the real game features onto the surface scan page, remove placeholders, and keep the newer custom UI
- Goal: Make the surface scan page reflect the actual gameplay and docs, including scanner-gated telemetry, transit risk, probe and anomaly intel, bunker status, and real action states, without falling back to the older shared panel UI.
- What changed: Reworked `GameScreen.kt` around live `GameState` and `SurfaceLocation` data; replaced hardcoded telemetry, fake transit data, and placeholder sections with real scanner, probe, anomaly, transit, and alert information; and restored the custom surface-scan visual treatment while keeping the new gameplay mapping in that layout.
- Verification: `./gradlew :app:assembleDebug` succeeded and the updated debug APK was installed and launched on device `d30a1726` via `adb`.
- Commit: `1d175c2`

### 2026-03-25 - Result Casualty Wording Fix

- Status: completed
- Title: Fix the ending narration so it does not wrongly say all deaths happened in the vault
- Goal: Make the result screen describe casualties accurately by separating total losses from travel losses instead of mislabeling them as vault-only deaths.
- What changed: Replaced the hard-coded “died in the vault” phrasing in the outcome generator with a casualty summary that reports total losses honestly and explicitly calls out transit deaths when they occurred.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the updated debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Ending Narrative Clarity Pass

- Status: completed
- Title: Rewrite the final result description so it is easier to understand without losing detail
- Goal: Keep the same outcome facts, harshness, and long-term settlement information while making the result screen narration read more clearly and naturally.
- What changed: Rewrote the endgame narrative templates and future epilogues in the outcome generator into shorter, clearer sentence groups, preserving the same information while improving flow, consistency, and readability across success, middling, and failure endings.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the updated debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Outcome Vault Status And Background Cleanup

- Status: completed
- Title: Add expandable vault diagnostics to the result screen and simplify the shared background
- Goal: Let players inspect bunker condition at the end of a run without cluttering the outcome card, and make the overall backdrop feel cleaner and less visually strange.
- What changed: Added a hidden-by-default vault status section to the final run breakdown with compact metrics and expandable full system bars, and reworked the shared backdrop into a calmer command-console background with softer lines and glows.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the updated debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Replace Glassy Home UI With Vault Console Style

- Status: completed
- Title: Redesign the home UI away from glassmorphism into a stronger game-like vault console presentation
- Goal: Make the main menu feel like a proper game interface with a more physical, mechanical vault aesthetic inspired by the reference direction.
- What changed: Reworked the shared chrome with heavier steel/brass panel styling, a more industrial background, and hardware-style buttons; redesigned the home screen around a drawn vault-core hero seal, stronger mission cards, and a more deliberate command-console hierarchy; and kept the implementation code-driven because no image-generation tool was available in this session.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Add 20 Apex Threat Encounters

- Status: completed
- Title: Add 20 hyper-challenging human, AI, and alien encounters with real run-ending potential
- Goal: Introduce a smaller top-tier encounter pool that is materially more lethal and extreme than the standard events.
- What changed: Added 20 new apex-threat encounters centered on warlords, cannibal clans, slave columns, rogue administrative AIs, killer drone swarms, alien harvest ships, mimic entities, and other high-lethality threats; brought the total encounter count to exactly 220; and kept these events tuned to allow catastrophic losses or outright run-ending outcomes.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Expand Encounters to 200

- Status: completed
- Title: Raise the random encounter pool from 40 to 200 with darker and more varied outcomes
- Goal: Add 160 new events so the game has many more dangerous, lucky, brutal, and high-stakes encounters, including some that can end a run.
- What changed: Added a new encounter expansion catalog with 160 unique in-theme events across vault, surface, and cosmic categories; brought the total encounter count to exactly 200; added much more variance in rewards, repairs, losses, and grim choices; and updated event resolution so an encounter that kills the last survivors now properly triggers game over.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Add 100 More Dangerous Locations

- Status: completed
- Title: Add 100 more dangerous named locations to the generator
- Goal: Push the world map further into hostile territory by expanding only the harshest location pools.
- What changed: Added exactly 100 more named locations, concentrated in dangerous categories like ruined cities, military bases, mountain passes, poisoned coasts, radioactive swamps, megacraters, plague zones, scrap heaps, abandoned subways, and cult territory; the total named location pool is now exactly 300.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Expand Surface World to 200 Locations

- Status: completed
- Title: Add more weird, apocalyptic, disgusting, and dangerous locations and raise the total to 200
- Goal: Expand the location generator so the surface world has far more variety and many harsher biome categories.
- What changed: Increased the named location pool from 70 to exactly 200; expanded the existing eight biome lists; added eight new biome types (`RADIOACTIVE_SWAMP`, `MEGACRATER`, `PLAGUE_ZONE`, `SCRAP_HEAP`, `ABANDONED_SUBWAY`, `FUNGAL_WASTES`, `GLASS_DESERT`, and `CULT_TERRITORY`); and wired each new type into world descriptions, travel profiles, and settlement naming.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Compact Command Tray and More Reading Space

- Status: completed
- Title: Make next actions more concise and free more space for the outside-location condition
- Goal: Reduce the height of the fixed gameplay chrome so the player can read the location panel more comfortably.
- What changed: Shortened the collapsed vault summary, tightened its copy and metric blocks, and replaced the large bottom action stack with a compact three-button command tray using concise labels (`Search`, `Probe`, `Open`).
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

### 2026-03-25 - Vault Summary and Fixed Command Tray

- Status: completed
- Title: Move vault systems to the top as a concise expandable summary and pin actions to the bottom
- Goal: Make the gameplay screen easier to read by showing vault status first in compact form and keeping the next actions statically available at the bottom.
- What changed: Reworked the vault panel into a collapsed top summary with core, archive, and critical metrics plus expandable full diagnostics; moved the gameplay layout to a split structure where the middle content scrolls; and pinned the action tray to the bottom with shorter, clearer command labels.
- Verification: `.\gradlew.bat assembleDebug` succeeded and the debug APK was installed and launched on device `192.168.137.30:39355` via `adb`.

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
