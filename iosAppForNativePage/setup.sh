#!/bin/bash
# Script to generate Xcode project and install pods for iosAppForNativePage
# (A2UI showcase focused on the *Native View* integration path).
# Prerequisites: xcodegen, cocoapods
#
# Behaviour:
# - A2UICore.framework is built automatically by CocoaPods script phase
#   during each Xcode build (debug/release follows Xcode configuration)
# - `sync_demo_assets.sh ios` syncs every iosAppFor* target's Resources
#   from Android in one pass (idempotent; running it repeatedly is safe).
# - No manual gradlew linkFramework needed

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== A2UI iOS Demo Setup ==="

# Ensure the .kuikly-external symlink is present so Podfile `:path => .kuikly-external` resolves.
bash "$SCRIPT_DIR/../scripts/link-kuikly.sh"

# Step 1: Generate dummy framework for CocoaPods (required for pod install)
echo "→ Generating dummy A2UICore framework for pod install..."
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
"$REPO_ROOT/gradlew" -p "$REPO_ROOT" :a2ui:generateDummyFramework
echo "  ✅ Dummy framework generated"

# Step 2: Sync demo assets from Android (source-of-truth)
echo "→ Syncing demo assets from Android..."
SYNC_SCRIPT="$SCRIPT_DIR/../sync_demo_assets.sh"
if [ -x "$SYNC_SCRIPT" ]; then
    "$SYNC_SCRIPT" ios
else
    chmod +x "$SYNC_SCRIPT"
    "$SYNC_SCRIPT" ios
fi
echo "  ✅ Demo assets synced"

# Step 3: Generate Xcode project using XcodeGen
echo "→ Generating Xcode project..."
if command -v xcodegen &> /dev/null; then
    xcodegen generate
    echo "  ✅ Xcode project generated"
else
    echo "  ⚠️  XcodeGen not found. Install with: brew install xcodegen"
    echo "  Attempting to continue without XcodeGen..."
fi

# Step 4: Install CocoaPods dependencies
echo "→ Installing CocoaPods dependencies..."
if command -v pod &> /dev/null; then
    pod install
    echo "  ✅ Pods installed"
else
    echo "  ⚠️  CocoaPods not found. Install with: sudo gem install cocoapods"
    exit 1
fi

echo ""
echo "=== Setup Complete ==="
echo "Open A2UIDemoApp.xcworkspace in Xcode to build and run."
echo ""
echo "Note: A2UICore.framework is now built automatically during Xcode build."
echo "      Debug/Release configuration follows your Xcode scheme setting."
echo ""
