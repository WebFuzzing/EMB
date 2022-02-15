#java_plugin(
#    name = "immutables_processor",
#    deps = [
#        "//third_party:org_immutables_value",
#    ],
#    processor_class = "org.immutables.processor.ProxyProcessor",
#)

java_library(
    name = "bulkupload",
#    plugins = [
#      ":immutables_processor"
#    ],
    srcs = glob([
        "**/*.java",
    ]),
    deps = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/util",
      "//third_party:com_fasterxml_jackson_core_jackson_annotations",
      "//third_party:org_apache_poi_poi_ooxml_schemas",
      "//third_party:org_apache_poi_poi_ooxml",
      "//third_party:org_apache_poi_poi",
      "//third_party:com_github_DANS_KNAW_dans_dp_lib",
      "//third_party:com_healthmarketscience_jackcess_jackcess",
      "//third_party:org_apache_commons_commons_csv",
      "//third_party:com_google_guava_guava",
      "//third_party:org_slf4j_slf4j_api",
    ],
    visibility = ["//visibility:public"],
)
