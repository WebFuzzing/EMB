
#NAME,TYPE,LANGUAGE,FILES,LOCS,DATABASE,LICENSE,ENDPOINTS,URL
DATA_FILE="./data.csv"

oldLatexTable <- function(){

  dt <- read.csv(DATA_FILE,header=T)

  dt = dt[order(dt$TYPE, dt$LANGUAGE, -dt$LOCS, dt$NAME),]

  TABLE = "./statistics_table_emb.tex"
  unlink(TABLE)
  sink(TABLE, append = TRUE, split = TRUE)

  cat("\\begin{tabular}{lll rr ll}\\\\ \n")
  cat("\\toprule \n")
  cat("SUT & Type & Language & \\#Files & \\#LOCs & Database & URL \\\\ \n")
  cat("\\midrule \n")

  for (i in 1:nrow(dt)){

    row = dt[i,]
    cat("\\emph{",row$NAME,"}",sep="")

    cat(" & ", row$TYPE)
    cat(" & ", row$LANGUAGE)
    cat(" & ", row$FILES)
    cat(" & ", row$LOCS)

    databases = gsub(";", ", ", row$DATABASE)
    cat(" & ", databases)

    url = row$URL
    if(url == "UNDEFINED"){
      cat(" & - ")
    } else {
      cat(" & \\url{", url,"}",sep="")
    }

    cat(" \\\\ \n")
  }

  cat("\\midrule \n")
  cat("Total",nrow(dt))
  cat(" & & & ")
  cat(sum(dt$FILES))
  cat(" & ")
  cat(sum(dt$LOCS))
  cat(" & ")
  cat(length(dt$DATABASE[dt$DATABASE != ""]))
  cat(" & ")
  cat(" \\\\ \n")

  cat("\\bottomrule \n")
  cat("\\end{tabular} \n")

  sink()
}