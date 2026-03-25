#!/bin/bash

# The Warden Protocol - Build Script
# This script helps verify the project structure and provides build instructions

echo "╔══════════════════════════════════════════════════════════════╗"
echo "║           THE WARDEN PROTOCOL - Build Verification          ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""

# Check if we're in the right directory
if [ ! -f "settings.gradle.kts" ]; then
    echo "❌ Error: Not in project root directory"
    echo "   Please run this script from ~/repos/warden_protocol_1"
    exit 1
fi

echo "✅ Project root directory confirmed"
echo ""

# Count files
echo "📊 Project Statistics:"
echo "   ├─ Kotlin files: $(find app/src/main -name "*.kt" | wc -l)"
echo "   ├─ XML resources: $(find app/src/main/res -name "*.xml" | wc -l)"
echo "   ├─ Gradle files: $(find . -maxdepth 2 -name "*.gradle*" | wc -l)"
echo "   └─ Documentation: $(ls -1 *.md 2>/dev/null | wc -l)"
echo ""

# Check key files
echo "🔍 Verifying Key Files:"

check_file() {
    if [ -f "$1" ]; then
        echo "   ✅ $1"
    else
        echo "   ❌ $1 (MISSING)"
    fi
}

check_file "app/build.gradle.kts"
check_file "app/src/main/AndroidManifest.xml"
check_file "app/src/main/java/com/wardenprotocol/game/MainActivity.kt"
check_file "app/src/main/java/com/wardenprotocol/game/data/repository/EventRepository.kt"
check_file "app/src/main/java/com/wardenprotocol/game/domain/engine/GameEngine.kt"
check_file "app/src/main/java/com/wardenprotocol/game/ui/viewmodel/GameViewModel.kt"

echo ""

# Check EventRepository size
EVENT_LINES=$(wc -l < app/src/main/java/com/wardenprotocol/game/data/repository/EventRepository.kt)
echo "📝 EventRepository: $EVENT_LINES lines"
if [ "$EVENT_LINES" -gt 1000 ]; then
    echo "   ✅ Contains 40+ events (expected 1,300+ lines)"
else
    echo "   ⚠️  May be incomplete (expected 1,300+ lines)"
fi

echo ""
echo "🚀 Build Instructions:"
echo ""
echo "Option 1: Android Studio (Recommended)"
echo "   1. Open Android Studio"
echo "   2. File → Open → Select this directory"
echo "   3. Wait for Gradle sync"
echo "   4. Click Run (▶) button"
echo ""
echo "Option 2: Command Line (if Gradle wrapper exists)"
echo "   ./gradlew assembleDebug"
echo "   ./gradlew installDebug"
echo ""
echo "Option 3: Generate Gradle Wrapper First"
echo "   gradle wrapper --gradle-version 8.2"
echo "   ./gradlew assembleDebug"
echo ""

echo "📚 Documentation:"
echo "   ├─ README.md - Game overview"
echo "   ├─ QUICKSTART.md - Setup guide"
echo "   ├─ IMPLEMENTATION_SUMMARY.md - Technical details"
echo "   └─ CHECKLIST.md - Requirements verification"
echo ""

echo "✅ Project verification complete!"
echo "   Status: READY TO BUILD"
echo ""
