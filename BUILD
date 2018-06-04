licenses(["notice"])  # Apache License 2.0

load(":builddefs.bzl", "java_agent_binary")

exports_files(
    ["builddefs.bzl"],
)

java_agent_binary(
    srcs = [],
    deploy_jar_name = "allocationinstrumenteragent_deploy.jar",
    library_name = "allocationinstrumenter",
    premain_class = "com.google.monitoring.runtime.instrumentation.AllocationInstrumenter",
    deps = [
        "//src/main/java/com/google/monitoring/runtime/instrumentation:allocation_instrumenter",
        "//src/main/java/com/google/monitoring/runtime/instrumentation:sampler",
        "//src/main/java/com/google/monitoring/runtime/instrumentation:staticclasswriter",
    ],
)

# Since some Java applications use different versions of ASM, we
# use JarJar to rename the ASM classes to something else, to avoid conflicts.
#
# Note that Flogger injection classes must not be renamed, because some injection strategies need
# the classes in the agent library to share information with the runtime library. This is safe
# however because none of the Flogger library itself is in the agent JAR, only the injection
# classes. We specify the entire "flogger" package as "non shaded" to mitigate the risk that the
# agent package (which is the only thing we actually care about) is ever renamed, as this would
# otherwise break log site injection in a very hard to understand way. Sadly there's no ability to
# add comments to the rules.txt files explaining this there.
filegroup(
    name = "jarjar_rules",
    srcs = ["rules.txt"],
)

filegroup(
    name = "bootstrap",
    srcs = ["//src/main/java/com/google/monitoring/runtime/instrumentation:Bootstrap.java.in"],
)
