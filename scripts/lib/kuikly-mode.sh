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
# trunk — only carries released versions). Default: 2.29.2.
#
# Mirrors:
#   - `KuiklyDep.kuiklyVersion` in buildSrc/.../KuiklyDep.kt
#   - `A2UI::KuiklyCocoapods.version` in scripts/lib/kuikly_cocoapods.rb
#
# Resolution order (first non-blank wins):
#   1. KUIKLY_VERSION env
#   2. <A2UI-root>/local.properties
#   3. <A2UI-root>/gradle.properties
#   4. Default: 2.29.2
# ─────────────────────────────────────────────────────────────────────────
kuikly_version() {
    local raw="${KUIKLY_VERSION:-}"
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/local.properties" kuikly.version || true)"
    fi
    if [ -z "$raw" ]; then
        raw="$(_km_read_prop "$A2UI_ROOT/gradle.properties" kuikly.version || true)"
    fi
    [ -z "$raw" ] && raw="2.29.2"
    echo "${raw%-SNAPSHOT}"
}
