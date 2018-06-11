# Tests for bytecode rewriting functionality in c.g.m.r.instrumentation.

licenses(["notice"])  # Apache License 2.0

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

java_test(
    name = "InstrumenterRefTest",
    srcs = ["InstrumenterRefTest.java"],
    data = ["//:allocationinstrumenteragent_deploy.jar"],
    jvm_flags = [
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

# TODO(java-platform-team): Move to third_party
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
    javacopts = ["-Xlint:all"],
    jvm_flags = ["-javaagent:$(location //:allocationinstrumenteragent_deploy.jar)=manualOnly,subclassesAlso"],
    deps = [
        ":compile_only_agent",
        "//src/main/java/com/google/monitoring/runtime/instrumentation:sampler",
        "@google_bazel_common//third_party/java/junit",
    ],
)
