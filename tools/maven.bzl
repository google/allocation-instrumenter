"""Macros to simplify generating maven files.
"""

load("@google_bazel_common//tools/maven:pom_file.bzl", default_pom_file = "pom_file")

def pom_file(name, targets, **kwargs):
    default_pom_file(
        name = name,
        targets = targets,
        preferred_group_ids = [
            "com.google.code.java-allocation-instrumenter",
            "com.google",
        ],
        template_file = "//tools:pom-template.xml",
        **kwargs
    )
