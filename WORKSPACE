load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
   name = "google_bazel_common",
   strip_prefix = "bazel-common-340a5edaf011f76568a6351984e090a8b202ebd6",
   urls = ["https://github.com/google/bazel-common/archive/340a5edaf011f76568a6351984e090a8b202ebd6.zip"],
   sha256 = "22bfc8de051be2f3c9f64fecb6d3ca195c49bdd7edb983f74b2c481ab604bf8b",
)

load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")

google_common_workspace_rules()
