#!/bin/bash

timbuctoo_uri=${timbuctoo_uri:-http://localhost:8080}
authorization=${authorization:-DUMMY}

datasetId=${datasetId:-dataset_$RANDOM}

if ! [ -e "$1" ]; then
  echo "You should provide a valid rdf file as the first argument"
  exit 1
fi

if [ -z "$2" ]; then
  echo "You should provide the mimetype of the file as the second argument"
  exit 1
fi

echo uploading to $datasetId

time curl --request POST \
  --url "${timbuctoo_uri}/v5/u33707283d426f900d4d33707283d426f900d4d0d/${datasetId}/upload/rdf?forceCreation=true" \
  --header "authorization: $authorization" \
  --header 'content-type: multipart/form-data; boundary=---011000010111000001101001' \
  --form encoding=UTF-8 \
  --form file=@"${1}" \
  --form fileMimeTypeOverride="$2" \
  --form baseUri="http://example.com/$(basename "${1%.*}")/base/" \
  --form defaultGraph="http://example.com/$(basename "${1%.*}")/graph/"

cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

du -sh ../../timbuctoo-instancev4/temp_for_debugrun/datasets/u33707283d426f900d4d33707283d426f900d4d0d/${datasetId}
