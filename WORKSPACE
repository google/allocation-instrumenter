load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
   name = "google_bazel_common",
   strip_prefix = "bazel-common-a3e5603614543c761b6bc96d2da452d9bfd58c0f",
   urls = ["https://github.com/google/bazel-common/archive/a3e5603614543c761b6bc96d2da452d9bfd58c0f.zip"],
   sha256 = "8b6ed188fc48f973420476994abd582f9ef8442009689e8858153cbe4c5d08b5",
)

load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")

google_common_workspace_rules()
