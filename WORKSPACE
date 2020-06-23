load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
   name = "google_bazel_common",
   strip_prefix = "bazel-common-e768dbfea5bac239734b3f59b2a1d7464c6dbd26",
   urls = ["https://github.com/google/bazel-common/archive/e768dbfea5bac239734b3f59b2a1d7464c6dbd26.zip"],
   sha256 = "17f66ba76073a290add024a4ce7f5f92883832b7da85ffd7677e1f5de9a36153",
)

load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")

google_common_workspace_rules()
