SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "sh path: $SCRIPT_DIR"
echo "project's root path: $PROJECT_ROOT"

cd "$PROJECT_ROOT" || { echo "Can't cd project's root path: $PROJECT_ROOT"; exit 1; }

java -version

CONFIG_FILE="publish/compatible/2.0_ohos.yaml"

# ─────────────────────────────────────────────────────────────────────
# Enforce artifact mode for publishing.
#
# `:a2ui` must be published against the already-released Kuikly
# artifacts (`com.tencent.kuikly-open:core:…` with the `-ohos` Kotlin
# segment, resolved by `KuiklyDep.kuiklyCoordinateVersion`) so the
# generated POMs carry Maven coordinates — not local `project(":core")`
# references. Overriding this via env is intentionally disabled;
# developer-side source-mode validation should go through a plain
# `./gradlew publishToMavenLocal` run without this script.
# ─────────────────────────────────────────────────────────────────────
export KUIKLY_USE_SOURCE=false

# 兼容性替换
java publish/FileReplacer.java replace "$CONFIG_FILE"

MODULE=${1:-all}
PUBLISH_TASK=${2:-publishToMavenLocal}
GRADLE_RUN_STATUS=0

if [ "$MODULE" = "all" ]; then
  echo "编译所有模块 a2ui"
  echo "发布方式: $PUBLISH_TASK"
  KUIKLY_AGP_VERSION="7.4.2" KUIKLY_KOTLIN_VERSION="2.0.21-KBA-010" ./gradlew -c settings.2.0.ohos.gradle.kts :a2ui:$PUBLISH_TASK --stacktrace

else
  echo "编译模块: $MODULE"
  echo "发布方式: $PUBLISH_TASK"
  KUIKLY_AGP_VERSION="7.4.2" KUIKLY_KOTLIN_VERSION="2.0.21-KBA-010" ./gradlew -c settings.2.0.ohos.gradle.kts :$MODULE:$PUBLISH_TASK --stacktrace
  GRADLE_RUN_STATUS=$?
fi

# 兼容性还原
java publish/FileReplacer.java restore "$CONFIG_FILE"

if [ $GRADLE_RUN_STATUS -eq 0 ]; then
  exit 0
else
  exit 1
fi
