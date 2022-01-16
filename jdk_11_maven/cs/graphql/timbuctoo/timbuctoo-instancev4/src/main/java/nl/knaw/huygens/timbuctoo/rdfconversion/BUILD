java_binary(
    name = "rdfconversion",
    srcs = glob([
        "**/*.java",
    ]),
    deps = [
      "//third_party:org_eclipse_rdf4j_rdf4j_model",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_api",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_nquads",
      "//third_party:org_eclipse_rdf4j_rdf4j_rio_turtle",
    ],
    visibility = ["//visibility:public"],
    main_class = "nl.knaw.huygens.timbuctoo.rdfconversion.RdfConverter"
)
