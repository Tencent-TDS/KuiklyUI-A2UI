Pod::Spec.new do |spec|
  spec.name             = "OpenA2UIIOSRender"
  # KuiklyUI-A2UI standalone project — version resolution order
  # (mirrors A2uiVersion.getSemantic() in buildSrc/):
  #   1. env `A2UI_VERSION`
  #   2. default '0.9.0-SNAPSHOT'
  #
  # CocoaPods enforces strict SemVer, so we deliberately keep the plain
  # semantic form (no Kotlin suffix). The Kotlin toolchain is instead
  # pinned by whichever build the shipped XCFramework was produced from.
  #
  # NOTE: this repository does not ship an automated iOS pod publish
  # script (aligned with the upstream Kuikly project). To publish, run
  # `pod trunk push OpenA2UIIOSRender.podspec --allow-warnings` manually
  # from this directory (optionally with `A2UI_VERSION=x.y.z` prefixed).
  # The default value above should be kept in sync with
  # `DEFAULT_A2UI_SEMANTIC` in buildSrc/src/main/java/A2uiVersion.kt.
  spec.version          = ENV['A2UI_VERSION'] || '0.9.0-SNAPSHOT'
  spec.summary          = "A2UI iOS Render Module"
  spec.description      = <<-DESC
                        A2UI iOS platform render implementation.
                        Bridges A2UI message stream to Kuikly's native render pipeline
                        using KuiklyRenderLayerHandler with proper threading model.
                        DESC
  spec.homepage         = "https://github.com/Tencent-TDS/KuiklyUI-A2UI"
  # `:file => "../LICENSE"` resolves to the standalone project's root LICENSE
  # (KuiklyUI license text) since this podspec lives under `a2ui-render-ios/`.
  spec.license          = { :type => "KuiklyUI", :file => "../LICENSE" }
  spec.author           = "Kuikly Team"
  spec.ios.deployment_target = '15.0'
  spec.source           = { :git => "https://github.com/Tencent-TDS/KuiklyUI-A2UI.git", :tag => "#{spec.version}" }
  spec.user_target_xcconfig = { 'CLANG_ALLOW_NON_MODULAR_INCLUDES_IN_FRAMEWORK_MODULES' => 'YES' }
  spec.requires_arc     = true

  spec.source_files     = 'Sources/**/*.{h,m,mm,swift}'
  spec.public_header_files = 'Sources/**/*.h'

  # Pin to the Kuikly Core version this A2UI release was built against.
  # Keep in sync with:
  #   - `KuiklyDep.DEFAULT_KUIKLY_VERSION` in
  #     `buildSrc/src/main/java/KuiklyDep.kt` (Android/KMP side)
  #   - `A2UI::KuiklyCocoapods::DEFAULT_KUIKLY_VERSION` in
  #     `scripts/lib/kuikly_cocoapods.rb` (Podfile side)
  # Override via `KUIKLY_VERSION=x.y.z pod trunk push …` at publish time.
  # `OpenKuiklyIOSRender` ships to CocoaPods trunk with plain SemVer tags
  # (no Kotlin suffix — it's a pure ObjC/C++ pod), so we strip any
  # `-SNAPSHOT` tail if the env var carries one.
  _kuikly_version = (ENV['KUIKLY_VERSION'] || '2.29.2').sub(/-SNAPSHOT\z/, '')
  spec.dependency 'OpenKuiklyIOSRender', _kuikly_version

  # Keep ObjC-only classes (notably `A2UIBridgeModule`, discovered by
  # `KuiklyRenderLayerHandler` via `NSClassFromString` at runtime) alive
  # when this pod is linked as a static library. Without `-ObjC`, the
  # linker's dead-strip pass can drop the module class on apps that
  # never explicitly reference it from Swift, which then triggers the
  # `NSAssert(NO, "创建Module失败 … ")` crash inside Kuikly's module
  # resolver for A2UI module calls.
  #
  # The matching KMP-side `objc_lookUpClass` pre-check
  # (`isA2UIBridgeModuleAvailable` in `Platform.ios.kt`) still protects
  # us if a host still manages to strip the class, but we want that
  # path to be rare, not routine.
  spec.pod_target_xcconfig = {
    'OTHER_LDFLAGS' => '-ObjC'
  }
  # NOTE: We intentionally do NOT declare `spec.dependency 'a2ui'` here.
  #
  # This render pod bridges to Kotlin/Native purely through runtime
  # reflection (`NSClassFromString(\"A2UICoreEntry\")` and
  # `NSClassFromString(\"A2UICustomViewRegistryBridge\")`), so it works
  # against *either*:
  #   - `A2UICore.framework`  (Native-page scenario, `pod 'a2ui'`)
  #   - `A2UIDemo.framework`  (Kuikly-page scenario, `pod 'a2ui_demo'`)
  # whichever the host app happens to ship. The host app's Podfile is
  # responsible for including exactly one of those pods — declaring the
  # dependency here would force both to be linked into apps that only
  # need one, which then triggers Kotlin stdlib duplicate-symbol errors.

  spec.ios.frameworks = [
    'UIKit',
    'QuartzCore',
    'CoreGraphics',
    'Foundation'
  ]
end
