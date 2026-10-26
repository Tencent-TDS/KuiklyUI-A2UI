# frozen_string_literal: true
#
# ─────────────────────────────────────────────────────────────────────────
# a2ui_cocoapods.rb — single source of truth for how the four
# `iosAppFor*/Podfile` scripts in this repo consume **this repository's
# own** iOS render pod (`OpenKuiklyA2UIIOSRender`), under both source
# and artifact modes.
#
# Mirrors `buildSrc/src/main/java/A2uiDep.kt` on the Gradle side:
#   - `a2ui.useSource=true`  → `:path => '../'` (local podspec at the
#                              root of this very repo —
#                              `OpenKuiklyA2UIIOSRender.podspec`)
#   - `a2ui.useSource=false` → `'<a2ui.version>'` (artefact from
#                              CocoaPods trunk)
#
# Default is `source` (inverse of `kuikly.useSource`): A2UI lives in this
# repo so same-repo dev loop is the common case.
#
# Resolution order mirrors `A2uiDep.useSource`:
#   1. env var  (`A2UI_USE_SOURCE` / `A2UI_VERSION`)
#   2. `<A2UI-root>/local.properties`
#   3. `<A2UI-root>/gradle.properties`
#   4. defaults: useSource=true, version=A2UI::A2uiCocoapods::DEFAULT_A2UI_VERSION
#
# NOTE on scope: this helper only covers `OpenKuiklyA2UIIOSRender`. The
# KMP-generated `a2ui` / `a2ui_demo` pods are intentionally kept at
# `:path => '../a2ui'` / `'../demo'` in all four Podfiles — they are
# cocoapods-plugin-generated podspecs that trigger `syncFramework` on
# every Xcode build, which is the only sane way to consume KMP output
# from Xcode. (The upstream Kuikly repo exposes `OpenKuiklyIOSRender` the
# same way via `kuikly_cocoapods.rb`.)
#
# Idiomatic Podfile usage:
#
#   require_relative '../scripts/lib/a2ui_cocoapods'
#   a2ui_version = A2UI::A2uiCocoapods.render_pod_version(__dir__)
#   a2ui_options = A2UI::A2uiCocoapods.render_pod_options(__dir__)
#   if a2ui_version
#     pod 'OpenKuiklyA2UIIOSRender', a2ui_version
#   else
#     pod 'OpenKuiklyA2UIIOSRender', **a2ui_options
#   end
#
# ─────────────────────────────────────────────────────────────────────────

require_relative 'kuikly_path' # for KuiklyPath.read_prop

module A2UI
  module A2uiCocoapods
    USE_SOURCE_PROP_KEY = 'a2ui.useSource'
    USE_SOURCE_ENV_KEY  = 'A2UI_USE_SOURCE'

    VERSION_ENV_KEY     = 'A2UI_VERSION'

    # Keep in sync with `A2uiVersion.DEFAULT_A2UI_SEMANTIC` in
    # `buildSrc/src/main/java/A2uiVersion.kt` and with the fallback in
    # `OpenKuiklyA2UIIOSRender.podspec` at the repository root.
    DEFAULT_A2UI_VERSION = '1.0.0'

    module_function

    # Returns true iff the resolved `a2ui.useSource` is `true`.
    # Default when nothing is set: TRUE (source mode), matching
    # `A2uiDep.useSource`.
    def use_source?(podfile_dir)
      a2ui_root = File.expand_path('..', podfile_dir)
      raw = resolve_setting(a2ui_root, USE_SOURCE_ENV_KEY, USE_SOURCE_PROP_KEY)
      return true if raw.nil? || raw.empty?
      raw.strip.casecmp('true').zero?
    end

    # Resolved A2UI SemVer to use for the `OpenKuiklyA2UIIOSRender` pod
    # in artifact mode. Only the plain SemVer segment is used here (no
    # Kotlin suffix), matching the podspec's own `spec.version` scheme.
    # Strips any `-SNAPSHOT` tail since CocoaPods trunk never carries one.
    def version(podfile_dir)
      a2ui_root = File.expand_path('..', podfile_dir)
      raw = ENV[VERSION_ENV_KEY]
      if raw.nil? || raw.empty?
        # a2ui doesn't ship a gradle-property fallback (same choice as
        # A2uiVersion.getSemantic which is env-only), but we still honour
        # a `local.properties` / `gradle.properties` entry so power users
        # can pin a2ui.version alongside kuikly.version if they want.
        raw = A2UI::KuiklyPath.read_prop(File.join(a2ui_root, 'local.properties'), 'a2ui.version')
      end
      if raw.nil? || raw.empty?
        raw = A2UI::KuiklyPath.read_prop(File.join(a2ui_root, 'gradle.properties'), 'a2ui.version')
      end
      return DEFAULT_A2UI_VERSION if raw.nil? || raw.empty?
      raw.strip.sub(/-SNAPSHOT\z/, '')
    end

    # Returns the version String to pass as the second positional
    # argument of `pod 'OpenKuiklyA2UIIOSRender', <here>` in artifact
    # mode, or `nil` in source mode (caller should splat
    # `render_pod_options` instead).
    def render_pod_version(podfile_dir)
      return nil if use_source?(podfile_dir)
      version(podfile_dir)
    end

    # Returns the options Hash to splat into `pod
    # 'OpenKuiklyA2UIIOSRender', **<here>` in source mode
    # (`{ :path => '../' }`), or `{}` in artifact mode.
    #
    # The `:path` is deliberately returned as a RELATIVE string (as
    # written in the Podfile), because `OpenKuiklyA2UIIOSRender` lives in
    # THIS repository and CocoaPods resolves `:path` relative to the
    # Podfile directory. The podspec lives at the repo root (aligned
    # with `OpenKuiklyIOSRender.podspec` in the upstream Kuikly repo),
    # so `../` from an `iosAppFor*/Podfile` points right at it.
    def render_pod_options(podfile_dir)
      return {} unless use_source?(podfile_dir)
      { :path => '../' }
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
