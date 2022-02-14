load("//timbuctoo-instancev4:unittests.bzl", "junit_suite_test")

junit_suite_test(
    name = "security",
    size = "small",
    srcs = glob(["**/*.java"]),
    resources = [
      "//timbuctoo-instancev4/src/test/resources/nl/knaw/huygens/timbuctoo/security:test_requirements"
    ],
    deps = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/security",
      "//third_party:com_fasterxml_jackson_core_jackson_core",
      "//third_party:com_fasterxml_jackson_core_jackson_databind",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/util",
      "//security-client-agnostic",
      "//third_party:com_microsoft_azure_azure_storage",
      "//third_party:com_google_guava_guava",
    ],
)
