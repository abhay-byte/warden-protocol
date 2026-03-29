# UX

## UX Goal

The app presents the player as an active bunker command intelligence, not as a generic menu-driven simulator. The interface leans into an industrial command-console tone with clear telemetry, high-contrast action grouping, and narrative panels that keep the game readable on a phone.

## Implemented Pages

### 1. Main Menu

Purpose:
Launch a new run and surface metagame context before play starts.

Current content:

- Warden core hero panel
- High score badge
- Run-count badge
- Start Mission primary card
- Links to run history and leaderboard
- Latest completed run preview
- Current top settlement preview

### 2. Gameplay / Surface Scan

Purpose:
Evaluate the current settlement target and choose the next command action.

Current content:

- Expandable vault status summary at the top
- Survivors, year, and probe counters
- Current target card with short and optional long field report
- Scanner-gated environment telemetry
- Transit section with route, risk, attrition band, and score penalty
- Probe report and anomaly details when revealed
- Fixed bottom command tray for `Search`, `Probe`, and `Open`

### 3. Event Screen

Purpose:
Resolve a generated vault, surface, cosmic, or apex-threat incident.

Current content:

- Incident title and description
- 2 or 3 choice cards
- Visible known effects
- Explicit unknown-risk warning when applicable

### 4. Event Outcome Screen

Purpose:
Pause after an event decision so the player can absorb the narrative consequence before returning to scanning.

Current content:

- Single report panel with the result narrative
- One action to continue the mission

### 5. Final Outcome Screen

Purpose:
Deliver the colony ending, score reveal, and final telemetry.

Current content:

- Animated outcome hero
- Settlement classification and settlement name
- New-high-score indicator
- Full ending narrative
- Run breakdown with travel and site stats
- Expandable vault diagnostics
- Actions to replay, open leaderboard, or open run history

### 6. Leaderboard

Purpose:
Show the highest-scoring archived colonies.

Current content:

- Summary panel with top entry and ranked run count
- Return to main menu
- Start new mission shortcut
- Ranked list with score, site, survivors, years, and completion date

### 7. Run History

Purpose:
Show a chronological archive of previous runs.

Current content:

- Archived run count
- Return to main menu
- Start new mission shortcut
- Reverse-chronological outcome list with summary text

### 8. Quit Confirmation Modal

Purpose:
Handle Android back navigation without dropping the player out of the app abruptly.

Current content:

- Full-screen styled modal from the main menu
- Dismiss or exit action

## Shared UX Patterns

- Single visual language built around bunker panels, brass/steel tones, and signal accents
- Screen transitions handled through `AnimatedContent`
- Scrollable content areas with a stable action zone on key pages
- Strong badge usage for compact telemetry
- Expand/collapse interactions for long narrative or diagnostic content
- Back button routing to the main menu before quit

## Current UX Strengths

- The core run loop is readable on mobile without hiding key decisions.
- Surface evaluation and travel risk are shown before the irreversible `Open` action.
- Meta screens reuse the same visual grammar, so archive navigation does not feel detached from the game.
- The result screen gives both narrative payoff and mechanical accountability.

## UX Gaps To Address

- There is no onboarding or first-run tutorial for scanners, probes, or archive value.
- The game has no inline explanation for how score is computed.
- Event choices still depend heavily on text density and could use clearer consequence grouping.
- Leaderboard and history are read-only and lack filters, detail drill-down, or comparison tools.
- Accessibility work is still missing for larger text, screen-reader labeling, and stronger non-color cues.

## Future UX Plan

### Near Term

- Add a first-run briefing that teaches the three primary actions and scanner failure behavior.
- Add a compact score-explanation sheet from the outcome screen.
- Surface more explicit warnings before opening the vault on lethal sites.
- Add empty-state illustrations and more contextual copy for archive pages.

### Mid Term

- Add an in-run codex for systems, anomalies, and hostility types.
- Add richer event cards with grouped upside, downside, and uncertainty sections.
- Let players inspect full saved-run details from history and leaderboard rows.
- Add light haptics and optional sound design around major state changes.

### Longer Term

- Add difficulty modes and UI variants tied to campaign tone.
- Add achievement-style progression or unlockable intel logs without weakening the standalone run loop.
- Add tablet-specific layout improvements with a two-pane command interface.
- Add a proper accessibility pass covering scaling, contrast modes, and narration support.
