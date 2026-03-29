# TODO

This file tracks the current task in progress.

## Active Task

- Status: in progress
- Title: Extract stitched location art and map it to live surface location categories
- Requested by: user
- Requested at: 2026-03-29
- Goal: Extract the generated location art from `assets/stitch_main_menu.zip`, normalize it into usable app assets, and map the images to the live `LocationType` categories used by the surface scan UI.
- Notes: Extracted the selected hero art into `assets/location_hero_sources/`, copied normalized `loc_<category>.png` files into `app/src/main/res/drawable-nodpi/`, removed the old generic urban and tech fallback images, and updated the surface scan image mapping to use per-category assets. The supplied archive did not contain a distinct `CULT_TERRITORY` image, so `loc_cult_territory.png` currently uses the selected military-base variant as a documented fallback.
- Verification status: `./gradlew :app:assembleDebug` passed and the updated debug APK was installed and launched on device `d30a1726`; commit hash pending
- Planned checks:
  - Build with Gradle
  - Run on device with `adb`
  - Commit changes
  - Push to `origin`

## How We Use This

When you send a new task, I will:

1. Write it into `TODO.md`.
2. Implement the change.
3. Build the app.
4. Run or install it through `adb`.
5. Commit and push the branch.
6. Move the completed item into `DONE.md`.
