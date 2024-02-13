source("analyze.R")

suts <- function(){

  # This generated table should be added to Git, as will change based on papers
  TABLE = "./table_suts.tex"

  #Names here should match what in data.csv
  SUTS = c(
    "catwatch",
    "cwa-verification",
    "features-service",
    "gestaohospital",
    "languagetool",
    "ocvn",
    "proxyprint",
    "rest-ncs",
    "rest-news",
    "rest-scs",
    "restcountries",
    "scout-api",
    "genome-nexus",
    "market",
    "bibliothek",
    "reservations-api",
    "session-service",
    #"petclinic-graphql",
    #"patio-api",
    #"timbuctoo",
    #"graphql-ncs",
    #"graphql-scs",
    #"thrift-ncs",
    #"thrift-scs",
    #"grpc-ncs",
    #"grpc-scs",
    #"signal-registration",
    "ind0",
    #"ind1",
    ""
  )

  latex(TABLE,SUTS)
}

