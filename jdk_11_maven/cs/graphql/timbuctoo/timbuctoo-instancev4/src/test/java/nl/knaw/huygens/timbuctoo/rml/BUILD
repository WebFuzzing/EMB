load("//timbuctoo-instancev4:unittests.bzl", "junit_suite_test")

junit_suite_test(
    name = "rml",
    size = "small",
    srcs = glob(["**/*.java"]),
    deps = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/rml",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/util",
      "//third_party:com_google_guava_guava",
    ],
)
