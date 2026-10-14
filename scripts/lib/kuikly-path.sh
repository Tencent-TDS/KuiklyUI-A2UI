#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────
# kuikly-path.sh — SINGLE SOURCE OF TRUTH for the path to the external
# KuiklyUI project, for shell scripts (setup.sh / ohos build scripts) and
# CocoaPods Podfiles (Ruby can `` this via shelling out).
#
# Resolution order (first match wins):
#   1. KUIKLY_PROJECT_DIR environment variable
#   2. `kuikly.projectDir` in <A2UI-root>/local.properties
#   3. `kuikly.projectDir` in <A2UI-root>/gradle.properties
#
# Usage:
#   source "<A2UI-root>/scripts/lib/kuikly-path.sh"
#   echo "$KUIKLY_PROJECT_DIR"
#
# Or (one-shot):
#   KUIKLY_PROJECT_DIR="$(bash <A2UI-root>/scripts/lib/kuikly-path.sh)"
#
# All paths returned are absolute (canonicalised).
# ─────────────────────────────────────────────────────────────────────────

# Determine the A2UI project root (2 levels above this file).
_KP_SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" && pwd)"
A2UI_ROOT="$(cd "$_KP_SCRIPT_DIR/../.." && pwd)"

_read_prop() {
    local file="$1"
    local key="$2"
    [ -f "$file" ] || return 1
    # Match `key=value` (allow leading whitespace, ignore `#` comments).
    local line
    line="$(grep -E "^[[:space:]]*${key}[[:space:]]*=" "$file" | grep -v '^[[:space:]]*#' | tail -n 1)"
    [ -n "$line" ] || return 1
    echo "${line#*=}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//'
}

_resolve_abs() {
    local raw="$1"
    if [[ "$raw" = /* ]]; then
        echo "$raw"
    else
        (cd "$A2UI_ROOT" && cd "$raw" && pwd)
    fi
}

if [ -n "$KUIKLY_PROJECT_DIR" ]; then
    _kp_raw="$KUIKLY_PROJECT_DIR"
else
    _kp_raw="$(_read_prop "$A2UI_ROOT/local.properties" kuikly.projectDir || true)"
    if [ -z "$_kp_raw" ]; then
        _kp_raw="$(_read_prop "$A2UI_ROOT/gradle.properties" kuikly.projectDir || true)"
    fi
fi

if [ -z "$_kp_raw" ]; then
    echo "ERROR: kuikly.projectDir is not configured." >&2
    echo "  Set it in <A2UI-root>/local.properties or gradle.properties," >&2
    echo "  or export KUIKLY_PROJECT_DIR=..." >&2
    return 1 2>/dev/null || exit 1
fi

KUIKLY_PROJECT_DIR="$(_resolve_abs "$_kp_raw")"

if [ ! -d "$KUIKLY_PROJECT_DIR/core" ]; then
    echo "ERROR: KUIKLY_PROJECT_DIR does not look like a KuiklyUI checkout:" >&2
    echo "       $KUIKLY_PROJECT_DIR" >&2
    echo "       (expected sub-directory 'core/' not found)" >&2
    return 1 2>/dev/null || exit 1
fi

export A2UI_ROOT
export KUIKLY_PROJECT_DIR

# When invoked directly (not `source`d), print the path so callers can
# `KUIKLY_PROJECT_DIR="$(bash kuikly-path.sh)"`.
if [ "${BASH_SOURCE[0]:-}" = "${0}" ]; then
    echo "$KUIKLY_PROJECT_DIR"
fi
