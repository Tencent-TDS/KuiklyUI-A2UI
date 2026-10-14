#!/bin/bash
# A2UI OHOS Build Script (Option-A: minimal-dependency variant).
#
# Standalone-A2UI-project version. Produces TWO separate libshared.so
# artifacts so that each ohosAppFor* module carries only the Kotlin/Native
# symbols it actually needs:
#
#   ┌──────────────────────────┬───────────────────────────────┬─────────────────────────────────┐
#   │ Gradle task              │ Bundles                        │ Consumer apps                    │
#   ├──────────────────────────┼───────────────────────────────┼─────────────────────────────────┤
#   │ :a2ui        linkShared… │ A2UI runtime only              │ ohosAppForNativePage             │
#   │                          │                                │ ohosAppForTest                   │
#   │ :demo        linkShared… │ A2UI runtime + demo Pager      │ ohosAppForKuiklyPage             │
#   │                          │ classes (@Page annotated)      │ ohosAppForBothPages              │
#   └──────────────────────────┴───────────────────────────────┴─────────────────────────────────┘
#
# Run from the A2UI project root directory:
#   ./2.0_a2ui_ohos_build.sh

set -e

echo "=== A2UI OHOS Build Script (standalone A2UI project) ==="

# Verify we're in the A2UI project root.
if [ ! -f "gradlew" ] || [ ! -f "settings.2.0.ohos.gradle.kts" ]; then
    echo "ERROR: This script must be run from the A2UI project root directory."
    echo "Usage: ./2.0_a2ui_ohos_build.sh"
    exit 1
fi

# Ensure the .kuikly-external symlink is up-to-date so oh-package.json5
# resolutions and any shared-lib deps that reference `core-render-ohos/`
# from the external Kuikly checkout keep working.
./scripts/link-kuikly.sh

# 1. Record original Gradle URL
ORIGIN_DISTRIBUTION_URL=$(grep "distributionUrl" gradle/wrapper/gradle-wrapper.properties | cut -d "=" -f 2)
echo "Original Gradle URL: $ORIGIN_DISTRIBUTION_URL"

# 2. Switch Gradle version (Kotlin 2.0.21-KBA-010 already declared in
#    build.2.0.ohos.gradle.kts — matches the external Kuikly ohos build).
NEW_DISTRIBUTION_URL="https\:\/\/services.gradle.org\/distributions\/gradle-8.0-bin.zip"
sed -i.bak "s/distributionUrl=.*$/distributionUrl=$NEW_DISTRIBUTION_URL/" gradle/wrapper/gradle-wrapper.properties
echo "New Gradle URL: $(grep "distributionUrl" gradle/wrapper/gradle-wrapper.properties | cut -d "=" -f 2)"

# Cleanup helper — always restore the mutated files, even on failure.
restore_files() {
    if [ -f gradle/wrapper/gradle-wrapper.properties.bak ]; then
        mv gradle/wrapper/gradle-wrapper.properties.bak gradle/wrapper/gradle-wrapper.properties
    fi
}
trap restore_files EXIT

# 3. Build TWO artifacts.
#    A2UI-runtime-only artifact (small): :a2ui
echo ""
echo "[Build 1/2] :a2ui   → runtime-only libshared.so (for NativePage + Test)"
KUIKLY_AGP_VERSION="7.4.2" KUIKLY_KOTLIN_VERSION="2.0.21-KBA-010" \
    ./gradlew -c settings.2.0.ohos.gradle.kts \
    :a2ui:linkSharedDebugSharedOhosArm64 --stacktrace

#    Runtime + demo-Pager artifact (larger): :demo (api-exports :a2ui)
echo ""
echo "[Build 2/2] :demo   → runtime + demo Pager libshared.so (for KuiklyPage + BothPages)"
KUIKLY_AGP_VERSION="7.4.2" KUIKLY_KOTLIN_VERSION="2.0.21-KBA-010" \
    ./gradlew -c settings.2.0.ohos.gradle.kts \
    :demo:linkSharedDebugSharedOhosArm64 --stacktrace

# 4. Copy artifacts to their respective consumer apps.
# Standalone project layout: :a2ui's projectDir is <root>/a2ui (not
# <root>/a2ui/a2ui as in the old mono-repo). Likewise for :demo.
LITE_SO_PATH=$PWD/a2ui/build/bin/ohosArm64/sharedDebugShared/libshared.so
LITE_SO_HEADER=$PWD/a2ui/build/bin/ohosArm64/sharedDebugShared/libshared_api.h
FULL_SO_PATH=$PWD/demo/build/bin/ohosArm64/sharedDebugShared/libshared.so
FULL_SO_HEADER=$PWD/demo/build/bin/ohosArm64/sharedDebugShared/libshared_api.h

copy_artifact() {
    local app_name=$1
    local so_path=$2
    local so_header=$3
    local app_dir=./$app_name

    if [ ! -d "$app_dir" ]; then
        echo "  · skip $app_name (directory not found)"
        return
    fi

    local libs_dir=$app_dir/entry/libs/arm64-v8a
    local header_dir=$app_dir/entry/src/main/cpp/thirdparty/biz_entry

    mkdir -p "$libs_dir" "$header_dir"
    cp "$so_path" "$libs_dir/"
    cp "$so_header" "$header_dir/"
    echo "  · $app_name ← $(basename "$(dirname "$(dirname "$(dirname "$so_path")")")")/libshared.so"
}

echo ""
echo "[Copy] Distributing artifacts…"
copy_artifact ohosAppForNativePage "$LITE_SO_PATH" "$LITE_SO_HEADER"
copy_artifact ohosAppForTest       "$LITE_SO_PATH" "$LITE_SO_HEADER"
copy_artifact ohosAppForKuiklyPage "$FULL_SO_PATH" "$FULL_SO_HEADER"
copy_artifact ohosAppForBothPages  "$FULL_SO_PATH" "$FULL_SO_HEADER"

echo ""
echo "=== A2UI OHOS Build Complete ==="
