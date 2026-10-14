# frozen_string_literal: true
#
# ─────────────────────────────────────────────────────────────────────────
# kuikly_path.rb — SINGLE SOURCE OF TRUTH for the path to the external
# KuiklyUI project, for CocoaPods Podfiles.
#
# Mirrors `scripts/lib/kuikly-path.sh`. Resolution order (first match wins):
#   1. `KUIKLY_PROJECT_DIR` environment variable
#   2. `kuikly.projectDir` in <A2UI-root>/local.properties
#   3. `kuikly.projectDir` in <A2UI-root>/gradle.properties
#
# Usage from a Podfile (this file lives at
# `<A2UI-root>/scripts/lib/kuikly_path.rb`; each Podfile is at
# `<A2UI-root>/<iosAppForXxx>/Podfile`, i.e. one level below root):
#
#   require_relative '../scripts/lib/kuikly_path'
#   kuikly_root = A2UI::KuiklyPath.resolve!(__dir__)
#
# Returns an absolute String path.
# ─────────────────────────────────────────────────────────────────────────

module A2UI
  module KuiklyPath
    PROP_KEY = 'kuikly.projectDir'
    ENV_KEY  = 'KUIKLY_PROJECT_DIR'

    module_function

    # `podfile_dir` = `__dir__` inside the caller Podfile (absolute path).
    # The A2UI project root is that dir's parent (each iosAppForXxx sits
    # directly under the A2UI root).
    def resolve!(podfile_dir)
      a2ui_root = File.expand_path('..', podfile_dir)
      raw = ENV[ENV_KEY]
      raw = read_prop(File.join(a2ui_root, 'local.properties'), PROP_KEY) if raw.nil? || raw.empty?
      raw = read_prop(File.join(a2ui_root, 'gradle.properties'), PROP_KEY) if raw.nil? || raw.empty?

      if raw.nil? || raw.empty?
        abort <<~MSG
          [A2UI] kuikly.projectDir is not configured.
                 Set it in <A2UI-root>/local.properties or gradle.properties,
                 or export #{ENV_KEY}=... before running pod install.
        MSG
      end

      resolved =
        if File.absolute_path?(raw)
          raw
        else
          File.expand_path(raw, a2ui_root)
        end

      unless Dir.exist?(File.join(resolved, 'core'))
        abort <<~MSG
          [A2UI] #{ENV_KEY}=#{resolved} does not look like a KuiklyUI checkout
                 (expected sub-directory 'core/' not found).
        MSG
      end

      resolved
    end

    def read_prop(file, key)
      return nil unless File.exist?(file)
      File.foreach(file) do |line|
        stripped = line.strip
        next if stripped.empty? || stripped.start_with?('#')
        if stripped.start_with?("#{key}=")
          return stripped.split('=', 2)[1].strip
        end
      end
      nil
    end
  end
end
