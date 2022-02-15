#!/bin/sh

set -o nounset

http GET ${SERVER:-:8080}/v5/graphql?query='{aboutMe{id}}' Authorization:"${AUTHTOKEN:-DUMMY}" | jq -er '.data.aboutMe.id'
