# Menu And Intro Design

This file documents the current menu and pre-run intro design direction for The Warden Protocol.

## Source Inputs

The updated home screen follows:

- the user-provided reference screenshot
- the supplied HTML mockup structure
- [`~/Downloads/DESIGN.md`](/home/abhay/Downloads/DESIGN.md), which defines the "Brutalist Relic" visual identity

## Design Intent

The home and mission-intro flow should feel like a bunker command console rather than a generic game menu. The implemented direction is built around:

- hard-edged geometry with effectively zero-radius surfaces
- amber-first tactical emphasis with phosphor-green status highlights
- heavy tonal panel stacking instead of visible border-heavy cards
- CRT scanline atmosphere layered over a dark oxidized-steel background
- asymmetrical command-console layout on larger screens
- compact, vertical, dashboard-style composition on mobile

## Pre-Run Briefing Direction

The mission intro screen extends the same visual system but narrows it into one dominant idea: a live terminal directive coming online before the first scan.

That screen should feel like:

- a full-screen phosphor terminal rather than a dashboard of cards
- stronger scanline and sweep motion than the home screen
- sequential copy reveal instead of static paragraphs
- a controlled handoff from archive/meta space into mission space
- one clear bottom action that is visible immediately, even while the briefing text is still unfolding

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
- The pre-run terminal briefing is implemented in [`MissionIntroScreen.kt`](/home/flux/repos/warden-protocol/app/src/main/java/com/wardenprotocol/game/ui/screen/MissionIntroScreen.kt).
- The layout uses code-native Compose drawing instead of imported image assets.
- Existing gameplay, leaderboard, and history flows are preserved, with the new intro page inserted between run start and the first surface scan.
- Several values shown on the home screen are derived from available local run data. Where the app does not currently store richer analytics, the UI presents the best current approximation.

## Follow-Up Design Work

- Bring the same brutalist relic treatment to leaderboard and history for visual consistency.
- Move shared home-specific tokens into the theme layer if this direction becomes the permanent app-wide visual system.
- Add richer bunker telemetry once the app persists more detailed archive statistics across all completed runs.
