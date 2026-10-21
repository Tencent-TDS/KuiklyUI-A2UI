#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────
# apply-ohos-mode.sh — rewrite ohos workspace files to match
# `kuikly.useSource`.
#
#   source mode (kuikly.useSource=true):
#     - oh-package.json5     → "@kuikly-open/render": "file:../..?/core-render-ohos"
#     - build-profile.json5  → modules[] includes {"name":"render","srcPath":"../core-render-ohos"}
#
#   artifact mode (kuikly.useSource=false, default):
#     - oh-package.json5     → "@kuikly-open/render": "<kuikly.version>"
#     - build-profile.json5  → render module entry removed (pulled from ohpm registry)
#
# This script is invoked by `scripts/link-kuikly.sh` and is idempotent —
# safe to run repeatedly, independent of the current on-disk state.
#
# Mirrors the Gradle (`KuiklyDep.kt`) / CocoaPods (`kuikly_cocoapods.rb`)
# dual-mode machinery on the ohos side. Resolution order follows the
# same convention: KUIKLY_USE_SOURCE / KUIKLY_VERSION env vars take
# precedence over local.properties, which takes precedence over
# gradle.properties.
# ─────────────────────────────────────────────────────────────────────────

set -e
set -o pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=./lib/kuikly-mode.sh
source "$SCRIPT_DIR/lib/kuikly-mode.sh"

if kuikly_use_source; then
    MODE="source"
else
    MODE="artifact"
fi
VERSION="$(kuikly_version)"

echo "=== apply-ohos-mode: mode=$MODE, kuikly.version=$VERSION ==="

# ── 1. oh-package.json5 (5 files) ────────────────────────────────────────
#   a2ui-render-ohos/oh-package.json5         → file:../core-render-ohos
#   ohosAppFor*/entry/oh-package.json5 (×4)   → file:../../core-render-ohos

rewrite_render_dep() {
    local file="$1"
    local new_value="$2"
    [ -f "$file" ] || return 0
    # Escape any `/` in the replacement so it works inside a single
    # sed/perl expression. `perl -pi` is used (not sed -i -E) for
    # cross-platform (BSD/GNU) consistency.
    #
    # Pattern: match any current form of the "@kuikly-open/render" dep
    # line and replace the value, preserving indentation + trailing
    # comma. Both file:... and "x.y.z" semver inputs are accepted.
    perl -i -pe \
        's{("\@kuikly-open/render"\s*:\s*)"[^"]+"}{$1"'"$new_value"'"}' \
        "$file"
}

if [ "$MODE" = "source" ]; then
    rewrite_render_dep "$SCRIPT_DIR/../a2ui-render-ohos/oh-package.json5" \
        "file:../core-render-ohos"
    for app in ohosAppForNativePage ohosAppForKuiklyPage ohosAppForBothPages ohosAppForTest; do
        rewrite_render_dep "$SCRIPT_DIR/../$app/entry/oh-package.json5" \
            "file:../../core-render-ohos"
    done
else
    rewrite_render_dep "$SCRIPT_DIR/../a2ui-render-ohos/oh-package.json5" \
        "$VERSION"
    for app in ohosAppForNativePage ohosAppForKuiklyPage ohosAppForBothPages ohosAppForTest; do
        rewrite_render_dep "$SCRIPT_DIR/../$app/entry/oh-package.json5" \
            "$VERSION"
    done
fi
echo "  ✅ oh-package.json5 (×5) rewritten"

# ── 2. build-profile.json5 (4 ohosApp files) ─────────────────────────────
#   source mode   → ensure a {"name":"render", "srcPath":"../core-render-ohos", …} module is present
#   artifact mode → ensure that module is absent

sync_build_profile() {
    local file="$1"
    [ -f "$file" ] || return 0

    # Always start from a "render-stripped" baseline — perl removes any
    # existing render module block (idempotent). The regex matches the
    # optional leading comma + whitespace, the whole `{ "name": "render", … }`
    # object (balanced to a single `}`).
    perl -i -0pe 's{,?\s*\{\s*"name"\s*:\s*"render"[^{}]*(?:\{[^{}]*\}[^{}]*)*\}}{}sg' "$file"

    # Cleanup pass: collapse any blank lines left between `}` (end of
    # the preceding module) and `]` (close of the modules array).
    perl -i -0pe 's{(\})\s*\n(\s*\n)+(\s*\])}{$1\n$3}sg' "$file"

    if [ "$MODE" = "source" ]; then
        # Insert the render module right before the LAST `  ]\n}` tail
        # (the closing of the `modules` array). We split the file at
        # that marker via awk — detecting the terminator by looking at
        # the second-to-last and last non-empty lines — and sandwich
        # the snippet in between. Not dependent on any regex escaping.
        local tmp="$file.tmp.$$"
        python3 - "$file" "$tmp" <<'PY'
import pathlib, sys, re
src = pathlib.Path(sys.argv[1]).read_text()
# Find the LAST `  ]` line (closes the modules array) followed by `}` tail.
# We expect the file to end with:   `  ]\n}\n`  (optionally with trailing \n).
pattern = re.compile(r'(\n[ \t]*\])(\s*\n\})\s*\Z', re.MULTILINE)
m = pattern.search(src)
if not m:
    sys.exit(f"[apply-ohos-mode] cannot locate `modules]` tail in {sys.argv[1]}")
snippet = '''    {
      "name": "render",
      "srcPath": "../core-render-ohos",
      "targets": [
        {
          "name": "default",
          "applyToProducts": [
            "default"
          ]
        }
      ]
    }'''
# m.group(1) is the "\n  ]" that closes the modules array;
# we insert the snippet BEFORE it, with a leading `,\n` to extend the
# previous module entry.
out = src[:m.start()] + ',\n' + snippet + m.group(1) + m.group(2) + '\n'
pathlib.Path(sys.argv[2]).write_text(out)
PY
        mv "$tmp" "$file"
    fi
}

for app in ohosAppForNativePage ohosAppForKuiklyPage ohosAppForBothPages ohosAppForTest; do
    sync_build_profile "$SCRIPT_DIR/../$app/build-profile.json5"
done
echo "  ✅ build-profile.json5 (×4) synced"

echo "✅ apply-ohos-mode done"
