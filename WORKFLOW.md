# Workflow

This repository now uses a simple task ledger based on two markdown files:

- `TODO.md` for the single active task
- `DONE.md` for completed tasks

## Standard Task Cycle

When you give me a task, I will follow this order:

1. Record the task in `TODO.md`.
2. Implement the requested change in code.
3. Build the project with Gradle.
4. Run, install, or verify through `adb` on a connected device or emulator.
5. Commit the changes with Git.
6. Push the commit to `origin`.
7. Move the task from `TODO.md` to `DONE.md`.

## Expected Entry Format

Each task should include:

- title
- requested at
- goal
- notes
- verification status
- commit hash after completion

## Notes

- If `adb` has more than one connected target, I will use the currently reachable default target unless you ask for a specific device.
- If push fails because of network, auth, or branch protection, I will stop and report the blocker instead of marking the task as done.
- If build or device verification fails, I will keep the task in `TODO.md` until it is resolved or you redirect me.
