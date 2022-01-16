#!/bin/bash

# ussage: ./undo_rml.sh <userid> <datasetid> 

cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

cd "$(git rev-parse --show-toplevel)" #go to git root

DATA_SET_PATH="timbuctoo-instancev4/temp_for_debugrun/datasets/$1/$2/files"

FILE_NAME=`more $DATA_SET_PATH/fileList.json | jq '.[]|keys_unsorted|.[-1]'`

FILE_TO_UNDO="${FILE_NAME//\"}"
UNDO_FILE="${FILE_TO_UNDO}_undo.nqud"

cat "$DATA_SET_PATH/$FILE_TO_UNDO" | sed 's/^/-/' > $UNDO_FILE

curl -v -F "file=@$UNDO_FILE;type=application/vnd.timbuctoo-rdf.nquads_unified_diff" -F "encoding=UTF-8" -H "Authorization: fake" http://localhost:8080/v5/$1/$2/upload/rdf?forceCreation=true

rm $UNDO_FILE
