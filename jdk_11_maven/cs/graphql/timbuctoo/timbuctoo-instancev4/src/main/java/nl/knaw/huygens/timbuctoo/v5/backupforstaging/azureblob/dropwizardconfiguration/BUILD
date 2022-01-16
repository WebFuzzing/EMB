java_library(
    name = "dropwizardconfiguration",
    srcs = glob([
        "**/*.java",
    ]),
    deps = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/backupforstaging/azureblob",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/backupforstaging",
      "//third_party:com_fasterxml_jackson_core_jackson_annotations",
      "//third_party:com_microsoft_azure_azure_storage",
#      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/dropwizard",
    ],
    exports = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/backupforstaging/azureblob",
    ],
    visibility = ["//visibility:public"],
)
