load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
   name = "google_bazel_common",
   strip_prefix = "bazel-common-d4ada735afa0ab044957cfa21849be577756a6cd",
   urls = ["https://github.com/google/bazel-common/archive/d4ada735afa0ab044957cfa21849be577756a6cd.zip"],
   sha256 = "0ba40405bc4cc095dd1ace08d145fe238798388f26c4ad0725e801b7e16e0f27",
)

load("@google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")

google_common_workspace_rules()
