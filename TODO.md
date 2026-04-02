# TODO

This file tracks the current task in progress.

## Active Task

- Status: in progress
- Title: Add selectable NVIDIA NIM models in settings and route forecasts through the selected model
- Requested by: user
- Requested at: 2026-04-02
- Goal: Research strong current NVIDIA NIM candidates for this game’s long-form JSON forecast task, add a themed settings dropdown with 8 test models including Llama 4 and Llama 3.3 options, persist the selection, and make the live request use the selected model.
- Notes:
  - Recommended default from current NVIDIA NIM docs: `nvidia/llama-3.3-nemotron-super-49b-v1.5`.
  - Preserved the current custom `qwen/qwen3.5-122b-a10b` option for direct comparison in the selector.
  - Added the model selector without changing the bunker settings visual language.
- Verification status: Release APK and AAB rebuilt successfully. Latest APK installed on device `192.168.137.21:36325`. Latest AAB copied to `/sdcard/Download/app-release.aab`.
- Planned checks:
  - [x] Build with Gradle
  - [x] Run on device with `adb`
  - [ ] Commit changes
  - [ ] Push to `origin`

## How We Use This

When you send me a new task, I will:

1. Write it into `TODO.md`.
2. Implement the change.
3. Build the app.
4. Run or install it through `adb`.
5. Commit and push the branch.
6. Move the completed item into `DONE.md`.
