#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────
# apply-ohos-mode.sh — rewrite ohos workspace files to match
# `kuikly.useSource` AND `a2ui.useSource`.
#
# Covers two orthogonal switches:
#
#   ┌────────────────────────────────────────────────────────────────┐
#   │ `kuikly.useSource` (DEFAULT false — artefact)                  │
#   │   source   → oh-package.json5 @kuikly-open/render              │
#   │              = "file:../..?/core-render-ohos"                  │
#   │              build-profile.json5 includes {"name":"render",    │
#   │              "srcPath":"../core-render-ohos"}                  │
#   │   artefact → oh-package.json5 = "<kuikly.version>"             │
#   │              build-profile.json5 "render" module removed       │
#   └────────────────────────────────────────────────────────────────┘
#   ┌────────────────────────────────────────────────────────────────┐
#   │ `a2ui.useSource` (DEFAULT true — source)                       │
#   │   source   → oh-package.json5 @kuikly-a2ui-open/render         │
#   │              = "file:../..?/a2ui-render-ohos"                  │
#   │              build-profile.json5 includes {"name":"a2ui_render"│
#   │              , "srcPath":"../a2ui-render-ohos"}                │
#   │   artefact → oh-package.json5 = "<a2ui.version>"               │
#   │              build-profile.json5 "a2ui_render" module removed  │
#   └────────────────────────────────────────────────────────────────┘
#
# This script is invoked by `scripts/link-kuikly.sh` and is idempotent —
# safe to run repeatedly, independent of the current on-disk state.
#
# Mirrors the Gradle (`KuiklyDep.kt` + `A2uiDep.kt`) / CocoaPods
# (`kuikly_cocoapods.rb` + `a2ui_cocoapods.rb`) dual-mode machinery on
# the ohos side. Resolution order follows the same convention:
# `*_USE_SOURCE` / `*_VERSION` env vars > local.properties >
# gradle.properties.
# ─────────────────────────────────────────────────────────────────────────

set -e
set -o pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=./lib/kuikly-mode.sh
source "$SCRIPT_DIR/lib/kuikly-mode.sh"

if kuikly_use_source; then
    KUIKLY_MODE="source"
else
    KUIKLY_MODE="artifact"
fi
KUIKLY_VER="$(kuikly_version)"

if a2ui_use_source; then
    A2UI_MODE="source"
else
    A2UI_MODE="artifact"
fi
A2UI_VER="$(a2ui_version)"

echo "=== apply-ohos-mode:"
echo "    kuikly: mode=$KUIKLY_MODE, kuikly.version=$KUIKLY_VER"
echo "    a2ui  : mode=$A2UI_MODE,   a2ui.version=$A2UI_VER"
echo "==="

# ── 1. oh-package.json5 ─────────────────────────────────────────────────
# Both @kuikly-open/render and @kuikly-a2ui-open/render deps are
# rewritten in-place. `rewrite_render_dep` takes the package name as the
# first arg so the same perl invocation handles both.

rewrite_render_dep() {
    local pkg="$1"       # e.g. @kuikly-open/render  OR  @kuikly-a2ui-open/render
    local file="$2"
    local new_value="$3"
    [ -f "$file" ] || return 0
    # Build a perl-safe literal of the package name:
    #   - escape every `/` so it survives inside `s{...}` (not strictly
    #     needed with brace delimiters, but harmless and consistent).
    #   - escape every `@` so perl doesn't try to interpolate it as an
    #     array reference (`@kuikly-a2ui-open` would expand to the empty
    #     list, silently killing the match).
    #
    # `perl -pi` (not sed -i -E) for cross-platform (BSD/GNU) consistency.
    #
    # Pattern: match any current form of the "<pkg>" dep line and
    # replace the value, preserving indentation + trailing comma. Both
    # file:... and "x.y.z" semver inputs are accepted.
    local pkg_pat="${pkg//\//\\/}"
    pkg_pat="${pkg_pat//@/\\@}"
    perl -i -pe \
        's{("'"$pkg_pat"'"\s*:\s*)"[^"]+"}{$1"'"$new_value"'"}' \
        "$file"
}

# ── 1a. @kuikly-open/render — rewrite in all 5 consuming files ──────────
#   a2ui-render-ohos/oh-package.json5         → file:../core-render-ohos
#   ohosAppFor*/entry/oh-package.json5 (×4)   → file:../../core-render-ohos
if [ "$KUIKLY_MODE" = "source" ]; then
    rewrite_render_dep "@kuikly-open/render" \
        "$SCRIPT_DIR/../a2ui-render-ohos/oh-package.json5" \
        "file:../core-render-ohos"
    for app in ohosAppForNativePage ohosAppForKuiklyPage ohosAppForBothPages ohosAppForTest; do
        rewrite_render_dep "@kuikly-open/render" \
            "$SCRIPT_DIR/../$app/entry/oh-package.json5" \
            "file:../../core-render-ohos"
    done
else
    rewrite_render_dep "@kuikly-open/render" \
        "$SCRIPT_DIR/../a2ui-render-ohos/oh-package.json5" \
        "$KUIKLY_VER"
    for app in ohosAppForNativePage ohosAppForKuiklyPage ohosAppForBothPages ohosAppForTest; do
        rewrite_render_dep "@kuikly-open/render" \
            "$SCRIPT_DIR/../$app/entry/oh-package.json5" \
            "$KUIKLY_VER"
    done
fi
echo "  ✅ @kuikly-open/render dep rewritten in oh-package.json5 (×5)"

# ── 1b. @kuikly-a2ui-open/render — rewrite in all 4 consuming files ─────
#   ohosAppFor*/entry/oh-package.json5 (×4)
# A2UI's own `a2ui-render-ohos/oh-package.json5` does NOT depend on
# itself, so no self-rewrite is needed here. In source mode the entry
# modules point at the LOCAL `a2ui-render-ohos` module (via the
# in-workspace build-profile srcPath); in artefact mode they pin the
# published ohpm version.
if [ "$A2UI_MODE" = "source" ]; then
    for app in ohosAppForNativePage ohosAppForKuiklyPage ohosAppForBothPages ohosAppForTest; do
        rewrite_render_dep "@kuikly-a2ui-open/render" \
            "$SCRIPT_DIR/../$app/entry/oh-package.json5" \
            "file:../../a2ui-render-ohos"
    done
else
    for app in ohosAppForNativePage ohosAppForKuiklyPage ohosAppForBothPages ohosAppForTest; do
        rewrite_render_dep "@kuikly-a2ui-open/render" \
            "$SCRIPT_DIR/../$app/entry/oh-package.json5" \
            "$A2UI_VER"
    done
fi
echo "  ✅ @kuikly-a2ui-open/render dep rewritten in oh-package.json5 (×4)"

# ── 2. build-profile.json5 (4 ohosApp files) ─────────────────────────────
# For EACH named module ("render" / "a2ui_render"):
#   source mode   → ensure the module entry is present (with its srcPath)
#   artefact mode → ensure the module entry is absent (pulled from ohpm)
#
# `sync_build_profile_module` is called twice per file so both switches
# apply independently.

sync_build_profile_module() {
    local file="$1"
    local module_name="$2"   # e.g. "render" or "a2ui_render"
    local src_path="$3"      # e.g. "../core-render-ohos" or "../a2ui-render-ohos"
    local mode="$4"          # "source" or "artifact"
    [ -f "$file" ] || return 0

    # Always strip any existing block for this module name — perl
    # removes `{ "name": "<module_name>", ... }` and the optional
    # leading comma + whitespace. The regex balances a single nested `{...}`
    # pair (sufficient for the "targets" sub-object).
    MODULE_NAME="$module_name" perl -i -0pe '
        my $name = quotemeta($ENV{MODULE_NAME});
        s{,?\s*\{\s*"name"\s*:\s*"$name"[^{}]*(?:\{[^{}]*\}[^{}]*)*\}}{}sg;
    ' "$file"

    # Cleanup pass: collapse any blank lines left between `}` (end of
    # the preceding module) and `]` (close of the modules array).
    perl -i -0pe 's{(\})\s*\n(\s*\n)+(\s*\])}{$1\n$3}sg' "$file"

    if [ "$mode" = "source" ]; then
        # Insert the module block right before the LAST `  ]\n}` tail
        # (the close of the `modules` array). python3 handles the
        # splice so we don't have to deal with sed/awk escaping.
        local tmp="$file.tmp.$$"
        MODULE_NAME="$module_name" SRC_PATH="$src_path" \
        python3 - "$file" "$tmp" <<'PY'
import pathlib, sys, re, os
src = pathlib.Path(sys.argv[1]).read_text()
pattern = re.compile(r'(\n[ \t]*\])(\s*\n\})\s*\Z', re.MULTILINE)
m = pattern.search(src)
if not m:
    sys.exit(f"[apply-ohos-mode] cannot locate `modules]` tail in {sys.argv[1]}")
mod_name = os.environ["MODULE_NAME"]
src_path = os.environ["SRC_PATH"]
snippet = f'''    {{
      "name": "{mod_name}",
      "srcPath": "{src_path}",
      "targets": [
        {{
          "name": "default",
          "applyToProducts": [
            "default"
          ]
        }}
      ]
    }}'''
out = src[:m.start()] + ',\n' + snippet + m.group(1) + m.group(2) + '\n'
pathlib.Path(sys.argv[2]).write_text(out)
PY
        mv "$tmp" "$file"
    fi
}

for app in ohosAppForNativePage ohosAppForKuiklyPage ohosAppForBothPages ohosAppForTest; do
    bp="$SCRIPT_DIR/../$app/build-profile.json5"
    # Kuikly render module ─ driven by `kuikly.useSource`
    sync_build_profile_module "$bp" "render" "../core-render-ohos" "$KUIKLY_MODE"
    # A2UI render module ─ driven by `a2ui.useSource`
    sync_build_profile_module "$bp" "a2ui_render" "../a2ui-render-ohos" "$A2UI_MODE"
done
echo "  ✅ build-profile.json5 (×4) synced"

echo "✅ apply-ohos-mode done"
