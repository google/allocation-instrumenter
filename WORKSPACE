load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
   name = "google_bazel_common",
   strip_prefix = "bazel-common-a9e1d8efd54cbf27249695b23775b75ca65bb59d",
   urls = ["https://github.com/google/bazel-common/archive/a9e1d8efd54cbf27249695b23775b75ca65bb59d.zip"],
   sha256 = "e30e092e50c47a38994334dbe42386675cf519a5e86b973e45034323bbdb70a3",
)

load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")

google_common_workspace_rules()
