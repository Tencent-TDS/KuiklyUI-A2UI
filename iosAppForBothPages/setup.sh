#!/bin/bash
# Script to generate Xcode project and install pods for iosAppForBothPages
# (side-by-side comparison: Kuikly-Page card vs Native-Page card).
# Prerequisites: xcodegen, cocoapods

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== A2UI iOS Demo Setup (BothPages) ==="

# Ensure the .kuikly-external symlink is present so Podfile `:path => .kuikly-external` resolves.
# Only needed under source mode (`kuikly.useSource=true`); artifact mode
# (default) pulls OpenKuiklyIOSRender from CocoaPods trunk instead.
# shellcheck source=../scripts/lib/kuikly-mode.sh
source "$SCRIPT_DIR/../scripts/lib/kuikly-mode.sh"
if kuikly_use_source; then
    bash "$SCRIPT_DIR/../scripts/link-kuikly.sh"
else
    echo "→ Skipping link-kuikly.sh (artifact mode: kuikly.useSource=false)"
fi

REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# In artifact mode (`a2ui.useSource=false`) the Podfile references
# `pod 'a2ui_demo', :path => '../demo'` but NOT the local `pod 'a2ui'` —
# `settings.gradle.kts` also skips `include(":a2ui")` in that mode, so
# `:a2ui:generateDummyFramework` would fail with "project 'a2ui' not
# found". The A2UIDemo dummy framework below is still required.
if a2ui_use_source; then
    echo "→ Generating dummy A2UICore framework for pod install..."
    "$REPO_ROOT/gradlew" -p "$REPO_ROOT" :a2ui:generateDummyFramework
    echo "  ✅ Dummy framework generated"
else
    echo "→ Skipping :a2ui:generateDummyFramework (artifact mode: a2ui.useSource=false)"
fi

echo "→ Generating dummy A2UIDemo framework for pod install..."
# KuiklyPageCardViewController hosts a Kuikly page from :demo, which
# ships as its own cocoapods framework (see a2ui_demo.podspec).
"$REPO_ROOT/gradlew" -p "$REPO_ROOT" :demo:generateDummyFramework
echo "  ✅ A2UIDemo dummy framework generated"

# BothPages parity with androidAppForBothPages: no JSON assets shipped.
# Running the shared sync is still safe (idempotent, syncs sibling apps only).
echo "→ Syncing demo assets from Android (idempotent no-op for BothPages)..."
SYNC_SCRIPT="$SCRIPT_DIR/../sync_demo_assets.sh"
if [ -x "$SYNC_SCRIPT" ]; then
    "$SYNC_SCRIPT" ios
else
    chmod +x "$SYNC_SCRIPT"
    "$SYNC_SCRIPT" ios
fi
echo "  ✅ Sibling assets synced"

echo "→ Generating Xcode project..."
if command -v xcodegen &> /dev/null; then
    xcodegen generate
    echo "  ✅ Xcode project generated"
else
    echo "  ⚠️  XcodeGen not found. Install with: brew install xcodegen"
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
