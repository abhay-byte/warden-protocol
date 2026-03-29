# Home Design

This file documents the current home-screen redesign direction for The Warden Protocol.

## Source Inputs

The updated home screen follows:

- the user-provided reference screenshot
- the supplied HTML mockup structure
- [`~/Downloads/DESIGN.md`](/home/abhay/Downloads/DESIGN.md), which defines the "Brutalist Relic" visual identity

## Design Intent

The home screen should feel like a bunker command console rather than a generic game menu. The implemented direction is built around:

- hard-edged geometry with effectively zero-radius surfaces
- amber-first tactical emphasis with phosphor-green status highlights
- heavy tonal panel stacking instead of visible border-heavy cards
- CRT scanline atmosphere layered over a dark oxidized-steel background
- asymmetrical command-console layout on larger screens
- compact, vertical, dashboard-style composition on mobile

## Implemented Home-Screen Structure

The redesigned Compose home screen is organized into three functional columns on larger layouts and a single stacked command feed on mobile.

### Left Rail

- Vault archive statistics
- High score record
- Successful extraction count
- Casualty rate
- Run history entry point
- Global leaderboard entry point

### Center Column

- Warden Core hero display
- core status telemetry
- top settlement summary
- latest outcome summary
- primary `Start New Mission` action

### Right Rail

- system diagnostics
- broadcast intercept panel
- live tactical overlay panel

### Shared Chrome

- compact top command bar
- global scanline overlay
- mobile bottom command bar

## Implementation Notes

- The redesign is currently localized to the home screen in [`MainMenuScreen.kt`](/home/abhay/repos/thewardenprotocol/app/src/main/java/com/wardenprotocol/game/ui/screen/MainMenuScreen.kt).
- The layout uses code-native Compose drawing instead of imported image assets.
- Existing gameplay, leaderboard, and history flows are preserved.
- Several values shown on the home screen are derived from available local run data. Where the app does not currently store richer analytics, the UI presents the best current approximation.

## Follow-Up Design Work

- Bring the same brutalist relic treatment to leaderboard and history for visual consistency.
- Move shared home-specific tokens into the theme layer if this direction becomes the permanent app-wide visual system.
- Add richer bunker telemetry once the app persists more detailed archive statistics across all completed runs.
