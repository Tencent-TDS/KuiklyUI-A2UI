#!/bin/bash
# Script to generate Xcode project and install pods for iosAppForKuiklyPage
# (A2UI showcase focused on the *Kuikly-Page* integration path — DSL & Compose).
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

echo "=== A2UI iOS Demo Setup (KuiklyPage) ==="

# Ensure the .kuikly-external symlink is present so that the Podfile can
# resolve `pod 'OpenKuiklyIOSRender', :path => '../.kuikly-external'`
# during the first `pod install` after a fresh clone.
bash "$SCRIPT_DIR/../scripts/link-kuikly.sh"

echo "→ Generating dummy A2UICore framework for pod install..."
# Standalone A2UI project: `../` (one level up) is the A2UI repo root,
# whose gradlew + settings.gradle.kts already include the `:a2ui`, `:demo`
# and `:androidAppFor*` projects and the external Kuikly modules. No need
# to reach into the KuiklyUI checkout.
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
"$REPO_ROOT/gradlew" -p "$REPO_ROOT" :a2ui:generateDummyFramework
echo "  ✅ Dummy framework generated"

echo "→ Generating dummy A2UIDemo framework for pod install..."
# The Kuikly-Page & Compose-Page hosting VCs load pages from :demo,
# which ships as its own cocoapods framework (see a2ui_demo.podspec). pod
# install below refuses to run if this framework directory doesn't exist.
"$REPO_ROOT/gradlew" -p "$REPO_ROOT" :demo:generateDummyFramework
echo "  ✅ A2UIDemo dummy framework generated"

echo "→ Syncing demo assets from Android..."
SYNC_SCRIPT="$SCRIPT_DIR/../sync_demo_assets.sh"
if [ -x "$SYNC_SCRIPT" ]; then
    "$SYNC_SCRIPT" ios
else
    chmod +x "$SYNC_SCRIPT"
    "$SYNC_SCRIPT" ios
fi
echo "  ✅ Demo assets synced"

echo "→ Generating Xcode project..."
if command -v xcodegen &> /dev/null; then
    xcodegen generate
    echo "  ✅ Xcode project generated"
else
    echo "  ⚠️  XcodeGen not found. Install with: brew install xcodegen"
    echo "  Attempting to continue without XcodeGen..."
fi

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
