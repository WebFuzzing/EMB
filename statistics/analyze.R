
#EMB,NAME,TYPE,LANGUAGE,RUNTIME,BUILD,FILES,LOCS,DATABASE,LICENSE,ENDPOINTS,URL
DATA_FILE="./data.csv"

UNDEFINED = "UNDEFINED"

handleMultiValues <- function(s){
  return(gsub(";", ", ", s))
}

### return a boolean vector, where each position in respect to x is true if that element appear in y
areInTheSubset <- function(x,y){

  ### first consider vector with all FALSE
  result = x!=x
  for(k in y){
    result = result | x==k
  }
  return(result)
}


markdown <- function (){

  dt <- read.csv(DATA_FILE,header=T)

  dt = dt[order(dt$TYPE, dt$LANGUAGE, -dt$LOCS, dt$NAME),]
  # skip industrial APIs that are not stored in EMB
  dt = dt[dt$EMB==TRUE,]

  TABLE = "./table_emb.md"
  unlink(TABLE)
  sink(TABLE, append = TRUE, split = TRUE)

  #EMB,NAME,TYPE,LANGUAGE,RUNTIME,BUILD,FILES,LOCS,DATABASE,LICENSE,ENDPOINTS,AUTHENTICATION,URL
  cat("|Name|Type|#LOCs|#SourceFiles|#Endpoints|Language(s)|Runtime|Build Tool|Database(s)|Authentication|\n")
  ## Note: the ":" are used for alignment of the columns
  cat("|----|----|----:|-----------:|---------:|-----------|-------|----------|-----------|:------------:|\n")

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

    if(row$AUTHENTICATION){
        cat("&check;")
    }
    cat("|")


    cat("\n")
  }

  sink()
}


latex <- function(TABLE,SUTS){

  # TODO what columns to include further could be passed as boolean selection.
  # will implement when needed

  dt <- read.csv(DATA_FILE,header=T)
  dt = dt[areInTheSubset(dt$NAME,SUTS),]
  dt = dt[order(dt$NAME),]

  unlink(TABLE)
  sink(TABLE, append = TRUE, split = TRUE)

  cat("\\begin{tabular}{l rrr}\\\\ \n")
  cat("\\toprule \n")
  cat("SUT & \\#SourceFiles & \\#LOCs & \\#Enbdpoints \\\\ \n")
  cat("\\midrule \n")

  for (i in 1:nrow(dt)){

    row = dt[i,]
    cat("\\emph{",row$NAME,"}",sep="")

    cat(" & ", row$FILES)
    cat(" & ", row$LOCS)
    cat(" & ", row$ENDPOINTS)

    cat(" \\\\ \n")
  }

  cat("\\midrule \n")
  cat("Total",nrow(dt))
  cat(" & ")
  cat(sum(dt$FILES))
  cat(" & ")
  cat(sum(dt$LOCS))
  cat(" & ")
  cat(sum(dt$ENDPOINTS))
  cat(" \\\\ \n")

  cat("\\bottomrule \n")
  cat("\\end{tabular} \n")

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