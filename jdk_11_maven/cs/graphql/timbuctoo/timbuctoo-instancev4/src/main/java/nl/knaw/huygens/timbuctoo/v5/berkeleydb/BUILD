#java_plugin(
#    name = "immutables_processor",
#    deps = [
#        "//third_party:org_immutables_value",
#    ],
#    processor_class = "org.immutables.processor.ProxyProcessor",
#    generates_api=True, # we use JsonDeserialize as
#)

java_library(
    name = "berkeleydb",
    plugins = [
      ":immutables_processor"
    ],
    srcs = glob([
        "**/*.java",
    ]),
    deps = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/util",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/filehelper",

      "//third_party:com_fasterxml_jackson_core_jackson_annotations",
      "//third_party:org_apache_commons_commons_lang3",
      "//third_party:com_sleepycat_je",
      "//third_party:org_slf4j_slf4j_api",
    ],
    visibility = ["//visibility:public"],
)
