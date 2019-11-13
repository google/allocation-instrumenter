licenses(["notice"])  # Apache License 2.0

package(default_visibility = [
    "//visibility:public",
])

filegroup(
    name = "bootstrap",
    srcs = ["Bootstrap.java.in"],
)

java_library(
    name = "verifying_class_adapter",
    srcs = ["VerifyingClassAdapter.java"],
    deps = [
        "@google_bazel_common//third_party/java/asm",
        "@google_bazel_common//third_party/java/asm:asm-commons",
        "@google_bazel_common//third_party/java/asm:asm-tree",
    ],
)

java_library(
    name = "sampler",
    srcs = [
        "ConstructorCallback.java",
        "Sampler.java",
    ],
)

java_library(
    name = "allocation_instrumenter",
    srcs = [
        "AllocationClassAdapter.java",
        "AllocationInstrumenter.java",
        "AllocationMethodAdapter.java",
        "AllocationRecorder.java",
        "ConstructorInstrumenter.java",
    ],
    deps = [
        ":collect.jar",
        ":sampler",
        ":staticclasswriter",
        ":verifying_class_adapter",
        "@google_bazel_common//third_party/java/asm",
        "@google_bazel_common//third_party/java/asm:asm-commons",
    ],
)

java_library(
    name = "staticclasswriter",
    srcs = ["StaticClassWriter.java"],
    deps = [
        "@google_bazel_common//third_party/java/asm",
    ],
)

## This generates exactly what we need from Guava, and no more.

java_binary(
    name = "collect_base",
    main_class = "NotSuitableAsAMain",
    runtime_deps = [
        "@google_bazel_common//third_party/java/guava",
    ],
)

filegroup(
    name = "jarjar_collect",
    srcs = ["jarjar_collect.txt"],
)

JARJAR = "@bazel_tools//third_party/jarjar:jarjar_bin"

genrule(
    name = "collect_no_classes",
    srcs = ["collect_base_deploy.jar"],
    outs = ["collect_no_classes.jar"],
    cmd = "$(location " + JARJAR + ") process $(location :jarjar_collect) '$<' '$@'",
    tools = [
        ":jarjar_collect",
        JARJAR,
        "@google_bazel_common//third_party/java/guava",
    ],
)

genrule(
    name = "collect",
    srcs = ["collect_no_classes.jar"],
    outs = ["collect.jar"],
    cmd = "JAR=$(JAVABASE)/bin/jar; $${JAR} xf $< && $${JAR} cf $@ $$(find . -type f  -regex \".*.class\" -or -regex \".*META-INF.*\")",
    toolchains = ["@bazel_tools//tools/jdk:current_host_java_runtime"],
)

## end common/collect generation.
exports_files(["Bootstrap.java.in"])
