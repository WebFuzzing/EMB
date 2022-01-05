java_plugin(
    name = "immutables_processor",
    deps = [
        "//third_party:org_immutables_value",
    ],
    processor_class = "org.immutables.processor.ProxyProcessor",
    generates_api=True, # we use JsonDeserialize as
)

java_library(
    name = "security",
    plugins = [
      ":immutables_processor"
    ],
    srcs = glob([
        "**/*.java",
    ]),
    deps = [
      "//security-client-agnostic:security-client-agnostic",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/util",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5",
      "//third_party:org_immutables_value",
      "//third_party:com_google_guava_guava",
      "//third_party:com_google_code_findbugs_jsr305",
      "//third_party:com_microsoft_azure_azure_storage",
      "//third_party:org_slf4j_slf4j_api",
      "//third_party:com_fasterxml_jackson_core_jackson_annotations",
      "//third_party:com_fasterxml_jackson_core_jackson_databind",
      "//third_party:com_fasterxml_jackson_core_jackson_core",
      "//third_party:javax_validation_validation_api",
      "//third_party:com_fasterxml_jackson_datatype_jackson_datatype_guava",
    ],
    visibility = ["//visibility:public"],
)
