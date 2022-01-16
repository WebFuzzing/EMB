#!/usr/bin/env bash

set -o nounset

echo "Uploading $1 to $DATASETID";
if [ -z "${USERID:-}" ]; then
  USERID=$(./get-userid.sh)
  if [ $? != 0 ]; then
    echo "Not authenticated"
    exit 1
  fi
fi


http --timeout=3600 --form POST "${SERVER:-:8080}/v5/u${USERID}/${DATASETID}/upload/rdf?forceCreation=true" authorization:"${AUTHTOKEN:-DUMMY}" encoding=UTF-8 fileMimeTypeOverride=${MIMETYPE:-application/ld+json} file@"$1"
