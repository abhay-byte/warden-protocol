# Quick Start Guide - The Warden Protocol

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 26-35
- Emulator or physical device running Android 8.0+

## Setup Steps

### 1. Open Project
```bash
cd ~/repos/warden_protocol_1
# Open this directory in Android Studio
```

### 2. Sync Gradle
- Android Studio will automatically detect the project
- Click "Sync Now" when prompted
- Wait for Gradle sync to complete

### 3. Run the App
- Select an emulator or connected device
- Click the green "Run" button (▶)
- Or use: `./gradlew installDebug` (if gradlew exists)

## Project Structure Overview

```
warden_protocol_1/
├── app/
│   ├── src/main/
│   │   ├── java/com/wardenprotocol/game/
│   │   │   ├── data/
│   │   │   │   ├── model/           # Data classes
│   │   │   │   └── repository/      # Data sources
│   │   │   ├── domain/
│   │   │   │   └── engine/          # Game logic
│   │   │   ├── ui/
│   │   │   │   ├── component/       # Reusable UI
│   │   │   │   ├── screen/          # Screen composables
│   │   │   │   ├── theme/           # Material3 theme
│   │   │   │   └── viewmodel/       # State management
│   │   │   ├── MainActivity.kt
│   │   │   └── WPApplication.kt
│   │   ├── res/                     # Resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Key Files to Explore

### Game Logic
- **GameEngine.kt** - Core game mechanics, procedural generation, scoring
- **EventRepository.kt** - All 40 game events (1,309 lines)
- **GameViewModel.kt** - State management and action handling

### UI Screens
- **MainMenuScreen.kt** - Animated main menu
- **GameScreen.kt** - Main gameplay interface
- **EventScreen.kt** - Event choices
- **OutcomeScreen.kt** - Final civilization outcome

### Data Models
- **GameState.kt** - Complete game state
- **SurfaceLocation.kt** - Location generation data
- **GameEvent.kt** - Event structure

## Gameplay Instructions

### Starting a Game
1. Launch app
2. Tap "NEW MISSION"
3. View your vault status and first surface location

### During Gameplay
- **OPEN THE VAULT** - End game at current location (calculate outcome)
- **CONTINUE SEARCHING** - Move to next year, face event, find new location
- **DEPLOY PROBE** - Reveal anomaly at current location (limited uses)

### Understanding Systems
- **Green (70-100%)** - Operational
- **Amber (40-69%)** - Degraded
- **Orange (15-39%)** - Critical
- **Red (0-14%)** - Failing

### Scanner Visibility
- Systems below 15% show "? Unknown" for their scan type
- Radiation Scanner → Radiation level
- Water Scanner → Water availability
- Agricultural Scanner → Food potential
- Structure Scanner → Shelter quality
- Resource Scanner → Resource richness
- Threat Assessment → Native hostility

### Events
- Occur between surface scans
- Present 2-3 choices
- Some choices have hidden risks (⚠ Unknown risk)
- Affect survivors, systems, or databases

### Winning
- Higher score = better civilization
- Balance: location quality, survivors, systems, databases
- No "perfect" playthrough - every choice has consequences

## Troubleshooting

### Gradle Sync Issues
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### Missing Gradle Wrapper
```bash
# Generate wrapper
gradle wrapper --gradle-version 8.2
```

### SDK Issues
- Ensure Android SDK 26-35 are installed
- Check SDK location in Android Studio settings

### Compose Issues
- Verify Kotlin plugin version 1.9.20
- Verify Compose Compiler 1.5.4

## Testing the Game

### Quick Test Path
1. Start new game
2. Deploy probe (reveals anomaly)
3. Continue searching (triggers event)
4. Select any choice
5. Continue through 2-3 more cycles
6. Open vault (see outcome)

### Full Test Path
1. Play through 10+ years
2. Let systems degrade
3. Experience multiple event types
4. Try different location types
5. Test high score persistence

## Modifying the Game

### Adding Events
Edit `EventRepository.kt`:
```kotlin
GameEvent(
    id = "your_event_id",
    title = "Event Title",
    description = "Event description...",
    choiceA = EventChoice(...),
    choiceB = EventChoice(...)
)
```

### Adjusting Difficulty
Edit `GameEngine.kt`:
- Change decay rates in `applyPassiveDecay()`
- Modify scoring in `scoreOutcome()`
- Adjust spawn rates in `generateSurfaceLocation()`

### Changing Theme
Edit files in `ui/theme/`:
- `Color.kt` - Color palette
- `Type.kt` - Typography
- `Theme.kt` - Material3 theme

## Performance Notes

- Game state is lightweight (< 1KB)
- No network calls
- No heavy computations
- Smooth 60fps UI
- Minimal battery usage

## Known Limitations

- Single save slot (current game only)
- No cloud sync
- No achievements system
- No sound/music (can be added)
- No multiplayer

## Next Steps

1. Build and run the app
2. Play through a complete game
3. Explore the code
4. Customize events or UI
5. Add your own features

## Support

For issues or questions:
- Check IMPLEMENTATION_SUMMARY.md for technical details
- Review README.md for game overview
- Examine promt.md for original requirements

---

**The game is complete and ready to play!**
