"""BUILD rules to construct a java agent.

All this is unfortunately necessary.  Some of the machinery is located at

  //java/com/google/monitoring/runtime/instrumentation/BUILD

but you should be able to use the java_agent_binary by itself; just
add yourself to the java_agent_builddef_users package group.
"""

# expand_template is present in a standard template in google3, but not in open-source.
def expand_template_impl(ctx):
    ctx.actions.expand_template(
        template = ctx.file.template,
        output = ctx.outputs.out,
        substitutions = ctx.attr.substitutions,
        is_executable = ctx.attr.is_executable,
    )

expand_template = rule(
    implementation = expand_template_impl,
    attrs = {
        "template": attr.label(mandatory = True, allow_single_file = True),
        "substitutions": attr.string_dict(mandatory = True),
        "out": attr.output(mandatory = True),
        "is_executable": attr.bool(default = False, mandatory = False),
    },
)

def java_agent_binary(
        library_name,
        deploy_jar_name,
        premain_class,
        srcs,
        deps,
        visibility = ["//visibility:private"],
        compatible_with = None,
        resources = None,
        extra_runtime_deps = [],
        **kwds):
    """Builddef to create a Java instrumentation agent cleanly.

    A large part of what this is doing is doing surgery on a java_binary
    so it won't, for example, interfere with build data or override
    existing classes in your application.

    Args:
         library_name: The name of the agent.
         deploy_jar_name: The name of the resulting deploy JAR.
         premain_class: The class to be run prior to main().  See the
           documentation for java.lang.instrument for more information.
      extra_runtime_deps: runtime_deps added to java_binary or java_test calls
    """

    simple_class_name = premain_class[premain_class.rfind(".") + 1:]

    package = premain_class[:premain_class.rfind(".")]

    path_to_bootstrap = premain_class.replace(".", "/") + "Bootstrap.class"

    bootstrap_file = simple_class_name + "Bootstrap.java"

    # See note in Bootstrap.java.in to explain this.
    expand_template(
        name = library_name + "_bootstrap",
        template = ":bootstrap",
        out = bootstrap_file,
        substitutions = {
            "CLASS_NAME": simple_class_name,
            "PACKAGE": package,
            "PATH_TO_CLASS": path_to_bootstrap,
            "PREMAIN_CLASS": premain_class,
            "GENERATOR": "allocation_instrumenter/builddefs.bzl",
        },
        compatible_with = compatible_with,
    )

    if srcs != []:
        native.java_library(
            name = library_name,
            srcs = srcs,
            javacopts = [
                # ASM has some deprecated methods we really shouldn't be
                # calling at all.
                "-Xlint:deprecation",
                "-Werror",
            ],
            visibility = visibility,
            deps = deps,
            compatible_with = compatible_with,
            **kwds
        )
    else:
        native.java_library(
            name = library_name,
            exports = deps,
            compatible_with = compatible_with,
            **kwds
        )

    native.java_binary(
        name = library_name + "_internal",
        srcs = [bootstrap_file],
        deploy_manifest_lines = [
            "Premain-Class: " + premain_class + "Bootstrap",
            "Can-Redefine-Classes: true",
            "Can-Retransform-Classes: true",
        ],
        main_class = "NotSuitableAsAMain",
        deps = [
            "@google_bazel_common//third_party/java/jsr250_annotations",
        ],
        runtime_deps = [":" + library_name] + extra_runtime_deps,
        visibility = ["//visibility:private"],
        compatible_with = compatible_with,
        resources = resources,
        **kwds
    )

    native.genrule(
        name = library_name + "_asm_rename",
        srcs = [library_name + "_internal_deploy.jar"],
        outs = [library_name + "_internal_2_deploy.jar"],
        cmd = "$(location @bazel_tools//third_party/jarjar:jarjar_bin) process $(location :jarjar_rules) '$<' '$@'",
        tools = [
            ":jarjar_rules",
            "@bazel_tools//third_party/jarjar:jarjar_bin",
        ],
        visibility = ["//visibility:private"],
        compatible_with = compatible_with,
        **kwds
    )

    native.genrule(
        name = library_name + "_jar_strip",
        srcs = [library_name + "_internal_2_deploy.jar"],
        outs = [deploy_jar_name],
        cmd = "absolutify() { [[ \"$$1\" =~ ^/ ]] && echo \"$$1\" || echo \"$$PWD/$$1\"; }; " +
              "JAR=`absolutify $(JAVABASE)/bin/jar`; " +
              "OUT=`absolutify $@`; " +
              "IN=`absolutify $<`; " +
              "TMPDIR=$$(mktemp -d $(@D)/tmp.XXX) && " +
              "cd $${TMPDIR} && " +
              "$${JAR} xf $${IN} &&" +
              "rm -rf $$(find . -name module-info\\.class) &&" +
              "$${JAR} cfm $${OUT} META-INF/MANIFEST.MF $$(find . -type f \\( -not -regex \".com/google/build/Data.class\" -and -not -regex \".*build-data\\.properties\" \\) )",
        toolchains = ["@bazel_tools//tools/jdk:current_host_java_runtime"],
        visibility = ["//visibility:public"],
        compatible_with = compatible_with,
        **kwds
    )
