#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────
# kuikly-mode.sh — single source of truth for the `kuikly.useSource`
# switch on the shell side, mirroring:
#   - `KuiklyDep.useSource` in `buildSrc/src/main/java/KuiklyDep.kt`
#   - `A2UI::KuiklyCocoapods.use_source?` in
#     `scripts/lib/kuikly_cocoapods.rb`
#
# Resolution order (first non-blank wins):
#   1. `KUIKLY_USE_SOURCE` environment variable
#   2. `kuikly.useSource` in <A2UI-root>/local.properties
#   3. `kuikly.useSource` in <A2UI-root>/gradle.properties
#   4. Default: false (artifact mode)
#
# Usage:
#   source "<A2UI-root>/scripts/lib/kuikly-mode.sh"
#   if kuikly_use_source; then
#       bash "$SCRIPT_DIR/../scripts/link-kuikly.sh"
#   fi
#
# Expects `A2UI_ROOT` to be set (as `kuikly-path.sh` does), or will
# compute it from this file's location.
# ─────────────────────────────────────────────────────────────────────────

if [ -z "${A2UI_ROOT:-}" ]; then
    _KM_SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" && pwd)"
    A2UI_ROOT="$(cd "$_KM_SCRIPT_DIR/../.." && pwd)"
    export A2UI_ROOT
fi

_km_read_prop() {
    local file="$1"
    local key="$2"
    [ -f "$file" ] || return 1
    local line
    line="$(grep -E "^[[:space:]]*${key}[[:space:]]*=" "$file" | grep -v '^[[:space:]]*#' | tail -n 1)"
    [ -n "$line" ] || return 1
    echo "${line#*=}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//'
}

# Returns 0 (success) when source mode is enabled, 1 otherwise.
kuikly_use_source() {
    local raw="${KUIKLY_USE_SOURCE:-}"
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/local.properties" kuikly.useSource || true)"
    fi
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/gradle.properties" kuikly.useSource || true)"
    fi
    # Lower-case + trim.
    raw="$(echo "$raw" | tr '[:upper:]' '[:lower:]' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
    [ "$raw" = "true" ]
}

# ─────────────────────────────────────────────────────────────────────────
# Resolve `kuikly.version` and echo it on stdout (plain SemVer; any
# `-SNAPSHOT` tail is stripped, since the ohpm registry — like CocoaPods
# trunk — only carries released versions). Default: 2.30.0.
#
# Mirrors:
#   - `KuiklyDep.kuiklyVersion` in buildSrc/.../KuiklyDep.kt
#   - `A2UI::KuiklyCocoapods.version` in scripts/lib/kuikly_cocoapods.rb
#
# Resolution order (first non-blank wins):
#   1. KUIKLY_VERSION env
#   2. <A2UI-root>/local.properties
#   3. <A2UI-root>/gradle.properties
#   4. Default: 2.30.0
# ─────────────────────────────────────────────────────────────────────────
kuikly_version() {
    local raw="${KUIKLY_VERSION:-}"
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/local.properties" kuikly.version || true)"
    fi
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/gradle.properties" kuikly.version || true)"
    fi
    [ -z "$raw" ] && raw="2.30.0"
    echo "${raw%-SNAPSHOT}"
}

# ─────────────────────────────────────────────────────────────────────────
# A2UI side — dual-mode switch for THIS repository's own publications
# (`:a2ui`, `:a2ui-render-android`, `OpenKuiklyA2UIIOSRender`,
# `@kuikly-a2ui-open/render`).
#
# Mirrors:
#   - `A2uiDep.useSource` in `buildSrc/src/main/java/A2uiDep.kt`
#   - `A2UI::A2uiCocoapods.use_source?` in `scripts/lib/a2ui_cocoapods.rb`
#
# Default is TRUE (source mode) — opposite of `kuikly.useSource` —
# because A2UI lives in this very repo so same-repo dev loop is common.
#
# Resolution order (first non-blank wins):
#   1. `A2UI_USE_SOURCE` environment variable
#   2. `a2ui.useSource` in <A2UI-root>/local.properties
#   3. `a2ui.useSource` in <A2UI-root>/gradle.properties
#   4. Default: true (source mode)
# ─────────────────────────────────────────────────────────────────────────
a2ui_use_source() {
    local raw="${A2UI_USE_SOURCE:-}"
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/local.properties" a2ui.useSource || true)"
    fi
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/gradle.properties" a2ui.useSource || true)"
    fi
    raw="$(echo "$raw" | tr '[:upper:]' '[:lower:]' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
    # Default true: unset / blank → source mode.
    [ -z "$raw" ] || [ "$raw" = "true" ]
}

# ─────────────────────────────────────────────────────────────────────────
# Resolve A2UI's own SemVer (used by the ohpm artefact dependency in
# `@kuikly-a2ui-open/render`-consumer oh-package.json5 files, and
# potentially the iOS pod when wired up in future). Default 1.0.0.
#
# Mirrors:
#   - `A2uiVersion.getSemantic` in buildSrc/.../A2uiVersion.kt
#   - `A2UI::A2uiCocoapods.version` in scripts/lib/a2ui_cocoapods.rb
#
# Resolution order (first non-blank wins):
#   1. A2UI_VERSION env
#   2. <A2UI-root>/local.properties → a2ui.version
#   3. <A2UI-root>/gradle.properties → a2ui.version
#   4. Default: 1.0.0
# Any `-SNAPSHOT` tail is stripped (ohpm registry carries only released
# versions).
# ─────────────────────────────────────────────────────────────────────────
a2ui_version() {
    local raw="${A2UI_VERSION:-}"
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/local.properties" a2ui.version || true)"
    fi
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/gradle.properties" a2ui.version || true)"
    fi
    [ -z "$raw" ] && raw="1.0.0"
    echo "${raw%-SNAPSHOT}"
}
