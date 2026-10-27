#!/bin/bash
# Script to generate Xcode project and install pods for iosAppForTest
# (A2UI protocol / theme / databinding / function sandbox — the full test suite).
# Prerequisites: xcodegen, cocoapods

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== A2UI iOS Demo Setup (Test) ==="

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

# iosAppForTest's Podfile consumes the local `pod 'a2ui', :path => '../a2ui'`,
# whose `A2UICore.framework` is produced by `:a2ui:generateDummyFramework`.
# That task requires `:a2ui` to be included as a Gradle subproject, which
# only happens when `a2ui.useSource=true`. Bail out early with a clear
# message in artifact mode instead of crashing in `pod install` later.
if ! a2ui_use_source; then
    cat <<'EOF' >&2

❌ iosAppForTest does not support artifact mode (a2ui.useSource=false).

   Its Podfile consumes the local `pod 'a2ui', :path => '../a2ui'`, whose
   `A2UICore.framework` is produced by `:a2ui:generateDummyFramework`.
   In artifact mode `:a2ui` is not included as a Gradle subproject, so
   that task is unavailable.

   Fix: set `a2ui.useSource=true` in gradle.properties (or local.properties)
   and re-run this script. For an artifact-mode smoke test of the published
   OpenKuiklyA2UIIOSRender pod, use iosAppForKuiklyPage or iosAppForBothPages
   instead — their Podfiles depend on `pod 'a2ui_demo'`, which stays local
   but pulls `:a2ui` via Maven.

EOF
    exit 1
fi

echo "→ Generating dummy A2UICore framework for pod install..."
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
"$REPO_ROOT/gradlew" -p "$REPO_ROOT" :a2ui:generateDummyFramework
echo "  ✅ Dummy framework generated"

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
