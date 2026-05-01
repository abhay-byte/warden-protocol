# Fastlane Setup

This directory contains [Fastlane](https://fastlane.tools) configuration, metadata, and store-listing assets for **The Warden Protocol**.

> **Note:** Release builds and GitHub releases are currently handled via Gradle and `gh` CLI. The Fastlane setup here is maintained for Google Play deployment and F-Droid metadata management.

---

## Directory Structure

```text
fastlane/
├── Appfile                                  # Google Play package name and JSON key path
├── Fastfile                                 # Fastlane lanes (test, build, deploy, screenshot)
├── Pluginfile                               # Optional Fastlane plugins (commented out)
├── README.md                                # This file
└── metadata/
    └── android/
        └── en-US/
            ├── title.txt                    # App title for stores
            ├── short_description.txt        # 80-char store summary
            ├── full_description.txt         # HTML store description
            ├── video.txt                    # Promo video URL (empty)
            ├── changelogs/
            │   └── 1.txt                    # Changelog for versionCode 1
            └── images/
                ├── featureGraphic.png       # 1024x500 store banner
                ├── 1.png                    # Main Menu
                ├── 2.png                    # Archive Logs / Run History
                ├── 3.png                    # Final Outcome / Chronicle
                ├── 4.png                    # System Config / Settings
                ├── 5.png                    # Surface Scan / Gameplay
                ├── 6.png                    # Event Encounter
                ├── 7.png                    # Event Choices / Protocol Response
                └── 8.png                    # Command Response / Outcome
```

---

## Installation

Fastlane should be installed via Ruby/Bundler:

```bash
cd /path/to/warden-protocol
bundle install
```

If no `Gemfile` exists yet, add one with:

```ruby
source "https://rubygems.org"
gem "fastlane"
```

Then run `bundle install`.

---

## Configuration

### `Appfile`

```ruby
json_key_file("fastlane/google-play-key.json")
package_name("com.ivarna.wardenprotocol")
```

### Environment Variables

Create a `.env` file or export these variables before running deployment lanes:

| Variable | Description | Required For |
|---|---|---|
| `KEYSTORE_PATH` | Path to Java keystore | `build_release`, `deploy_*` |
| `KEYSTORE_PASSWORD` | Keystore password | `build_release`, `deploy_*` |
| `KEY_ALIAS` | Key alias name | `build_release`, `deploy_*` |
| `KEY_PASSWORD` | Key password | `build_release`, `deploy_*` |
| `PLAY_STORE_JSON_KEY` | Google Play service account JSON | `deploy_*` |
| `SLACK_WEBHOOK_URL` | Slack webhook for notifications | `send_slack_notification` |

---

## Available Lanes

| Lane | Command | Description |
|---|---|---|
| `test` | `bundle exec fastlane test` | Run all Android unit tests |
| `build_debug` | `bundle exec fastlane build_debug` | Assemble debug APK |
| `build_release` | `bundle exec fastlane build_release` | Assemble signed release APK |
| `deploy_internal` | `bundle exec fastlane deploy_internal` | Upload to Google Play internal track |
| `deploy_alpha` | `bundle exec fastlane deploy_alpha` | Upload to Google Play alpha track |
| `deploy_beta` | `bundle exec fastlane deploy_beta` | Upload to Google Play beta track |
| `deploy_production` | `bundle exec fastlane deploy_production` | Upload to production (confirms first) |
| `deploy_fdroid` | `bundle exec fastlane deploy_fdroid` | Run lint + test + git tag |
| `increment_version` | `bundle exec fastlane increment_version` | Bump versionName/versionCode and create git tag |
| `screenshot` | `bundle exec fastlane screenshot` | Placeholder for Screengrab setup |
| `send_slack_notification` | `bundle exec fastlane send_slack_notification` | Post deployment summary to Slack |

### Signing for Release Builds

The `build_release` lane passes signing credentials via Gradle properties. Ensure your environment variables are set before running:

```bash
export KEYSTORE_PATH=/path/to/keystore.jks
export KEYSTORE_PASSWORD=your_store_password
export KEY_ALIAS=your_key_alias
export KEY_PASSWORD=your_key_password
bundle exec fastlane build_release
```

---

## Metadata Files

### `title.txt`

Store title (max 50 chars):

```
Warden Protocol: Vault RPG
```

### `short_description.txt`

One-line summary (max 80 chars):

```
Manage a post-apocalyptic vault, scan the surface, and ensure the survival of your archives and people.
```

### `full_description.txt`

HTML-formatted long description for store pages. Currently includes:

- Game premise (Warden / vault / surface)
- Key feature list (strategic management, exploration, AI narratives, aesthetic, events)
- Call to action

### `changelogs/{versionCode}.txt`

Per-version release notes. Example for v1:

```
Initial release of Warden Protocol.
```

---

## Screenshots

All screenshots are captured from a physical Android device at the app's native resolution. They represent the following screens:

| File | Screen | Description |
|------|--------|-------------|
| `1.png` | **Main Menu** | Warden Core hub with start mission, stats, and navigation |
| `2.png` | **Archive Logs** | Chronological run history with outcome cards |
| `3.png` | **Long-Range Chronicle** | AI-generated colony epilogue and timeline |
| `4.png` | **System Config** | Audio settings and core telemetry |
| `5.png` | **Surface Scan** | Gameplay — location evaluation with search/probe/open actions |
| `6.png` | **Event Encounter** | Critical incident with scanner telemetry and narrative |
| `7.png` | **Protocol Response** | Event choice cards with visible and hidden consequences |
| `8.png` | **Command Response** | Outcome filing with narrative result and continue action |

### Screenshot Guidelines

When adding new screenshots:

1. Capture on a **clean device** (no notifications, full battery, clean status bar).
2. Use the **same device model** across all shots for consistency.
3. Export as **PNG** for lossless quality.
4. Follow the naming convention: `1.png`, `2.png`, etc.
5. Update this README when adding or replacing screenshots.

---

## Adding a New Locale

To support additional languages on F-Droid or other stores:

1. Create the locale directory:
   ```bash
   mkdir -p fastlane/metadata/android/{locale}/
   ```
   Replace `{locale}` with a valid Android locale code (e.g., `de-DE`, `fr-FR`, `ja-JP`).

2. Add the required files:
   - `title.txt`
   - `short_description.txt`
   - `full_description.txt`
   - `changelogs/{versionCode}.txt`

3. Optionally add localized screenshots in:
   ```
   fastlane/metadata/android/{locale}/images/
   ```

4. Ensure translations match the tone of the original English copy — the game uses a technical, militarized bunker-console voice.

---

## Adding a New Changelog

When releasing a new version:

1. Note the new `versionCode` from `app/build.gradle.kts`.
2. Create the changelog file:
   ```bash
   touch fastlane/metadata/android/en-US/changelogs/{versionCode}.txt
   ```
3. Write concise, player-facing notes.
4. If applicable, add changelogs for other supported locales.

---

## F-Droid Integration

The project includes F-Droid metadata at the repository root:

```
metadata/com.ivarna.wardenprotocol.yml
```

This file works alongside the Fastlane metadata. It contains:

- Build recipe (`gradle: yes`)
- Source repo and issue tracker links
- Auto-update configuration (`Version` + `Tags`)
- APK signer fingerprint

For full F-Droid contribution guidelines, see:
https://gitlab.com/fdroid/fdroiddata/-/blob/master/CONTRIBUTING.md

---

## Troubleshooting

### Build fails with signing error
- Verify keystore exists at `KEYSTORE_PATH`.
- Check `KEY_ALIAS` matches the key alias in the keystore.
- Ensure passwords are correct.

### Play Store upload fails
- Verify the service account has proper permissions.
- Check the JSON key format is correct.
- Ensure `fastlane/google-play-key.json` exists.

### Fastlane not found
- Run `bundle exec fastlane` instead of `fastlane`.
- Verify Fastlane is in the Gemfile.

---

## Resources

- [Fastlane Documentation](https://docs.fastlane.tools/)
- [F-Droid Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference)
- [F-Droid Contributing Guide](https://gitlab.com/fdroid/fdroiddata/-/blob/master/CONTRIBUTING.md)
- [Google Play Screenshot Guidelines](https://support.google.com/googleplay/android-developer/answer/9866151)
