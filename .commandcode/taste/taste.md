# workflow
- Follow WORKFLOW.md and complete the full cycle each time. Confidence: 0.75
- Commit and push all changes including assets (music, sound effects, images). Confidence: 0.70

# security
- Never commit API keys to git; ensure gradle properly excludes sensitive files via gitignore. Confidence: 0.75

# ai-integration
- Use NVIDIA NIM API for AI features (narrative generation, scoring). Base URL: https://integrate.api.nvidia.com/v1. Confidence: 0.80
- Use mistralai/mistral-small-4-119b-2603 model with reasoning_effort=high, max_tokens=16384, temperature=0.10. Confidence: 0.75

# tone
- Game narrative must be brutal, honest, and dark in tone. Confidence: 0.75

# ui
- Maintain consistent theming across all pages; strictly follow existing page theming. Confidence: 0.70
- For loading/wait screens, prefer a static info page over animation — keep it simple. Confidence: 0.70
- Do not show probability/risk tags in event choice UI. Confidence: 0.70

# images
- Location and event images use 9:16 vertical mobile-friendly composition by default. Confidence: 0.75
- Images from index 14 onward use 1:1 square 1024x1024 composition. Confidence: 0.75

# build
- Build with `./gradlew assembleDebug` and install with `./gradlew installDebug`. Confidence: 0.80
- Build and run APK after significant changes to verify. Confidence: 0.60
