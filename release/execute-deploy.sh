#!/bin/bash
set -eu

# Delegate to bazel-common's execute-deploy.sh helper
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_BASE="$(bazel info output_base 2>/dev/null || echo "")"

COMMON_DEPLOY_SCRIPT=""
candidate_paths=(
  "${SCRIPT_DIR}/../../../bazel-common-gray/bazel-common/tools/maven/execute-deploy.sh"
  "${SCRIPT_DIR}/../../bazel-common-gray/bazel-common/tools/maven/execute-deploy.sh"
  "${OUTPUT_BASE}/external/google_bazel_common/tools/maven/execute-deploy.sh"
  "${OUTPUT_BASE}/external/google_bazel_common+/tools/maven/execute-deploy.sh"
  "${OUTPUT_BASE}/external/google_bazel_common~/tools/maven/execute-deploy.sh"
)

for path in "${candidate_paths[@]}"; do
  if [ -f "$path" ]; then
    COMMON_DEPLOY_SCRIPT="$path"
    break
  fi
done

if [ -z "$COMMON_DEPLOY_SCRIPT" ]; then
  echo "Error: Could not locate bazel-common execute-deploy.sh script" >&2
  exit 1
fi

source "$COMMON_DEPLOY_SCRIPT"

deploy_library \
  allocationinstrumenteragent_deploy.jar \
  src/main/java/com/google/monitoring/runtime/instrumentation/liballocation_instrumenter_all-src.jar \
  src/main/java/com/google/monitoring/runtime/instrumentation/javadoc.jar \
  pom.xml
