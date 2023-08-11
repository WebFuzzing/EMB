
#EMB,NAME,TYPE,LANGUAGE,RUNTIME,BUILD,FILES,LOCS,DATABASE,LICENSE,ENDPOINTS,URL
DATA_FILE="./data.csv"

UNDEFINED = "UNDEFINED"

handleMultiValues <- function(s){
  return(gsub(";", ", ", s))
}

markdown <- function (){

  dt <- read.csv(DATA_FILE,header=T)

  dt = dt[order(dt$TYPE, dt$LANGUAGE, -dt$LOCS, dt$NAME),]
  # skip industrial APIs that are not stored in EMB
  dt = dt[dt$EMB==TRUE,]

  TABLE = "./table_emb.md"
  unlink(TABLE)
  sink(TABLE, append = TRUE, split = TRUE)

  #EMB,NAME,TYPE,LANGUAGE,RUNTIME,BUILD,FILES,LOCS,DATABASE,LICENSE,ENDPOINTS,URL
  cat("|Name|Type|#LOCs|#SourceFiles|#Endpoints|Language(s)|Runtime|Build Tool|Database(s)|\n")
  cat("|----|----|----:|-----------:|---------:|-----------|-------|----------|-----------|\n")

  for (i in 1:nrow(dt)){

    row = dt[i,]
    cat("|__",row$NAME,"__|",sep="")

    cat(row$TYPE,"|",sep="")
    cat(row$LOCS,"|",sep="")
    cat(row$FILES,"|",sep="")
    cat(row$ENDPOINTS,"|",sep="")
    cat(handleMultiValues(row$LANGUAGE),"|",sep="")
    cat(row$RUNTIME,"|",sep="")
    cat(row$BUILD,"|",sep="")
    cat(handleMultiValues(row$DATABASE),"|",sep="")
    cat("\n")
  }

  sink()
}


oldLatexTable <- function(){

  dt <- read.csv(DATA_FILE,header=T)

  dt = dt[order(dt$TYPE, dt$LANGUAGE, -dt$LOCS, dt$NAME),]

  TABLE = "./old_statistics_table_emb.tex"
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