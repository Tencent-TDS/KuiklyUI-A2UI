# frozen_string_literal: true
#
# ─────────────────────────────────────────────────────────────────────────
# kuikly_cocoapods.rb — single source of truth for how the four
# `iosAppFor*/Podfile` scripts in this repo consume the upstream
# `OpenKuiklyIOSRender` pod, under both source and artifact modes.
#
# Mirrors `buildSrc/src/main/java/KuiklyDep.kt` on the Gradle side:
#   - `kuikly.useSource=true`  → `:path => <local Kuikly checkout>`
#   - `kuikly.useSource=false` → `'<kuikly.version>'` (artifact from
#                                CocoaPods trunk)
#
# Resolution order for both switches is identical to KuiklyDep.kt:
#   1. env var  (`KUIKLY_USE_SOURCE` / `KUIKLY_VERSION`)
#   2. `<A2UI-root>/local.properties`
#   3. `<A2UI-root>/gradle.properties`
#   4. defaults (useSource=false, version=2.30.0)
#
# Usage from a Podfile (this file lives at
# `<A2UI-root>/scripts/lib/kuikly_cocoapods.rb`; each Podfile is at
# `<A2UI-root>/<iosAppForXxx>/Podfile`, i.e. one level below root):
#
#   require_relative '../scripts/lib/kuikly_cocoapods'
#   kuikly_dep = A2UI::KuiklyCocoapods.render_pod_args(__dir__)
#   pod 'OpenKuiklyIOSRender', **kuikly_dep
#
# `render_pod_args` returns a Hash suitable for CocoaPods' `pod` DSL:
#   - source  mode → `{ :path => '/abs/path/to/KuiklyUI' }`
#   - artifact mode → `{}` with the version baked into a separate return,
#                     but we instead return `{ :version => '2.30.0' }`
#                     and let the Podfile splat it — see below.
#
# Because CocoaPods' `pod` method does NOT accept a `:version` keyword
# (the version is a positional argument), we expose two helpers:
#
#   render_pod_version(podfile_dir)  → nil in source mode, "2.30.0" otherwise
#   render_pod_options(podfile_dir)  → { :path => ... } in source mode, {} otherwise
#
# Idiomatic Podfile usage:
#
#   version = A2UI::KuiklyCocoapods.render_pod_version(__dir__)
#   options = A2UI::KuiklyCocoapods.render_pod_options(__dir__)
#   if version
#     pod 'OpenKuiklyIOSRender', version
#   else
#     pod 'OpenKuiklyIOSRender', **options
#   end
#
# ─────────────────────────────────────────────────────────────────────────

require_relative 'kuikly_path'

module A2UI
  module KuiklyCocoapods
    USE_SOURCE_PROP_KEY = 'kuikly.useSource'
    USE_SOURCE_ENV_KEY  = 'KUIKLY_USE_SOURCE'

    VERSION_PROP_KEY    = 'kuikly.version'
    VERSION_ENV_KEY     = 'KUIKLY_VERSION'

    # Keep in sync with `KuiklyDep.DEFAULT_KUIKLY_VERSION` in
    # `buildSrc/src/main/java/KuiklyDep.kt`.
    DEFAULT_KUIKLY_VERSION = '2.30.0'

    module_function

    # Returns true iff the resolved `kuikly.useSource` is `true`.
    # Default when nothing is set: false (artifact mode), matching
    # `KuiklyDep.useSource`.
    def use_source?(podfile_dir)
      a2ui_root = File.expand_path('..', podfile_dir)
      raw = resolve_setting(a2ui_root, USE_SOURCE_ENV_KEY, USE_SOURCE_PROP_KEY)
      return false if raw.nil? || raw.empty?
      raw.strip.casecmp('true').zero?
    end

    # Resolved Kuikly version to use for the `OpenKuiklyIOSRender` pod in
    # artifact mode. Only the plain SemVer segment is used here — the
    # Kotlin suffix (e.g. `-2.1.21`) is NOT appended, because
    # `OpenKuiklyIOSRender` is a pure ObjC/C++ pod and ships to CocoaPods
    # trunk with plain SemVer tags (see Kuikly's
    # `OpenKuiklyIOSRender.podspec`: `spec.version = '2.30.0'`).
    def version(podfile_dir)
      a2ui_root = File.expand_path('..', podfile_dir)
      raw = resolve_setting(a2ui_root, VERSION_ENV_KEY, VERSION_PROP_KEY)
      return DEFAULT_KUIKLY_VERSION if raw.nil? || raw.empty?
      # Strip any `-SNAPSHOT` tail — CocoaPods trunk never carries one.
      raw.strip.sub(/-SNAPSHOT\z/, '')
    end

    # Returns the version String to pass as the second positional
    # argument of `pod 'OpenKuiklyIOSRender', <here>` in artifact mode,
    # or `nil` in source mode (caller should use `render_pod_options`
    # instead).
    def render_pod_version(podfile_dir)
      return nil if use_source?(podfile_dir)
      version(podfile_dir)
    end

    # Returns the options Hash to splat into `pod 'OpenKuiklyIOSRender',
    # **<here>` in source mode (`{ :path => '/abs' }`), or `{}` in
    # artifact mode.
    def render_pod_options(podfile_dir)
      return {} unless use_source?(podfile_dir)
      kuikly_root = A2UI::KuiklyPath.resolve!(podfile_dir)
      { :path => kuikly_root }
    end

    # ── Internal: unified env + local/gradle.properties lookup ───────

    def resolve_setting(a2ui_root, env_key, prop_key)
      raw = ENV[env_key]
      return raw if !raw.nil? && !raw.empty?
      raw = A2UI::KuiklyPath.read_prop(File.join(a2ui_root, 'local.properties'), prop_key)
      return raw if !raw.nil? && !raw.empty?
      A2UI::KuiklyPath.read_prop(File.join(a2ui_root, 'gradle.properties'), prop_key)
    end
  end
end
