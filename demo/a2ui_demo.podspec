Pod::Spec.new do |spec|
    spec.name                     = 'a2ui_demo'
    spec.version                  = '1.0.0'
    spec.homepage                 = 'https://github.com/Tencent-TDS/KuiklyUI-A2UI'
    spec.source                   = { :http=> ''}
    spec.authors                  = 'Kuikly Team'
    spec.license                  = { :type => 'KuiklyUI', :file => '../LICENSE' }
    spec.summary                  = 'A2UI DSL demo pages (KMP business sample — :path consumption only, not published)'
    spec.vendored_frameworks      = 'build/cocoapods/framework/A2UIDemo.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target    = '15.0'


    if !Dir.exist?('build/cocoapods/framework/A2UIDemo.framework') || Dir.empty?('build/cocoapods/framework/A2UIDemo.framework')
        raise "

        Kotlin framework 'A2UIDemo' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :demo:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end

    spec.xcconfig = {
        'ENABLE_USER_SCRIPT_SANDBOXING' => 'NO',
    }

    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':demo',
        'PRODUCT_MODULE_NAME' => 'A2UIDemo',
    }

    spec.script_phases = [
        {
            :name => 'Build a2ui_demo',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                # Standalone A2UI project: gradlew lives one level up
                # (A2UI repo root), not two.
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]

end
