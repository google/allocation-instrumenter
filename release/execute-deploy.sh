#!/bin/sh
set -eu

readonly MVN_GOAL="$1"
readonly VERSION_NAME="$2"
shift 2
readonly EXTRA_MAVEN_ARGS=("$@")

bazel_output_file() {
  library=$1
  library_output=bazel-bin/$library
  if [[ ! -e $library_output ]]; then
     library_output=bazel-genfiles/$library
  fi
  if [[ ! -e $library_output ]]; then
    echo "Could not find bazel output file for $library"
    exit 1
  fi
  echo -n $library_output
}

deploy_library() {
  library=$1
  srcjar=$2
  javadoc=$3
  pomfile=$4
  bazelisk build --define=pom_version="$VERSION_NAME" \
    $library $srcjar $javadoc $pomfile

  mvn $MVN_GOAL \
    -Dfile=$(bazel_output_file $library) \
    -Djavadoc=$(bazel_output_file $javadoc) \
    -DpomFile=$(bazel_output_file $pomfile) \
    -Dsources=$(bazel_output_file $srcjar) \
    "${EXTRA_MAVEN_ARGS[@]:+${EXTRA_MAVEN_ARGS[@]}}"
}

deploy_library \
  allocationinstrumenteragent_deploy.jar \
  src/main/java/com/google/monitoring/runtime/instrumentation/liballocation_instrumenter_all-src.jar \
  src/main/java/com/google/monitoring/runtime/instrumentation/javadoc.jar \
  pom.xml
