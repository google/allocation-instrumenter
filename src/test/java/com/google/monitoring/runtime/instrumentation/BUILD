# Tests for bytecode rewriting functionality in c.g.m.r.instrumentation.

load("@rules_java//java:defs.bzl", "java_binary", "java_import", "java_library", "java_test")
load("@rules_shell//shell:sh_binary.bzl", "sh_binary")
load("@rules_shell//shell:sh_test.bzl", "sh_test")

licenses(["notice"])

java_library(
    name = "helpers",
    srcs = [
        "Instrumentee.java",
        "InstrumentingClassLoader.java",
    ],
    javacopts = ["-Xep:BetaApi:OFF"],
    deps = [
        "//src/main/java/com/google/monitoring/runtime/instrumentation:allocation_instrumenter",
        "@google_bazel_common//third_party/java/guava",
    ],
)

# This is used for tests that compile against the agent and then load
# it via a command line flag.  This is the "compile against the agent"
# part, and the command line flag is in the given test rule.
java_import(
    name = "compile_only_agent",
    jars = ["//:allocationinstrumenteragent_deploy.jar"],
    neverlink = 1,
    tags = [
        "incomplete-deps",  # remove this suppression and add any missing deps, see go/java-import-deps-checking-lsc
    ],
)

sh_binary(
    name = "build_big_java",
    srcs = ["build_big_java.sh"],
)

genrule(
    name = "muchtoobig",
    outs = ["MuchTooBig.java"],
    cmd = "$(location :build_big_java) $(OUTS)",
    tools = [":build_big_java"],
)

java_test(
    name = "AllocationInstrumenterTest",
    srcs =
        [
            "AllocationInstrumenterTest.java",
            ":muchtoobig",
        ],
    # AllocationInstrumenterTest.testTooBig uses a lot of memory
    jvm_flags = [
        "-Xmx1024m",
        "-Xms1024m",
    ],
    deps = [
        ":helpers",
        "@google_bazel_common//third_party/java/junit",
    ],
)

java_binary(
    name = "allocationinstrumenterverifier",
    srcs = [
        "AllocationInstrumenterVerifier.java",
    ],
    main_class = ("com.google.monitoring.runtime.instrumentation." +
                  "AllocationInstrumenterVerifier"),
    deps = [
        ":helpers",
        "@google_bazel_common//third_party/java/apache_bcel",
    ],
)

java_test(
    name = "InstrumenterRefTest",
    srcs = ["InstrumenterRefTest.java"],
    data = ["//:allocationinstrumenteragent_deploy.jar"],
    jvm_flags = [
        # Prevents test failure under fastdebug JVM with java_test's default -Xverify:none.
        "-Xverify:remote",
        "-javaagent:$(location //:allocationinstrumenteragent_deploy.jar)",
    ],
    deps = [
        "@google_bazel_common//third_party/java/guava:testlib",
        "@google_bazel_common//third_party/java/junit",
    ],
)

### Begin tests of agent integrity / build data stripping

# We avoid making this a java_test because that comes with ASM, which
# throws the test off.
java_binary(
    name = "AgentIntegrity",
    srcs = ["AgentIntegrity.java"],
    javacopts = ["-Xlint:all"],
    main_class = "com.google.monitoring.runtime.instrumentation.AgentIntegrity",
)

sh_test(
    name = "agent_integrity_test",
    size = "small",
    timeout = "long",
    srcs = ["agent_integrity_test.sh"],
    args = [
        "$(location :AgentIntegrity) -javaagent:$(location //:allocationinstrumenteragent_deploy.jar)",
    ],
    data = [
        ":AgentIntegrity",
        ":AgentIntegrity_deploy.jar",
        "//:allocationinstrumenteragent_deploy.jar",
    ],
)

### End tests of agent integrity / build data stripping.

java_test(
    name = "MetaspaceExhaustionTest",
    srcs = ["MetaspaceExhaustionTest.java"],
    data = ["//:allocationinstrumenteragent_deploy.jar"],
    javacopts = ["-Xlint:all"],
    jvm_flags = [
        "-javaagent:$(location //:allocationinstrumenteragent_deploy.jar)=manualOnly,subclassesAlso",
        "-XX:MaxMetaspaceSize=32m",
    ],
    deps = [
        ":compile_only_agent",
        "//src/main/java/com/google/monitoring/runtime/instrumentation:sampler",
        "@google_bazel_common//third_party/java/asm",
        "@google_bazel_common//third_party/java/junit",
    ],
)

java_test(
    name = "ConstructorInstrumenterTest",
    srcs = ["ConstructorInstrumenterTest.java"],
    data = ["//:allocationinstrumenteragent_deploy.jar"],
    javacopts = [
        "-Xlint:all",
    ],
    jvm_flags = [
        # Prevents test failure under fastdebug JVM with java_test's default -Xverify:none.
        "-Xverify:remote",
        "-javaagent:$(location //:allocationinstrumenteragent_deploy.jar)=manualOnly,subclassesAlso",
    ],
    deps = [
        ":compile_only_agent",
        "//src/main/java/com/google/monitoring/runtime/instrumentation:sampler",
        "@google_bazel_common//third_party/java/junit",
    ],
)
