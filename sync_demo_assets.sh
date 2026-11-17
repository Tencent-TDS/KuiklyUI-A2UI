#!/bin/bash
# sync_demo_assets.sh
# Syncs demo asset files from Android (source-of-truth) to iOS/ohos platforms.
#
# Sources (in Android side after the 4-App split):
#   - androidAppForKuiklyPage/src/main/assets/  → components / custom_components /
#                                                 custom_functions / icons
#   - androidAppForTest/src/main/assets/         → databinding_tests / examples /
#                                                 function_tests / theme_tests
#
# Targets:
#   iOS (per 4-App split):
#     - iosAppForNativePage/Resources/  ← components/custom_components/custom_functions/icons
#     - iosAppForKuiklyPage/Resources/  ← components/custom_components/custom_functions/icons
#     - iosAppForBothPages/Resources/   ← (none)
#     - iosAppForTest/Resources/        ← everything used by the Test showcase
#
#   ohos (per 4-App split, matching the Android/iOS split one-for-one):
#     - ohosAppForNativePage/entry/src/main/resources/rawfile   ← components/custom_*/custom_functions
#     - ohosAppForNativePage/entry/src/main/resources/resfile   ← icons
#     - ohosAppForKuiklyPage/entry/src/main/resources/rawfile   ← components/custom_*/custom_functions
#     - ohosAppForKuiklyPage/entry/src/main/resources/resfile   ← icons
#     - ohosAppForBothPages/entry/src/main/resources/{rawfile,resfile}   ← (none)
#     - ohosAppForTest/entry/src/main/resources/rawfile         ← test-only asset set
#
# Usage:
#   ./sync_demo_assets.sh [ios|ohos|all]
#   Default: all
#
# This script is designed to be called automatically:
#   - iOS: via each iosAppFor* target's setup.sh before pod install
#   - ohos: via hvigor pre-build hook on each ohosAppFor* module

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ─── Sources ──────────────────────────────────────────────────────────────
SRC_KUIKLYPAGE="$SCRIPT_DIR/androidAppForKuiklyPage/src/main/assets"
SRC_TEST="$SCRIPT_DIR/androidAppForTest/src/main/assets"

# ─── iOS targets ──────────────────────────────────────────────────────────
IOS_NATIVE_DIR="$SCRIPT_DIR/iosAppForNativePage/Resources"
IOS_KUIKLY_DIR="$SCRIPT_DIR/iosAppForKuiklyPage/Resources"
IOS_TEST_DIR="$SCRIPT_DIR/iosAppForTest/Resources"

# ─── ohos targets (per-app; mirrors the Android/iOS 4-App split) ──────────
OHOS_NATIVE_RAWFILE="$SCRIPT_DIR/ohosAppForNativePage/entry/src/main/resources/rawfile"
OHOS_NATIVE_RESFILE="$SCRIPT_DIR/ohosAppForNativePage/entry/src/main/resources/resfile"
OHOS_KUIKLY_RAWFILE="$SCRIPT_DIR/ohosAppForKuiklyPage/entry/src/main/resources/rawfile"
OHOS_KUIKLY_RESFILE="$SCRIPT_DIR/ohosAppForKuiklyPage/entry/src/main/resources/resfile"
OHOS_TEST_RAWFILE="$SCRIPT_DIR/ohosAppForTest/entry/src/main/resources/rawfile"
# BothPages ohos app intentionally has no JSON/icon assets — parity with iOS/Android BothPages.

# ─── Asset dir groups ─────────────────────────────────────────────────────
# Component / native-card story: shared by NativePage + KuiklyPage apps.
COMPONENT_DIRS=(
    "a2ui_components"
    "a2ui_custom_components"
    "a2ui_custom_functions"
)
# Icon assets need resfile on ohos, Resources on iOS.
ICON_DIRS=(
    "a2ui_icons"
)
TEST_ONLY_DIRS=(
    "v0_9"
    "v1_0"
)

# ─── Helpers ──────────────────────────────────────────────────────────────
# Copy the given asset sub-dirs from $src → $target with rsync (incremental,
# preserves symlinks/perms, removes files that no longer exist on the source).
sync_dirs() {
    local src="$1"
    local target="$2"
    shift 2
    local dirs=("$@")

    mkdir -p "$target"
    for dir in "${dirs[@]}"; do
        local from="$src/$dir"
        if [ -d "$from" ]; then
            rsync -a --delete "$from/" "$target/$dir/"
        fi
    done
}

sync_android() {
    echo "→ Syncing Android formal assets…"
    sync_dirs "$SRC_KUIKLYPAGE" "$SCRIPT_DIR/androidAppForNativePage/src/main/assets" "${COMPONENT_DIRS[@]}" "${ICON_DIRS[@]}"
    for dir in "${COMPONENT_DIRS[@]}"; do
        if [ -d "$SCRIPT_DIR/androidAppForBothPages/src/main/assets/$dir" ]; then
            sync_dirs "$SRC_KUIKLYPAGE" "$SCRIPT_DIR/androidAppForBothPages/src/main/assets" "$dir"
        fi
    done
}

# ─── iOS sync ─────────────────────────────────────────────────────────────
sync_ios() {
    echo "→ Syncing iOS assets…"

    # iosAppForNativePage: components + custom_components + custom_functions + icons
    echo "  · iosAppForNativePage/Resources ← androidAppForKuiklyPage/assets"
    sync_dirs "$SRC_KUIKLYPAGE" "$IOS_NATIVE_DIR" "${COMPONENT_DIRS[@]}" "${ICON_DIRS[@]}"

    # iosAppForKuiklyPage: same set as NativePage.
    echo "  · iosAppForKuiklyPage/Resources ← androidAppForKuiklyPage/assets"
    sync_dirs "$SRC_KUIKLYPAGE" "$IOS_KUIKLY_DIR" "${COMPONENT_DIRS[@]}" "${ICON_DIRS[@]}"

    echo "  · iosAppForTest/Resources ← androidAppForTest/assets"
    sync_dirs "$SRC_TEST" "$IOS_TEST_DIR" "${TEST_ONLY_DIRS[@]}"

    # iosAppForBothPages: no JSON assets needed — parity with
    # androidAppForBothPages which has an empty assets/ directory.

    echo "  ✅ iOS assets synced"
}

# ─── ohos sync ────────────────────────────────────────────────────────────
sync_ohos() {
    echo "→ Syncing ohos assets…"

    # ohosAppForNativePage: components/custom_*/custom_functions in rawfile,
    # icons in resfile — mirrors iosAppForNativePage/Resources one-for-one.
    echo "  · ohosAppForNativePage rawfile+resfile ← androidAppForKuiklyPage/assets"
    sync_dirs "$SRC_KUIKLYPAGE" "$OHOS_NATIVE_RAWFILE" "${COMPONENT_DIRS[@]}"
    sync_dirs "$SRC_KUIKLYPAGE" "$OHOS_NATIVE_RESFILE" "${ICON_DIRS[@]}"

    # ohosAppForKuiklyPage: same asset set as NativePage.
    echo "  · ohosAppForKuiklyPage rawfile+resfile ← androidAppForKuiklyPage/assets"
    sync_dirs "$SRC_KUIKLYPAGE" "$OHOS_KUIKLY_RAWFILE" "${COMPONENT_DIRS[@]}"
    sync_dirs "$SRC_KUIKLYPAGE" "$OHOS_KUIKLY_RESFILE" "${ICON_DIRS[@]}"

    # ohosAppForTest: test-only asset dirs in rawfile only (no icons —
    # BothPages/Test intentionally leave icon registry empty so unregistered
    # names fall back to the built-in emoji glyphs).
    echo "  · ohosAppForTest rawfile ← androidAppForTest/assets"
    sync_dirs "$SRC_TEST" "$OHOS_TEST_RAWFILE" "${TEST_ONLY_DIRS[@]}"

    # ohosAppForBothPages: intentionally empty rawfile/resfile — parity with
    # androidAppForBothPages / iosAppForBothPages.

    echo "  ✅ ohos assets synced"
}

# ─── Entry ────────────────────────────────────────────────────────────────
TARGET="${1:-all}"
python3 "$SCRIPT_DIR/scripts/generate_demo_asset_manifest.py"

case "$TARGET" in
    android) sync_android ;;
    ios)  sync_ios ;;
    ohos) sync_ohos ;;
    all)  sync_android; sync_ios; sync_ohos ;;
    *)
        echo "Usage: $0 [android|ios|ohos|all]"
        exit 1
        ;;
esac

echo "=== Asset sync complete ==="
