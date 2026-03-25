# Agent Prompt: The Warden Protocol — Android Game (Kotlin + Jetpack Compose)

## Overview

Build a complete, production-ready Android game called **"The Warden Protocol"** — a text-based narrative strategy game inspired by Seedship. The player is an AI managing an underground fallout bunker. You send scouts to the ruined surface, manage vault resources, respond to crises, and ultimately choose when to open the vault doors. The game ends with a procedurally generated civilization outcome.

This game must be fully playable end-to-end with no stubs, no placeholder text, and no TODOs. All logic, data, UI, and narrative must be implemented.

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material Design 3)
- **Architecture:** MVVM + Clean Architecture
- **State:** `StateFlow` + `ViewModel`
- **Navigation:** Compose Navigation (single-activity)
- **Persistence:** `DataStore<Preferences>` for high score only
- **Min SDK:** 26 | **Target SDK:** 35
- **No external game engines** — pure Compose + Canvas

---

## Project Structure

```
com.yourpackage.wardenprotocol/
├── data/
│   ├── model/
│   │   ├── GameState.kt
│   │   ├── VaultSystem.kt
│   │   ├── SurfaceLocation.kt
│   │   ├── GameEvent.kt
│   │   └── ColonyOutcome.kt
│   └── repository/
│       ├── HighScoreRepository.kt
│       └── EventRepository.kt
├── domain/
│   ├── usecase/
│   │   ├── GenerateSurfaceLocationUseCase.kt
│   │   ├── GenerateRandomEventUseCase.kt
│   │   ├── ScoreColonyUseCase.kt
│   │   └── GenerateOutcomeNarrativeUseCase.kt
│   └── engine/
│       └── GameEngine.kt
├── ui/
│   ├── screen/
│   │   ├── MainMenuScreen.kt
│   │   ├── GameScreen.kt
│   │   ├── EventScreen.kt
│   │   ├── SurfaceLocationScreen.kt
│   │   └── OutcomeScreen.kt
│   ├── component/
│   │   ├── VaultStatusPanel.kt
│   │   ├── SystemStatusBar.kt
│   │   ├── ChoiceButton.kt
│   │   └── ScanResultCard.kt
│   ├── theme/
│   │   ├── Theme.kt
│   │   ├── Color.kt
│   │   └── Type.kt
│   └── viewmodel/
│       ├── GameViewModel.kt
│       └── MenuViewModel.kt
├── MainActivity.kt
└── WPApplication.kt
```

---

## Core Data Models

### `GameState.kt`
```kotlin
data class GameState(
    val survivors: Int = 1000,              // starts at 1000, min 0
    val yearsSinceWar: Int = 0,             // increments each surface scan
    val surfaceLocationsScanned: Int = 0,
    val surfaceProbes: Int = 3,             // consumable, used for detailed scans
    val vaultSystems: VaultSystems = VaultSystems(),
    val databases: Databases = Databases(),
    val phase: GamePhase = GamePhase.SURFACE_SCAN
)

enum class GamePhase {
    SURFACE_SCAN,       // evaluating a surface location
    RANDOM_EVENT,       // mid-journey crisis
    OPEN_VAULT,         // player chose to open
    GAME_OVER           // survivors = 0
}

data class VaultSystems(
    val powerGrid: Int = 100,        // 0-100
    val foodStores: Int = 100,
    val medicalBay: Int = 100,
    val securitySystem: Int = 100,
    val constructionGear: Int = 100  // needed to build surface settlement
)

data class Databases(
    val culturalArchive: Int = 100,  // music, history, art
    val scientificArchive: Int = 100 // medicine, engineering, agriculture
)
```

### `SurfaceLocation.kt`
```kotlin
data class SurfaceLocation(
    val name: String,
    val type: LocationType,
    val radiation: RadiationLevel,    // None/Low/Moderate/High/Lethal
    val water: WaterAvailability,     // Abundant/Scarce/None
    val food: FoodPotential,          // Fertile/Marginal/Barren
    val shelter: ShelterQuality,      // Excellent/Good/Poor/None
    val resources: ResourceRichness,  // Rich/Moderate/Poor
    val nativeHostility: Hostility,   // None/Bandits/Warlord/Wasteland Cult
    val anomaly: SurfaceAnomaly?      // optional special feature
)

enum class LocationType {
    RUINED_CITY, FOREST, MILITARY_BASE, FARMLAND,
    UNDERGROUND_RIVER, MOUNTAIN_PASS, COASTAL_TOWN, RESEARCH_FACILITY
}

enum class SurfaceAnomaly {
    SEED_VAULT, FUNCTIONING_REACTOR, ALIEN_SIGNAL,
    SURVIVOR_CAMP, ANCIENT_LIBRARY, CLEAN_WATER_SPRING,
    WEAPONS_CACHE, DISEASE_OUTBREAK_SITE
}
```

### `GameEvent.kt`
```kotlin
data class GameEvent(
    val id: String,
    val title: String,
    val description: String,
    val choiceA: EventChoice,
    val choiceB: EventChoice,
    val choiceC: EventChoice? = null   // some events have 3 options
)

data class EventChoice(
    val label: String,
    val description: String,
    val knownEffect: String,           // what the player is TOLD will happen
    val hiddenRisk: Float = 0f,        // 0.0-1.0 probability of bad outcome
    val outcome: EventOutcome
)

data class EventOutcome(
    val survivorDelta: Int = 0,
    val systemDeltas: Map<String, Int> = emptyMap(),  // "powerGrid" -> -15
    val databaseDeltas: Map<String, Int> = emptyMap(),
    val probesDelta: Int = 0,
    val narrativeText: String
)
```

---

## Vault Systems — 8 Systems Total

Each system has a health value 0-100. Damage is shown as colored status bars:
- **100-70:** Green (Operational)
- **69-40:** Yellow (Degraded)
- **39-15:** Orange (Critical)
- **14-0:** Red (Failing / Destroyed)

| System | Effect When Damaged |
|---|---|
| Power Grid | Increases passive damage to all systems each year |
| Food Stores | Survivors die each year when below 30 |
| Medical Bay | Survivors die during disease events |
| Security System | Faction events more likely; harder to resolve |
| Construction Gear | Colony score penalty at game end |
| Atmosphere Scrubbers | Survivors take damage in vault over time |
| Cultural Archive | Civilization outcome skews toward barbarism |
| Scientific Archive | Civilization outcome skews toward technological regression |

---

## Surface Location Scanning

Each "turn" the player is presented with a new procedurally generated surface location.

The player sees:
1. **Basic scan** (always visible): Location type + name
2. **System-dependent scans** (only shown if system health > 15%):
   - Radiation Scanner → radiation level
   - Water Scanner → water availability  
   - Agricultural Scanner → food potential
   - Structure Scanner → shelter quality
   - Resource Scanner → resource richness
   - Threat Assessment → native hostility
3. **Anomaly** (only visible if probe was used)

### Player Actions on Surface Screen:
- **"Open the Vault"** — End the game and generate outcome based on location + current systems
- **"Continue Searching"** — Move to next year; trigger a random event; return to scanning
- **"Deploy Probe"** (if probes > 0) — Reveals anomaly and upgrades one random scanner temporarily

**Passive decay per "Continue Searching":**
- All vault systems lose 2-8 HP randomly
- `yearsSinceWar += 1`
- If `foodStores < 30`, survivors lose `5-20` per year
- If `powerGrid < 20`, all system decay rate doubles

---

## Random Events — Minimum 40 Unique Events

Events are shown between surface scans. Each requires a choice. Categories:

### Vault Internal Events (15 events)
Examples:
- **Power Surge**: "Fluctuations in the reactor. Reroute power to life support (damage construction gear) or accept rolling blackouts (damage medical bay)?"
- **The Coup**: "A faction within the survivors has seized the food distribution system. Shut them down (risk security damage) or negotiate (lose 20 survivors as they leave)?"
- **The Plague**: "A mutated virus spreading through the sleeping quarters. Quarantine sector (lose 50 survivors) or attempt experimental treatment (risk medical bay damage, chance to cure fully)?"
- **Memory Corruption**: "Archive sectors failing. Save the cultural database (destroy 30% of scientific archive) or the scientific database (destroy 30% of cultural archive)?"
- **The Birth**: "A child has been born in the vault — the first since the war. Allocate extra food (food stores -10) or maintain strict rationing (cultural archive -5, morale event)."
- **Reactor Leak**: "Low-level radiation entering living quarters. Emergency seal (construction gear -15) or gradual filtration (atmosphere scrubbers -10, survivors -5/yr for 3 yrs)."

### Surface/External Events (15 events)
Examples:
- **The Knock**: "Someone is banging on the outer vault door. Open a comm channel (risk security breach) or stay silent (lose chance at survivor intel)."
- **The Signal**: "A radio transmission detected — Morse code SOS 40 km north. Send a scout team (probes -1, chance of: supplies gained OR scouts lost) or ignore."
- **Acid Rain Season**: "A particularly toxic storm season is forecast. Reinforce roof drainage (construction gear -10) or accept surface contamination (water scanner -20)."
- **The Scavenger**: "A lone survivor has found your ventilation exhaust. They know you're here. Invite them in (survivors +1, but they carry disease risk) or drive them away (security event)."

### Cosmic / Weird Events (10 events)
Examples:
- **The Broadcast**: "An automated AI broadcast from a pre-war government satellite. It offers navigation data for unknown coordinates. Follow it (risk, high reward) or ignore."
- **Underground River**: "Seismic sensors detect a subterranean water source below your vault. Drill for it (construction gear -20, chance of abundant water) or leave it."

---

## Procedural Outcome Generation

When the player opens the vault, score and generate a narrative.

### Scoring Formula
```
Base score = survivors × 10
+ (location radiation penalty: None=+500, Low=+200, Moderate=0, High=-300, Lethal=-800)
+ (location water bonus: Abundant=+400, Scarce=+100, None=-400)
+ (location food bonus: Fertile=+300, Marginal=+100, Barren=-300)
+ (location shelter: Excellent=+200, Good=+100, Poor=0, None=-200)
+ (location resources: Rich=+250, Moderate=+100, Poor=0)
+ (constructionGear/100 × 300)      // settlement quality
+ (culturalArchive/100 × 200)
+ (scientificArchive/100 × 200)
- (nativeHostility penalty: None=0, Bandits=-150, Warlord=-400, Cult=-300)
+ (anomaly bonus if beneficial)
```

### Outcome Narrative Builder
Generate a paragraph using template selection based on score ranges and key stat combinations:

**Society Type** (determined by cultural/scientific archive ratios):
- High culture + high science → "enlightened democratic republic"
- High culture + low science → "artistic but fragile theocracy"
- Low culture + high science → "efficient but cold technocracy"
- Low culture + low science → "superstitious tribal society"
- Both destroyed → "survivors descended to pre-literate tribalism"

**Economic Structure** (determined by resources + construction):
- Rich resources + good construction → "industrial prosperity"
- Rich resources + poor construction → "resource-dependent feudalism"  
- Poor resources + good construction → "innovative recycling economy"
- Poor resources + poor construction → "subsistence survival"

**Political Structure** (determined by security system + faction events):
- High security + no coups → "stable meritocracy"
- High security + coup → "authoritarian police state"
- Low security + many survivors → "chaotic democracy"
- Low security + few survivors → "warlord territory"

**Environmental Relationship** (determined by radiation + food + water):
- Good all three → "harmony with the renewed land"
- Mixed → "constant struggle against the poisoned earth"
- Bad all three → "underground civilization that never sees sunlight"

**Final narrative** = [survivors count] survivors emerged from Vault [random number] in Year [yearsSinceWar] after the war. They built [settlement name] on [location name]. [2-3 sentences describing society type + economy + political structure + environment]. [1 sentence monument to the dead]. [Civilization score].

---

## UI Design

### Color Palette (Dark Theme Only)
```
Background: #0A0A0A (near black)
Surface: #141414
Vault Green: #00FF88   (operational systems)
Warning Amber: #FFB300  (degraded systems)
Critical Orange: #FF6D00 (critical systems)
Danger Red: #F44336    (failing systems)
Text Primary: #E8E8E8
Text Secondary: #9E9E9E
Accent: #4CAF50 (green — hope motif)
```

### Typography
- Use `JetBrains Mono` or `Courier Prime` for system readout text (scanner values, stats)
- Use a clean sans-serif (e.g., `Inter` or default MD3 font) for narrative text
- Title font: bold serif or stencil-style for "The Warden Protocol" branding

### Main Menu Screen
- Full-screen dark background with subtle scanline overlay (Compose Canvas)
- Animated vault door graphic (simple concentric circles that pulse slowly)
- Title: "THE WARDEN PROTOCOL" in military stencil style
- Subtitle: *"The last light of humanity sleeps below. You are its guardian."*
- Buttons: NEW MISSION | CONTINUE (if save exists) | HIGH SCORES | ABOUT

### Game Screen Layout (Portrait)
```
┌─────────────────────────────┐
│  YEAR: 12    SURVIVORS: 847 │  ← top status bar
├─────────────────────────────┤
│                             │
│   [VAULT STATUS PANEL]      │  ← 8 system bars with labels
│   Power Grid    ████░░ 68%  │
│   Food Stores   ██░░░░ 40%  │
│   Medical Bay   █████░ 82%  │
│   ...                       │
├─────────────────────────────┤
│                             │
│   [SURFACE LOCATION CARD]   │  ← main content area
│   Ruined City: "New Haven"  │
│   Radiation:  ⚠ Moderate    │
│   Water:      ✓ Abundant    │
│   Food:       ✓ Fertile     │
│   Shelter:    ✓ Good        │
│   Resources:  ? Unknown     │  ← scanner too damaged
│   Threats:    ✗ Warlord     │
│   Anomaly:    [Deploy Probe]│
│                             │
├─────────────────────────────┤
│  [OPEN VAULT] [SEARCH MORE] │  ← action buttons
│  [DEPLOY PROBE (2 left)]    │
└─────────────────────────────┘
```

### Surface Status Colors
- Good value → Green text with ✓ icon
- Marginal value → Amber text with ⚠ icon
- Bad value → Red text with ✗ icon
- Unknown (scanner damaged) → Grey text with ? icon

### Event Screen
- Covers full screen with a dark modal-style card
- Large event title in bold
- 3-4 lines of atmospheric narrative text
- 2-3 choice buttons with:
  - Bold choice label
  - Small description of known effect
  - "⚠ Unknown risk" indicator if the choice has a hidden risk

### Outcome Screen
- Slow reveal animation (fade in paragraph by paragraph)
- Score displayed prominently with label (e.g., "THRIVING CIVILIZATION — 8,420 pts")
- Civilization classification badge (e.g., "Enlightened Republic")
- Full procedural narrative text
- Share button (plain text share intent)
- "Play Again" + "Main Menu" buttons
- High score highlight if beaten

---

## Animation Requirements

- **System bars**: Animate health value changes with `animateIntAsState` over 600ms
- **Survivor count**: `animateIntAsState` when value drops
- **Event card**: Slide up from bottom with `AnimatedVisibility` + `slideInVertically`
- **Outcome text**: Fade in each paragraph with 400ms delay between each using `LaunchedEffect` + delay
- **Vault pulse**: Infinite repeatable animation on main menu using `rememberInfiniteTransition`
- **Screen transitions**: `fadeIn` / `fadeOut` between all screens

---

## Game Loop Summary

```
Main Menu
    ↓ [New Game]
Initialize GameState
    ↓
Generate Surface Location
    ↓
Show Surface Location Screen
    ├── [Deploy Probe] → reveal anomaly, return to scan
    ├── [Open Vault] → go to Outcome Screen
    └── [Search More] ─────────────────────────────────┐
                                                        ↓
                                              Apply passive decay
                                              yearsSinceWar++
                                              Check: survivors <= 0? → Game Over
                                                        ↓
                                              Generate Random Event
                                                        ↓
                                              Show Event Screen
                                                        ↓
                                              Player picks choice
                                                        ↓
                                              Apply outcome deltas
                                              Show outcome narrative
                                                        ↓
                                              Generate new Surface Location
                                                        ↓
                                              Return to Surface Location Screen ←┘
```

---

## Implementation Requirements

### GameEngine.kt
Implement all game logic here (pure Kotlin, no Android dependencies):
- `generateSurfaceLocation(): SurfaceLocation` — weighted random generation
- `generateEvent(state: GameState): GameEvent` — pick from event pool, filtered by state
- `applyEventChoice(state: GameState, choice: EventChoice): GameState` — apply deltas + hidden risk rolls
- `applyPassiveDecay(state: GameState): GameState` — yearly degradation
- `scoreOutcome(state: GameState, location: SurfaceLocation): Int`
- `generateOutcomeNarrative(state: GameState, location: SurfaceLocation, score: Int): ColonyOutcome`

### GameViewModel.kt
- Expose `StateFlow<GameState>` and `StateFlow<UiState>`
- Handle all user actions as sealed class `GameAction` events
- Use `viewModelScope` for coroutines
- Never expose mutable state directly

### Event Pool
Implement all 40+ events as a hardcoded list in `EventRepository.kt`. Each event must have:
- Unique ID
- Full narrative text (2-4 sentences, atmospheric, written in second-person: "The sensors detect...")
- Two choices minimum, three where appropriate
- At least one choice with a hidden risk element

### Procedural Location Names
Generate location names procedurally:
- Ruined City: [adjective] + [pre-war city fragment] e.g., "Flooded Detroit", "Silent Chicago Ruins"
- Forest: [color/condition] + [forest type] e.g., "Ash-Grey Pinelands", "Recovering Oak Valley"
- Military Base: "Fort [random NATO phonetic]" e.g., "Fort Zulu", "Outpost Kilo-7"
- etc.

---

## Deliverables

Produce the complete Android project with:

1. All Kotlin source files — no stubs, no TODOs
2. `build.gradle.kts` (app + project level) with all dependencies
3. `AndroidManifest.xml`
4. `res/` directory with:
   - `strings.xml` (all user-facing strings)
   - `themes.xml` (MD3 dark theme)
   - App icons (adaptive icon XML)
5. Full 40-event pool implemented in `EventRepository.kt`
6. Complete procedural narrative system producing varied, non-repetitive outcome text
7. Functional high score persistence via DataStore
8. Game playable from New Game through to Outcome screen with at least 5 distinct civilization outcome tiers

Do not use any lorem ipsum text. All narrative text must be final, atmospheric, and thematically consistent with the post-nuclear setting.
