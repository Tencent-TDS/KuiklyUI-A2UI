#!/bin/bash
# A2UI OHOS Release Build Script (Option-A: minimal-dependency variant).
#
# Standalone-A2UI-project version. Produces TWO libshared.so artifacts
# (Release) and distributes them to the 4 ohosAppFor* modules per their
# actual dependency needs:
#
#   :a2ui  linkSharedReleaseSharedOhosArm64 → NativePage, Test
#   :demo  linkSharedReleaseSharedOhosArm64 → KuiklyPage, BothPages
#
# Uses FileReplacer for compatibility replacements, aligned with
# kuikly's publish/2.0_ohos_build_release.sh.

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# Standalone A2UI project: the script lives at the project root itself.
PROJECT_ROOT="$SCRIPT_DIR"

echo "=== A2UI OHOS Release Build Script (standalone A2UI project) ==="
echo "sh path: $SCRIPT_DIR"
echo "project's root path: $PROJECT_ROOT"

cd "$PROJECT_ROOT" || { echo "Can't cd project's root path: $PROJECT_ROOT"; exit 1; }

# Ensure the .kuikly-external symlink is present.
./scripts/link-kuikly.sh

java -version

CONFIG_FILE="publish/compatible/2.0_ohos_build.yaml"

# 1. Compatibility replacements (Gradle 7.6.3 -> 8.0)
echo "[Step 1] Applying compatibility replacements..."
java publish/FileReplacer.java replace "$CONFIG_FILE"

restore_replacements() {
    echo "[Cleanup] Restoring compatibility replacements..."
    java publish/FileReplacer.java restore "$CONFIG_FILE" || true
}
trap restore_replacements EXIT

# 2. Build TWO artifacts (Release).
echo ""
echo "[Step 2a] :a2ui   → runtime-only libshared.so (for NativePage + Test)"
KUIKLY_AGP_VERSION="7.4.2" KUIKLY_KOTLIN_VERSION="2.0.21-KBA-010" \
    ./gradlew -c settings.2.0.ohos.gradle.kts \
    :a2ui:linkSharedReleaseSharedOhosArm64 --stacktrace

echo ""
echo "[Step 2b] :demo   → runtime + demo Pager libshared.so (for KuiklyPage + BothPages)"
KUIKLY_AGP_VERSION="7.4.2" KUIKLY_KOTLIN_VERSION="2.0.21-KBA-010" \
    ./gradlew -c settings.2.0.ohos.gradle.kts \
    :demo:linkSharedReleaseSharedOhosArm64 --stacktrace

# 3. Copy artifacts to their respective consumer apps.
# Standalone project layout: :a2ui's projectDir is <root>/a2ui, and
# :demo's projectDir is <root>/demo.
LITE_SO_PATH=$PWD/a2ui/build/bin/ohosArm64/sharedReleaseShared/libshared.so
LITE_SO_HEADER=$PWD/a2ui/build/bin/ohosArm64/sharedReleaseShared/libshared_api.h
FULL_SO_PATH=$PWD/demo/build/bin/ohosArm64/sharedReleaseShared/libshared.so
FULL_SO_HEADER=$PWD/demo/build/bin/ohosArm64/sharedReleaseShared/libshared_api.h

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
echo "[Step 3] Distributing artifacts…"
copy_artifact ohosAppForNativePage "$LITE_SO_PATH" "$LITE_SO_HEADER"
copy_artifact ohosAppForTest       "$LITE_SO_PATH" "$LITE_SO_HEADER"
copy_artifact ohosAppForKuiklyPage "$FULL_SO_PATH" "$FULL_SO_HEADER"
copy_artifact ohosAppForBothPages  "$FULL_SO_PATH" "$FULL_SO_HEADER"

echo ""
echo "=== A2UI OHOS Release Build Complete ==="
