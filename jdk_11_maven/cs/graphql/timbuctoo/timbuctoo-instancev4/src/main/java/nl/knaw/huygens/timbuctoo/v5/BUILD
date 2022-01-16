java_plugin(
    name = "immutables_processor",
    deps = [
        "//third_party:org_immutables_value",
    ],
    processor_class = "org.immutables.processor.ProxyProcessor",
    generates_api=True, # we use JsonDeserialize as
)

java_library(
    name = "v5",
    plugins = [
      ":immutables_processor"
    ],
    resources = [
      "//timbuctoo-instancev4/src/main/resources/nl/knaw/huygens/timbuctoo/v5/graphql/rootquery:graphqlfiles"
    ],
    srcs = glob([
        "**/*.java",
    ]),
    runtime_deps = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/backupforstaging/azureblob/dropwizardconfiguration",
    ],
    exports = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/berkeleydb",
    ],
    deps = [
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/backupforstaging",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/filehelper",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/v5/berkeleydb",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/util",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/rml",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/rml/jena",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/rml/datasource/jexl",
      "//timbuctoo-instancev4/src/main/java/nl/knaw/huygens/timbuctoo/bulkupload",
      "//third_party:com_javaslang_javaslang",
      "//third_party:com_github_jsonld_java_jsonld_java",
      "//third_party:org_apache_httpcomponents_httpclient",
      "//third_party:org_apache_httpcomponents_httpcore",
      "//third_party:org_apache_httpcomponents_httpcore_nio",
      "//third_party:org_apache_httpcomponents_httpasyncclient",
      "//third_party:org_elasticsearch_client_rest",
      "//third_party:org_apache_jena_jena_core",
      "//third_party:com_google_code_findbugs_jsr305",
      "//third_party:org_apache_commons_commons_csv",
      "//third_party:io_dropwizard_dropwizard_jersey",
      "//third_party:io_dropwizard_dropwizard_lifecycle",
      "//third_party:com_fasterxml_jackson_core_jackson_annotations",
      "//third_party:com_fasterxml_jackson_core_jackson_core",
      "//third_party:com_fasterxml_jackson_core_jackson_databind",
      "//third_party:com_fasterxml_jackson_datatype_jackson_datatype_jdk8",
      "//third_party:com_fasterxml_jackson_datatype_jackson_datatype_guava",
      "//third_party:io_dropwizard_dropwizard_validation",
      "//third_party:io_dropwizard_dropwizard_servlets",
      "//third_party:javax_ws_rs_javax_ws_rs_api",
      "//third_party:com_googlecode_juniversalchardet_juniversalchardet",
      "//third_party:org_eclipse_rdf4j_rdf4j_model",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_api",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_jsonld",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_n3",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_nquads",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_ntriples",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_turtle",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_rdfxml",
      "//third_party:org_eclipse_rdf4j_rdf4j_util",
      "//third_party:org_glassfish_jersey_core_jersey_common",
      "//third_party:org_glassfish_jersey_media_jersey_media_multipart",
      "//third_party:com_graphql_java_graphql_java",
      "//third_party:commons_io_commons_io",
      "//third_party:org_apache_commons_commons_lang3",
      "//third_party:com_sleepycat_je",
      "//third_party:org_immutables_value",
      "//third_party:com_google_guava_guava",
      "//third_party:org_slf4j_slf4j_api",
    ],
    visibility = ["//visibility:public"],
)
